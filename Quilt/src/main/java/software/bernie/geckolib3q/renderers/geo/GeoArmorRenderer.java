package software.bernie.geckolib3q.renderers.geo;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;
import org.quiltmc.loader.api.QuiltLoader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3q.compat.PatchouliCompat;
import software.bernie.geckolib3q.geo.render.built.GeoBone;
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.model.AnimatedGeoModel;
import software.bernie.geckolib3q.util.EModelRenderCycle;
import software.bernie.geckolib3q.util.GeoUtils;
import software.bernie.geckolib3q.util.IRenderCycle;
import software.bernie.geckolib3q.util.RenderUtils;

public class GeoArmorRenderer<T extends ArmorItem & IAnimatable> implements IGeoRenderer<T>, ArmorRenderer {
	public static final Map<Class<? extends ArmorItem>, GeoArmorRenderer> renderers = new ConcurrentHashMap<>();

	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof ArmorItem) {
				GeoArmorRenderer renderer = renderers.get(object.getClass());
				return renderer == null ? null : renderer.getGeoModelProvider();
			}
			return null;
		});
	}

	protected T currentArmorItem;
	protected LivingEntity entityLiving;
	protected ItemStack itemStack;
	protected EquipmentSlot armorSlot;
	protected HumanoidModel baseModel;
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

	private AnimatedGeoModel<T> modelProvider;

	protected MultiBufferSource rtb = null;

	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	public GeoArmorRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	public void setModel(AnimatedGeoModel<T> model) {
		this.modelProvider = model;
	}

	public static <E extends Entity> void registerArmorRenderer(GeoArmorRenderer renderer, Item... items) {
		for (Item item : items) {
			registerArmorRenderer(renderer, item);
		}
	}

	public static <E extends Entity> void registerArmorRenderer(GeoArmorRenderer renderer, Item item) {
		if (item instanceof ArmorItem) {
			renderers.put((Class<? extends ArmorItem>) item.getClass(), renderer);
			ArmorRenderer.register(renderer, item);
		}
	}

	public static GeoArmorRenderer getRenderer(Class<? extends ArmorItem> item) {
		final GeoArmorRenderer renderer = renderers.get(item);
		if (renderer == null) {
			throw new IllegalArgumentException("Renderer not registered for item " + item);
		}
		return renderer;
	}

	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity,
			EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
		setCurrentItem(entity, stack, slot, contextModel);
		this.render(matrices, vertexConsumers, light);
	}

	public void render(float partialTicks, PoseStack stack, VertexConsumer bufferIn, int packedLightIn) {
		stack.translate(0.0D, 1.497F, 0.0D);
		stack.scale(-1.005F, -1.0F, 1.005F);
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.dispatchedMat = stack.last().pose().copy();
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(currentArmorItem));

		AnimationEvent<T> itemEvent = new AnimationEvent<T>(this.currentArmorItem, 0, 0,
				Minecraft.getInstance().getFrameTime(), false,
				Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
		modelProvider.setLivingAnimations(currentArmorItem, this.getUniqueID(this.currentArmorItem), itemEvent);

		this.fitToBiped();
		this.applySlot(armorSlot);
		stack.pushPose();
		RenderSystem.setShaderTexture(0, getTextureLocation(currentArmorItem));
		Color renderColor = getRenderColor(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn);
		RenderType renderType = getRenderType(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn,
				getTextureLocation(currentArmorItem));
		render(model, currentArmorItem, partialTicks, renderType, stack, null, bufferIn, packedLightIn,
				OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);

		if (QuiltLoader.isModLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.popPose();
		stack.scale(-1.005F, -1.0F, 1.005F);
		stack.translate(0.0D, -1.497F, 0.0D);
	}

	public void render(PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
		stack.translate(0.0D, 1.497F, 0.0D);
		stack.scale(-1.005F, -1.0F, 1.005F);
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.dispatchedMat = stack.last().pose().copy();
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(currentArmorItem));

		AnimationEvent<T> itemEvent = new AnimationEvent<T>(this.currentArmorItem, 0, 0,
				Minecraft.getInstance().getFrameTime(), false,
				Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
		modelProvider.setLivingAnimations(currentArmorItem, this.getUniqueID(this.currentArmorItem), itemEvent);

		this.fitToBiped();
		this.applySlot(armorSlot);
		stack.pushPose();
		RenderSystem.setShaderTexture(0, getTextureLocation(currentArmorItem));
		Color renderColor = getRenderColor(currentArmorItem, 0, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(currentArmorItem, 0, stack, bufferIn, null, packedLightIn,
				getTextureLocation(currentArmorItem));
		render(model, currentArmorItem, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);

		if (QuiltLoader.isModLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.popPose();
		stack.scale(-1.005F, -1.0F, 1.005F);
		stack.translate(0.0D, -1.497F, 0.0D);
	}

	@Override
	public void renderEarly(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
			VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
			float alpha) {
		this.renderEarlyMat = poseStack.last().pose().copy();
		this.currentArmorItem = animatable;

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlay, red, green, blue, alpha);
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

	public Vec3 getRenderOffset(T entity, float tickDelta) {
		return Vec3.ZERO;
	}

	private void fitToBiped() {
		if (this.headBone != null) {
			IBone headBone = this.modelProvider.getBone(this.headBone);

			GeoUtils.copyRotations(baseModel.head, headBone);
			headBone.setPositionX(baseModel.head.x);
			headBone.setPositionY(-baseModel.head.y);
			headBone.setPositionZ(baseModel.head.z);
		}

		if (this.bodyBone != null) {
			IBone bodyBone = this.modelProvider.getBone(this.bodyBone);

			GeoUtils.copyRotations(baseModel.body, bodyBone);
			bodyBone.setPositionX(baseModel.body.x);
			bodyBone.setPositionY(-baseModel.body.y);
			bodyBone.setPositionZ(baseModel.body.z);
		}
		if (this.rightArmBone != null) {
			IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);

			GeoUtils.copyRotations(baseModel.rightArm, rightArmBone);
			rightArmBone.setPositionX(baseModel.rightArm.x + 5);
			rightArmBone.setPositionY(2 - baseModel.rightArm.y);
			rightArmBone.setPositionZ(baseModel.rightArm.z);
		}

		if (this.leftArmBone != null) {
			IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);

			GeoUtils.copyRotations(baseModel.leftArm, leftArmBone);
			leftArmBone.setPositionX(baseModel.leftArm.x - 5);
			leftArmBone.setPositionY(2 - baseModel.leftArm.y);
			leftArmBone.setPositionZ(baseModel.leftArm.z);
		}
		if (this.rightLegBone != null) {
			IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);

			GeoUtils.copyRotations(baseModel.rightLeg, rightLegBone);
			rightLegBone.setPositionX(baseModel.rightLeg.x + 2);
			rightLegBone.setPositionY(12 - baseModel.rightLeg.y);
			rightLegBone.setPositionZ(baseModel.rightLeg.z);
			if (this.rightBootBone != null) {
				IBone rightBootBone = this.modelProvider.getBone(this.rightBootBone);

				GeoUtils.copyRotations(baseModel.rightLeg, rightBootBone);
				rightBootBone.setPositionX(baseModel.rightLeg.x + 2);
				rightBootBone.setPositionY(12 - baseModel.rightLeg.y);
				rightBootBone.setPositionZ(baseModel.rightLeg.z);
			}
		}
		if (this.leftLegBone != null) {
			IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);

			GeoUtils.copyRotations(baseModel.leftLeg, leftLegBone);
			leftLegBone.setPositionX(baseModel.leftLeg.x - 2);
			leftLegBone.setPositionY(12 - baseModel.leftLeg.y);
			leftLegBone.setPositionZ(baseModel.leftLeg.z);
			if (this.leftBootBone != null) {
				IBone leftBootBone = this.modelProvider.getBone(this.leftBootBone);

				GeoUtils.copyRotations(baseModel.leftLeg, leftBootBone);
				leftBootBone.setPositionX(baseModel.leftLeg.x - 2);
				leftBootBone.setPositionY(12 - baseModel.leftLeg.y);
				leftBootBone.setPositionZ(baseModel.leftLeg.z);
			}
		}
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	public float getWidthScale(T entity) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.23")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureResource(instance);
	}

	@Override
	public ResourceLocation getTextureResource(T entity) {
		return this.modelProvider.getTextureResource(entity);
	}

	/**
	 * Everything after this point needs to be called every frame before rendering
	 */
	public GeoArmorRenderer<T> setCurrentItem(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot,
			HumanoidModel model) {
		this.entityLiving = entityLiving;
		this.itemStack = itemStack;
		this.armorSlot = armorSlot;
		this.currentArmorItem = (T) itemStack.getItem();
		this.baseModel = model;

		return this;
	}

	public GeoArmorRenderer<T> applySlot(EquipmentSlot slot) {
		this.modelProvider.getModel(this.modelProvider.getModelResource(this.currentArmorItem));

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
		default -> {
		}
		}

		return this;
	}

	/**
	 * Sets a specific bone (and its child-bones) to visible or not
	 * 
	 * @param boneName  The name of the bone
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
		return Objects.hash(this.armorSlot, itemStack.getItem(), itemStack.getCount(),
				itemStack.hasTag() ? itemStack.getTag().toString() : 1, this.entityLiving.getUUID().toString());
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