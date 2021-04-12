package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.GeoUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GeoArmorRenderer<T extends ArmorItem & IAnimatable> extends BipedModel
		implements IGeoRenderer<T> {
	private static Map<Class<? extends ArmorItem>, GeoArmorRenderer> renderers = new ConcurrentHashMap<>();

	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof ArmorItem) {
				GeoArmorRenderer renderer = renderers.get(object.getClass());
				return renderer == null ? null : renderer.getGeoModelProvider();
			}
			return null;
		});
	}

	private T currentArmorItem;
	private LivingEntity entityLiving;
	private ItemStack itemStack;
	private EquipmentSlotType armorSlot;

	// Set these to the names of your armor's bones, or null if you aren't using them
	public String headBone = "armorHead";
	public String bodyBone = "armorBody";
	public String rightArmBone = "armorRightArm";
	public String leftArmBone = "armorLeftArm";
	public String rightLegBone = "armorRightLeg";
	public String leftLegBone = "armorLeftLeg";
	public String rightBootBone = "armorRightBoot";
	public String leftBootBone = "armorLeftBoot";

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

	private final AnimatedGeoModel<T> modelProvider;

	public GeoArmorRenderer(AnimatedGeoModel<T> modelProvider) {
		super(1);
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		this.render(0, matrixStackIn, bufferIn, packedLightIn);
	}

	public void render(float partialTicks, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn) {
		stack.translate(0.0D, 24 / 16F, 0.0D);
		stack.scale(-1.0F, -1.0F, 1.0F);
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(currentArmorItem));

		AnimationEvent itemEvent = new AnimationEvent(this.currentArmorItem, 0, 0, 0, false,
				Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
		modelProvider.setLivingAnimations(currentArmorItem, this.getUniqueID(this.currentArmorItem), itemEvent);
		this.fitToBiped();
		stack.push();
		Minecraft.getInstance().textureManager.bindTexture(getTextureLocation(currentArmorItem));
		Color renderColor = getRenderColor(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn);
		RenderType renderType = getRenderType(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn,
				getTextureLocation(currentArmorItem));
		render(model, currentArmorItem, partialTicks, renderType, stack, null, bufferIn, packedLightIn,
				OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		if (ModList.get().isLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.pop();
		stack.scale(-1.0F, -1.0F, 1.0F);
		stack.translate(0.0D, -24 / 16F, 0.0D);
	}

	protected void fitToBiped() {
		if (!(this.entityLiving instanceof ArmorStandEntity)) {
			if (this.headBone != null) {
				IBone headBone = this.modelProvider.getBone(this.headBone);
				GeoUtils.copyRotations(this.bipedHead, headBone);
				headBone.setPositionX(this.bipedHead.rotationPointX);
				headBone.setPositionY(-this.bipedHead.rotationPointY);
				headBone.setPositionZ(this.bipedHead.rotationPointZ);
			}

			if (this.bodyBone != null) {
				IBone bodyBone = this.modelProvider.getBone(this.bodyBone);
				GeoUtils.copyRotations(this.bipedBody, bodyBone);
				bodyBone.setPositionX(this.bipedBody.rotationPointX);
				bodyBone.setPositionY(-this.bipedBody.rotationPointY);
				bodyBone.setPositionZ(this.bipedBody.rotationPointZ);
			}

			if (this.rightArmBone != null) {
				IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);
				GeoUtils.copyRotations(this.bipedRightArm, rightArmBone);
				rightArmBone.setPositionX(this.bipedRightArm.rotationPointX + 5);
				rightArmBone.setPositionY(2 - this.bipedRightArm.rotationPointY);
				rightArmBone.setPositionZ(this.bipedRightArm.rotationPointZ);
			}

			if (this.leftArmBone != null) {
				IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);
				GeoUtils.copyRotations(this.bipedLeftArm, leftArmBone);
				leftArmBone.setPositionX(this.bipedLeftArm.rotationPointX - 5);
				leftArmBone.setPositionY(2 - this.bipedLeftArm.rotationPointY);
				leftArmBone.setPositionZ(this.bipedLeftArm.rotationPointZ);
			}

			if (this.rightLegBone != null) {
				IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);
				GeoUtils.copyRotations(this.bipedRightLeg, rightLegBone);
				rightLegBone.setPositionX(this.bipedRightLeg.rotationPointX + 2);
				rightLegBone.setPositionY(12 - this.bipedRightLeg.rotationPointY);
				rightLegBone.setPositionZ(this.bipedRightLeg.rotationPointZ);
				if (this.rightBootBone != null) {
					IBone rightBootBone = this.modelProvider.getBone(this.rightBootBone);
					GeoUtils.copyRotations(this.bipedRightLeg, rightBootBone);
					rightBootBone.setPositionX(this.bipedRightLeg.rotationPointX + 2);
					rightBootBone.setPositionY(12 - this.bipedRightLeg.rotationPointY);
					rightBootBone.setPositionZ(this.bipedRightLeg.rotationPointZ);
				}
			}

			if (this.leftLegBone != null) {
				IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);
				GeoUtils.copyRotations(this.bipedLeftLeg, leftLegBone);
				leftLegBone.setPositionX(this.bipedLeftLeg.rotationPointX - 2);
				leftLegBone.setPositionY(12 - this.bipedLeftLeg.rotationPointY);
				leftLegBone.setPositionZ(this.bipedLeftLeg.rotationPointZ);
				if (this.leftBootBone != null) {
					IBone leftBootBone = this.modelProvider.getBone(this.leftBootBone);
					GeoUtils.copyRotations(this.bipedLeftLeg, leftBootBone);
					leftBootBone.setPositionX(this.bipedLeftLeg.rotationPointX - 2);
					leftBootBone.setPositionY(12 - this.bipedLeftLeg.rotationPointY);
					leftBootBone.setPositionZ(this.bipedLeftLeg.rotationPointZ);
				}
			}
		}
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	/**
	 * Everything after this point needs to be called every frame before rendering
	 */
	public GeoArmorRenderer setCurrentItem(LivingEntity entityLiving, ItemStack itemStack,
		   EquipmentSlotType armorSlot) {
		this.entityLiving = entityLiving;
		this.itemStack = itemStack;
		this.armorSlot = armorSlot;
		this.currentArmorItem = (T) itemStack.getItem();
		return this;
	}

	public final GeoArmorRenderer applyEntityStats(BipedModel defaultArmor) {
		this.isChild = defaultArmor.isChild;
		this.isSneak = defaultArmor.isSneak;
		this.isSitting = defaultArmor.isSitting;
		this.rightArmPose = defaultArmor.rightArmPose;
		this.leftArmPose = defaultArmor.leftArmPose;
		return this;
	}

	public GeoArmorRenderer applySlot(EquipmentSlotType slot) {
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
				if (headBone != null) headBone.setHidden(false);
				break;
			case CHEST:
				if (bodyBone != null) bodyBone.setHidden(false);
				if (rightArmBone != null) rightArmBone.setHidden(false);
				if (leftArmBone != null) leftArmBone.setHidden(false);
				break;
			case LEGS:
				if (rightLegBone != null) rightLegBone.setHidden(false);
				if (leftLegBone != null) leftLegBone.setHidden(false);
				break;
			case FEET:
				if (rightBootBone != null) rightBootBone.setHidden(false);
				if (leftBootBone != null) leftBootBone.setHidden(false);
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
				itemStack.hasTag() ? itemStack.getTag().toString() : 1, this.entityLiving.getUniqueID().toString());
	}
}
