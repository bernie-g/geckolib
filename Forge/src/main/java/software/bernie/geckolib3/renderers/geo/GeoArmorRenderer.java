package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.ApiStatus.AvailableSince;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.controller.AnimationController.ModelFetcher;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.GeoUtils;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class GeoArmorRenderer<T extends ArmorItem & IAnimatable> extends HumanoidModel
		implements IGeoRenderer<T>, ModelFetcher<T> {
	protected static Map<Class<? extends ArmorItem>, Supplier<GeoArmorRenderer>> CONSTRUCTORS = new ConcurrentHashMap<>();
	public static Map<Class<? extends ArmorItem>, ConcurrentHashMap<UUID, GeoArmorRenderer<?>>> LIVING_ENTITY_RENDERERS = new ConcurrentHashMap<>();
	// Rename this in breaking change to ARMOR_ITEM_RENDERERS

	protected Class<? extends ArmorItem> assignedItemClass = null;

	protected T currentArmorItem;
	protected LivingEntity entityLiving;
	protected ItemStack itemStack;
	protected EquipmentSlot armorSlot;
	protected float widthScale = 1;
	protected float heightScale = 1;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();

	// Set these to the names of your armor's bones, or null if you aren't using
	// them
	public String headBone = "armorHead";
	public String bodyBone = "armorBody";
	public String rightArmBone = "armorRightArm";
	public String leftArmBone = "armorLeftArm";
	public String rightLegBone = "armorRightLeg";
	public String leftLegBone = "armorLeftLeg";
	public String rightBootBone = "armorRightBoot";
	public String leftBootBone = "armorLeftBoot";

	private final AnimatedGeoModel<T> modelProvider;

	protected MultiBufferSource rtb = null;

	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	{
		AnimationController.addModelFetcher(this);
	}

	@Override
	@Nullable
	public IAnimatableModel<T> apply(IAnimatable t) {
		if (t instanceof ArmorItem && t.getClass() == this.assignedItemClass)
			return this.getGeoModelProvider();

		return null;
	}

	/**
	 * Use {@link GeoArmorRenderer#registerArmorRenderer(Class, Supplier)}
	 * @param itemClass
	 * @param renderer
	 */
	@Deprecated(forRemoval = true)
	public static void registerArmorRenderer(Class<? extends ArmorItem> itemClass, GeoArmorRenderer renderer) {
		for (Constructor<?> constructor : renderer.getClass().getConstructors()) {
			if (constructor.getParameterCount() == 0) {
				registerArmorRenderer(itemClass, () -> {
					try {
						return (GeoArmorRenderer)constructor.newInstance();
					}
					catch (Exception e) {
						e.printStackTrace();
					}

					return null;
				});
			}
			else {
				GeckoLib.LOGGER.error(
						"Registration of armor renderer for item class {} failed cause the renderer class {} does not feature a zero-args constructor!",
						itemClass.getName(), renderer.getClass().getName());
				throw new IllegalArgumentException(
						"If you still use the registration using instances, please give it a no-args constructor!");
			}
		}
	}

	public static void registerArmorRenderer(Class<? extends ArmorItem> itemClass,
			Supplier<GeoArmorRenderer> rendererConstructor) {
		CONSTRUCTORS.put(itemClass, rendererConstructor);
		LIVING_ENTITY_RENDERERS.put(itemClass, new ConcurrentHashMap<>());
	}

	/**
	 * Use {@link GeoArmorRenderer#getRenderer(Class, Entity)}
	 * Remove at some point unless a use is found for it
	 */
	@Deprecated(forRemoval = true)
	public static GeoArmorRenderer getRenderer(Class<? extends ArmorItem> item, final Entity wearer,
											   boolean forExtendedEntity) {
		return getRenderer(item, wearer);
	}

	public static GeoArmorRenderer getRenderer(Class<? extends ArmorItem> item, final Entity wearer) {
		ConcurrentHashMap<UUID, GeoArmorRenderer<?>> renderers = LIVING_ENTITY_RENDERERS.get(item);
		GeoArmorRenderer armorRenderer;
		UUID uuid = wearer.getUUID();

		if (renderers == null || (armorRenderer = renderers.get(uuid)) == null) {
			armorRenderer = CONSTRUCTORS.get(item).get();

			if (armorRenderer == null)
				throw new IllegalArgumentException("Renderer not registered for item " + item);

			armorRenderer.assignedItemClass = item;

			if (renderers == null) {
				renderers = new ConcurrentHashMap<>();

				LIVING_ENTITY_RENDERERS.put(item, renderers);
			}

			renderers.put(uuid, armorRenderer);
		}

		return armorRenderer;
	}

	public GeoArmorRenderer(AnimatedGeoModel<T> modelProvider) {
		super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));

		this.modelProvider = modelProvider;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		this.render(0, poseStack, buffer, packedLight);
	}

	public void render(float partialTick, PoseStack poseStack, VertexConsumer buffer, int packedLight) {
		GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(this.currentArmorItem));
		AnimationEvent animationEvent = new AnimationEvent(this.currentArmorItem, 0, 0,
				Minecraft.getInstance().getFrameTime(), false,
				Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));

		poseStack.pushPose();
		poseStack.translate(0, 24 / 16F, 0);
		poseStack.scale(-1, -1, 1);

		this.dispatchedMat = poseStack.last().pose().copy();

		this.modelProvider.setLivingAnimations(this.currentArmorItem, getInstanceId(this.currentArmorItem), animationEvent); // TODO change to setCustomAnimations in 1.20+
		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		fitToBiped();
		RenderSystem.setShaderTexture(0, getTextureLocation(this.currentArmorItem));

		Color renderColor = getRenderColor(this.currentArmorItem, partialTick, poseStack, null, buffer, packedLight);
		RenderType renderType = getRenderType(this.currentArmorItem, partialTick, poseStack, null, buffer, packedLight,
				getTextureLocation(this.currentArmorItem));

		render(model, this.currentArmorItem, partialTick, renderType, poseStack, null, buffer, packedLight,
				OverlayTexture.NO_OVERLAY, renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
				renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);

		if (ModList.get().isLoaded("patchouli"))
			PatchouliCompat.patchouliLoaded(poseStack);

		poseStack.popPose();
	}

	@Override
	public void renderEarly(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
							VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
							float alpha) {
		this.renderEarlyMat = poseStack.last().pose().copy();
		this.currentArmorItem = animatable;

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer,
				packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose();
			Vec3 renderOffset = getRenderOffset(this.currentArmorItem, 1);
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.translate(new Vector3f(renderOffset));
			bone.setLocalSpaceXform(localMatrix);
		}

		IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	public Vec3 getRenderOffset(T entity, float partialTick) {
		return Vec3.ZERO;
	}

	protected void fitToBiped() {
		if (this.headBone != null) {
			IBone headBone = this.modelProvider.getBone(this.headBone);

			GeoUtils.copyRotations(this.head, headBone);
			headBone.setPositionX(this.head.x);
			headBone.setPositionY(-this.head.y);
			headBone.setPositionZ(this.head.z);
		}

		if (this.bodyBone != null) {
			IBone bodyBone = this.modelProvider.getBone(this.bodyBone);

			GeoUtils.copyRotations(this.body, bodyBone);
			bodyBone.setPositionX(this.body.x);
			bodyBone.setPositionY(-this.body.y);
			bodyBone.setPositionZ(this.body.z);
		}

		if (this.rightArmBone != null) {
			IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);

			GeoUtils.copyRotations(this.rightArm, rightArmBone);
			rightArmBone.setPositionX(this.rightArm.x + 5);
			rightArmBone.setPositionY(2 - this.rightArm.y);
			rightArmBone.setPositionZ(this.rightArm.z);
		}

		if (this.leftArmBone != null) {
			IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);

			GeoUtils.copyRotations(this.leftArm, leftArmBone);
			leftArmBone.setPositionX(this.leftArm.x - 5);
			leftArmBone.setPositionY(2 - this.leftArm.y);
			leftArmBone.setPositionZ(this.leftArm.z);
		}

		if (this.rightLegBone != null) {
			IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);

			GeoUtils.copyRotations(this.rightLeg, rightLegBone);
			rightLegBone.setPositionX(this.rightLeg.x + 2);
			rightLegBone.setPositionY(12 - this.rightLeg.y);
			rightLegBone.setPositionZ(this.rightLeg.z);

			if (this.rightBootBone != null) {
				IBone rightBootBone = this.modelProvider.getBone(this.rightBootBone);

				GeoUtils.copyRotations(this.rightLeg, rightBootBone);
				rightBootBone.setPositionX(this.rightLeg.x + 2);
				rightBootBone.setPositionY(12 - this.rightLeg.y);
				rightBootBone.setPositionZ(this.rightLeg.z);
			}
		}

		if (this.leftLegBone != null) {
			IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);

			GeoUtils.copyRotations(this.leftLeg, leftLegBone);
			leftLegBone.setPositionX(this.leftLeg.x - 2);
			leftLegBone.setPositionY(12 - this.leftLeg.y);
			leftLegBone.setPositionZ(this.leftLeg.z);

			if (this.leftBootBone != null) {
				IBone leftBootBone = this.modelProvider.getBone(this.leftBootBone);

				GeoUtils.copyRotations(this.leftLeg, leftBootBone);
				leftBootBone.setPositionX(this.leftLeg.x - 2);
				leftBootBone.setPositionY(12 - this.leftLeg.y);
				leftBootBone.setPositionZ(this.leftLeg.z);
			}
		}
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getWidthScale(T animatable) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public ResourceLocation getTextureLocation(T animatable) {
		return this.modelProvider.getTextureLocation(animatable);
	}

	/**
	 * Everything after this point needs to be called every frame before rendering
	 */
	public GeoArmorRenderer setCurrentItem(LivingEntity entity, ItemStack itemStack, EquipmentSlot armorSlot) {
		this.entityLiving = entity;
		this.itemStack = itemStack;
		this.armorSlot = armorSlot;
		this.currentArmorItem = (T)itemStack.getItem();

		return this;
	}

	public final GeoArmorRenderer applyEntityStats(HumanoidModel defaultArmor) {
		this.young = defaultArmor.young;
		this.crouching = defaultArmor.crouching;
		this.riding = defaultArmor.riding;
		this.rightArmPose = defaultArmor.rightArmPose;
		this.leftArmPose = defaultArmor.leftArmPose;

		return this;
	}

	public GeoArmorRenderer applySlot(EquipmentSlot slot) {
		this.modelProvider.getModel(this.modelProvider.getModelLocation(this.currentArmorItem));

		setBoneVisibility(this.headBone, false);
        setBoneVisibility(this.bodyBone, false);
        setBoneVisibility(this.rightArmBone, false);
        setBoneVisibility(this.leftArmBone, false);
        setBoneVisibility(this.rightLegBone, false);
        setBoneVisibility(this.leftLegBone, false);
        setBoneVisibility(this.rightBootBone, false);
        setBoneVisibility(this.rightBootBone, false);
        setBoneVisibility(this.leftBootBone, false);
		
		switch (slot) {
			case HEAD -> setBoneVisibility(this.headBone, true);
			case CHEST -> {
				setBoneVisibility(this.bodyBone, true);
				setBoneVisibility(this.rightArmBone, true);
				setBoneVisibility(this.leftArmBone, true);
			}
			case LEGS -> {
				setBoneVisibility(this.rightLegBone, true);
				setBoneVisibility(this.leftLegBone, true);
			}
			case FEET -> {
				setBoneVisibility(this.rightBootBone, true);
				setBoneVisibility(this.rightBootBone, true);
				setBoneVisibility(this.leftBootBone, true);
			}
			default -> {}
		}

		return this;
	}

	/**
	 * Sets a specific bone (and its child-bones) to visible or not
	 * @param boneName The name of the bone
	 * @param isVisible Whether the bone should be visible
	 */
	protected void setBoneVisibility(String boneName, boolean isVisible) {
		if (boneName == null)
			return;

		this.modelProvider.getBone(boneName).setHidden(!isVisible);
	}

	/**
	 * Use {@link GeoArmorRenderer#setBoneVisibility(String, boolean)}
	 */
	@Deprecated(forRemoval = true)
	protected IBone getAndHideBone(String boneName) {
		setBoneVisibility(boneName, false);

		return this.modelProvider.getBone(boneName);
	}

	/**
	 * Use {@link IGeoRenderer#getInstanceId(Object)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public Integer getUniqueID(T animatable) {
		return getInstanceId(animatable);
	}

	@Override
	public int getInstanceId(T animatable) {
		return Objects.hash(this.armorSlot, this.itemStack.getItem(), this.itemStack.getCount(),
				this.itemStack.hasTag() ? this.itemStack.getTag().toString() : 1, this.entityLiving.getUUID().toString());
	}

	@Override
	public void setCurrentRTB(MultiBufferSource bufferSource) {
		this.rtb = bufferSource;
	}

	@Override
	public MultiBufferSource getCurrentRTB() {
		return this.rtb;
	}
}
