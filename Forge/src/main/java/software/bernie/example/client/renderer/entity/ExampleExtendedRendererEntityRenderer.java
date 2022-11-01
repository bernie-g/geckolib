package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.example.client.DefaultBipedBoneIdents;
import software.bernie.example.client.EntityResources;
import software.bernie.example.client.model.entity.ExampleExtendedRendererEntityModel;
import software.bernie.example.entity.ExtendedRendererEntity;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class ExampleExtendedRendererEntityRenderer extends ExtendedGeoEntityRenderer<ExtendedRendererEntity> {

	public ExampleExtendedRendererEntityRenderer(EntityRendererManager renderManager) {
		super(renderManager,
				new ExampleExtendedRendererEntityModel<ExtendedRendererEntity>(EntityResources.EXTENDED_MODEL, EntityResources.EXTENDED_TEXTURE, "testentity"));
	}

	@Override
	protected ItemStack getHeldItemForBone(String boneName, ExtendedRendererEntity currentEntity) {
		switch (boneName) {
		case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
			return currentEntity.isLeftHanded() ? mainHand : offHand;
		case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
			return currentEntity.isLeftHanded() ? offHand : mainHand;
		case DefaultBipedBoneIdents.POTION_BONE_IDENT:
			break;
		}
		return null;
	}

	@Override
	protected TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		switch (boneName) {
		case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
			return TransformType.THIRD_PERSON_RIGHT_HAND;
		case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
			return TransformType.THIRD_PERSON_RIGHT_HAND;
		default:
			return TransformType.NONE;
		}
	}

	@Override
	protected void preRenderItem(MatrixStack stack, ItemStack item, String boneName,
			ExtendedRendererEntity currentEntity, IBone bone) {
		if (item == this.mainHand || item == this.offHand) {
			stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			boolean shieldFlag = item.isShield(currentEntity) || item.getItem() instanceof ShieldItem;
			if (item == this.mainHand) {
				if (shieldFlag) {
					stack.translate(0.0, 0.125, -0.25);
				} else {

				}
			} else {
				if (shieldFlag) {
					stack.translate(0, 0.125, 0.25);
					stack.mulPose(Vector3f.YP.rotationDegrees(180));
				} else {

				}

			}
			// stack.mulPose(Vector3f.YP.rotationDegrees(180));

			// stack.scale(0.75F, 0.75F, 0.75F);
			
		}
	}

	@Override
	protected void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName,
			ExtendedRendererEntity currentEntity, IBone bone) {

	}

	@Override
	protected ItemStack getArmorForBone(String boneName, ExtendedRendererEntity currentEntity) {
		switch (boneName) {
		case "armorBipedLeftFoot":
		case "armorBipedRightFoot":
		case "armorBipedLeftFoot2":
		case "armorBipedRightFoot2":
			return boots;
		case "armorBipedLeftLeg":
		case "armorBipedRightLeg":
		case "armorBipedLeftLeg2":
		case "armorBipedRightLeg2":
			return leggings;
		case "armorBipedBody":
		case "armorBipedRightArm":
		case "armorBipedLeftArm":
			return chestplate;
		case "armorBipedHead":
			return helmet;
		default:
			return null;
		}
	}

	@Override
	protected EquipmentSlotType getEquipmentSlotForArmorBone(String boneName, ExtendedRendererEntity currentEntity) {
		switch (boneName) {
		case "armorBipedLeftFoot":
		case "armorBipedRightFoot":
		case "armorBipedLeftFoot2":
		case "armorBipedRightFoot2":
			return EquipmentSlotType.FEET;
		case "armorBipedLeftLeg":
		case "armorBipedRightLeg":
		case "armorBipedLeftLeg2":
		case "armorBipedRightLeg2":
			return EquipmentSlotType.LEGS;
		case "armorBipedRightArm":
			return !currentEntity.isLeftHanded() ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
		case "armorBipedLeftArm":
			return currentEntity.isLeftHanded() ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
		case "armorBipedBody":
			return EquipmentSlotType.CHEST;
		case "armorBipedHead":
			return EquipmentSlotType.HEAD;
		default:
			return null;
		}
	}

	@Override
	protected ModelRenderer getArmorPartForBone(String name, BipedModel<?> armorModel) {
		switch (name) {
		case "armorBipedLeftFoot":
		case "armorBipedLeftLeg":
		case "armorBipedLeftFoot2":
		case "armorBipedLeftLeg2":
			return armorModel.leftLeg;
		case "armorBipedRightFoot":
		case "armorBipedRightLeg":
		case "armorBipedRightFoot2":
		case "armorBipedRightLeg2":
			return armorModel.rightLeg;
		case "armorBipedRightArm":
			return armorModel.rightArm;
		case "armorBipedLeftArm":
			return armorModel.leftArm;
		case "armorBipedBody":
			return armorModel.body;
		case "armorBipedHead":
			return armorModel.head;
		default:
			return null;
		}
	}

	@Override
	protected BlockState getHeldBlockForBone(String boneName, ExtendedRendererEntity currentEntity) {
		return null;
	}

	@Override
	protected void preRenderBlock(MatrixStack stack, BlockState block, String boneName, ExtendedRendererEntity currentEntity) {

	}

	@Override
	protected void postRenderBlock(MatrixStack stack, BlockState block, String boneName, ExtendedRendererEntity currentEntity) {
	}
	
	@Override
	protected ResourceLocation getTextureForBone(String boneName, ExtendedRendererEntity currentEntity) {
		if ("bipedCape".equals(boneName))
			return EntityResources.EXTENDED_CAPE_TEXTURE;

		return null;
	}

	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return bone.getName().startsWith("armor");
	}

}
