package software.bernie.geckolib3.renderer.geo;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.GeoUtils;

public abstract class GeoArmorRenderer<T extends ArmorItem & IAnimatable> extends BipedEntityModel
		implements IGeoRenderer<T> {
	private static final Map<Class<? extends ArmorItem>, GeoArmorRenderer> renderers = new ConcurrentHashMap<>();

	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof ArmorItem) {
				GeoArmorRenderer renderer = renderers.get(object.getClass());
				return renderer == null ? null : renderer.getGeoModelProvider();
			}
			return null;
		});
	}

	private final AnimatedGeoModel<T> modelProvider;
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
	protected T currentArmorItem;
	protected LivingEntity entityLiving;
	protected ItemStack itemStack;
	protected EquipmentSlot armorSlot;

	public GeoArmorRenderer(AnimatedGeoModel<T> modelProvider) {
		super(1);
		this.modelProvider = modelProvider;
	}

	public static void registerArmorRenderer(Class<? extends ArmorItem> itemClass, GeoArmorRenderer renderer) {
		renderers.put(itemClass, renderer);
	}

	public static GeoArmorRenderer getRenderer(Class<? extends ArmorItem> item) {
		final GeoArmorRenderer renderer = renderers.get(item);
		if (renderer == null) {
			throw new IllegalArgumentException("Renderer not registered for item " + item);
		}
		return renderer;
	}

	@Override
	public void render(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		this.render(0, matrixStackIn, bufferIn, packedLightIn);
	}

	public void render(float partialTicks, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn) {
		stack.translate(0.0D, 24 / 16F, 0.0D);
		stack.scale(-1.0F, -1.0F, 1.0F);
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(currentArmorItem));

		AnimationEvent itemEvent = new AnimationEvent(this.currentArmorItem, 0, 0, 0, false,
				Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
		modelProvider.setLivingAnimations(currentArmorItem, this.getUniqueID(this.currentArmorItem), itemEvent);
		this.fitToBiped();
		stack.push();
		MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(currentArmorItem));
		Color renderColor = getRenderColor(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn);
		RenderLayer renderType = getRenderType(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn,
				getTextureLocation(currentArmorItem));
		render(model, currentArmorItem, partialTicks, renderType, stack, null, bufferIn, packedLightIn,
				OverlayTexture.DEFAULT_UV, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		if (FabricLoader.getInstance().isModLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.pop();
		stack.scale(-1.0F, -1.0F, 1.0F);
		stack.translate(0.0D, -1.501F, 0.0D);
	}

	private void fitToBiped() {
		if (!(this.entityLiving instanceof ArmorStandEntity)) {
			if (this.headBone != null) {
				IBone headBone = this.modelProvider.getBone(this.headBone);
				GeoUtils.copyRotations(this.head, headBone);
				headBone.setPositionX(this.head.pivotX);
				headBone.setPositionY(-this.head.pivotY);
				headBone.setPositionZ(this.head.pivotZ);
			}

			if (this.bodyBone != null) {
				IBone bodyBone = this.modelProvider.getBone(this.bodyBone);
				GeoUtils.copyRotations(this.torso, bodyBone);
				bodyBone.setPositionX(this.torso.pivotX);
				bodyBone.setPositionY(-this.torso.pivotY);
				bodyBone.setPositionZ(this.torso.pivotZ);
			}
			if (this.rightArmBone != null) {
				IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);
				GeoUtils.copyRotations(this.rightArm, rightArmBone);
				rightArmBone.setPositionX(this.rightArm.pivotX + 5);
				rightArmBone.setPositionY(2 - this.rightArm.pivotY);
				rightArmBone.setPositionZ(this.rightArm.pivotZ);
			}

			if (this.leftArmBone != null) {
				IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);
				GeoUtils.copyRotations(this.leftArm, leftArmBone);
				leftArmBone.setPositionX(this.leftArm.pivotX - 5);
				leftArmBone.setPositionY(2 - this.leftArm.pivotY);
				leftArmBone.setPositionZ(this.leftArm.pivotZ);
			}
			if (this.rightLegBone != null) {
				IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);
				GeoUtils.copyRotations(this.rightLeg, rightLegBone);
				rightLegBone.setPositionX(this.rightLeg.pivotX + 2);
				rightLegBone.setPositionY(12 - this.rightLeg.pivotY);
				rightLegBone.setPositionZ(this.rightLeg.pivotZ);
				if (this.rightBootBone != null) {
					IBone rightBootBone = this.modelProvider.getBone(this.rightBootBone);
					GeoUtils.copyRotations(this.rightLeg, rightBootBone);
					rightBootBone.setPositionX(this.rightLeg.pivotX + 2);
					rightBootBone.setPositionY(12 - this.rightLeg.pivotY);
					rightBootBone.setPositionZ(this.rightLeg.pivotZ);
				}
			}
			if (this.leftLegBone != null) {
				IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);
				GeoUtils.copyRotations(this.leftLeg, leftLegBone);
				leftLegBone.setPositionX(this.leftLeg.pivotX - 2);
				leftLegBone.setPositionY(12 - this.leftLeg.pivotY);
				leftLegBone.setPositionZ(this.leftLeg.pivotZ);
				if (this.leftBootBone != null) {
					IBone leftBootBone = this.modelProvider.getBone(this.leftBootBone);
					GeoUtils.copyRotations(this.leftLeg, leftBootBone);
					leftBootBone.setPositionX(this.leftLeg.pivotX - 2);
					leftBootBone.setPositionY(12 - this.leftLeg.pivotY);
					leftBootBone.setPositionZ(this.leftLeg.pivotZ);
				}
			}
		}
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@Override
	public Identifier getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	/**
	 * Everything after this point needs to be called every frame before rendering
	 */
	public GeoArmorRenderer setCurrentItem(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot) {
		this.entityLiving = entityLiving;
		this.itemStack = itemStack;
		this.armorSlot = armorSlot;
		this.currentArmorItem = (T) itemStack.getItem();
		return this;
	}

	public final GeoArmorRenderer applyEntityStats(BipedEntityModel defaultArmor) {
		this.child = defaultArmor.child;
		this.sneaking = defaultArmor.sneaking;
		this.riding = defaultArmor.riding;
		this.rightArmPose = defaultArmor.rightArmPose;
		this.leftArmPose = defaultArmor.leftArmPose;
		return this;
	}

	public GeoArmorRenderer applySlot(EquipmentSlot slot) {
		modelProvider.getModel(modelProvider.getModelLocation(currentArmorItem));

		IBone headBone = this.getAndHideBone(this.headBone);
		IBone bodyBone = this.getAndHideBone(this.bodyBone);
		IBone rightArmBone = this.getAndHideBone(this.rightArmBone);
		IBone leftArmBone = this.getAndHideBone(this.leftArmBone);
		IBone rightLegBone = this.getAndHideBone(this.rightLegBone);
		IBone leftLegBone = this.getAndHideBone(this.leftLegBone);
		IBone rightBootBone = this.getAndHideBone(this.rightBootBone);
		IBone leftBootBone = this.getAndHideBone(this.leftBootBone);

		switch (slot) {
		case HEAD:
			if (headBone != null)
				headBone.setHidden(false);
			break;
		case CHEST:
			if (bodyBone != null)
				bodyBone.setHidden(false);
			if (rightArmBone != null)
				rightArmBone.setHidden(false);
			if (leftArmBone != null)
				leftArmBone.setHidden(false);
			break;
		case LEGS:
			if (rightLegBone != null)
				rightLegBone.setHidden(false);
			if (leftLegBone != null)
				leftLegBone.setHidden(false);
			break;
		case FEET:
			if (rightBootBone != null)
				rightBootBone.setHidden(false);
			if (leftBootBone != null)
				leftBootBone.setHidden(false);
			break;
		}
		return this;
	}

	protected IBone getAndHideBone(String boneName) {
		if (boneName != null) {
			final IBone bone = this.modelProvider.getBone(boneName);
			bone.setHidden(true);
			return bone;
		}
		return null;
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return Objects.hash(this.armorSlot, itemStack.getItem(), itemStack.getCount(),
				itemStack.hasTag() ? itemStack.getTag().toString() : 1, this.entityLiving.getUuid().toString());
	}
}
