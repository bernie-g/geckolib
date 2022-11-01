package software.bernie.example.client.renderer.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import software.bernie.example.client.DefaultBipedBoneIdents;
import software.bernie.example.client.EntityResources;
import software.bernie.example.client.model.entity.ExampleExtendedRendererEntityModel;
import software.bernie.example.entity.ExtendedRendererEntity;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class ExampleExtendedRendererEntityRenderer extends ExtendedGeoEntityRenderer<ExtendedRendererEntity> {

	protected ItemStack mainHandItem, offHandItem, helmetItem, chestplateItem, leggingsItem, bootsItem;

	public ExampleExtendedRendererEntityRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new ExampleExtendedRendererEntityModel<>(EntityResources.EXTENDED_MODEL, EntityResources.EXTENDED_TEXTURE, "testentity"));
	}
	
	@Override
	public void renderEarly(ExtendedRendererEntity animatable, MatrixStack poseStack, float partialTick,
			VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float partialTicks) {
		super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);

		this.mainHandItem = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
		this.offHandItem = animatable.getEquippedStack(EquipmentSlot.OFFHAND);
		this.helmetItem = animatable.getEquippedStack(EquipmentSlot.HEAD);
		this.chestplateItem = animatable.getEquippedStack(EquipmentSlot.CHEST);
		this.leggingsItem = animatable.getEquippedStack(EquipmentSlot.LEGS);
		this.bootsItem = animatable.getEquippedStack(EquipmentSlot.FEET);
	}

	@Override
	protected ItemStack getHeldItemForBone(String boneName, ExtendedRendererEntity currentEntity) {
		return switch (boneName) {
			case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT -> currentEntity.isLeftHanded() ? mainHandItem : offHandItem;
			case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT -> currentEntity.isLeftHanded() ? offHandItem : mainHandItem;
			default -> null;
		};
	}

	@Override
	protected Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		return switch (boneName) {
		case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT, DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT -> Mode.THIRD_PERSON_RIGHT_HAND; // Do Defaults
		default -> Mode.NONE;
	};
}

	@Override
	protected void preRenderItem(MatrixStack stack, ItemStack item, String boneName,
			ExtendedRendererEntity currentEntity, IBone bone) {
		if (item == this.mainHandItem) {
			stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f));

			if (item.getItem() instanceof ShieldItem)
				stack.translate(0, 0.125, -0.25);
		}
		else if (item == this.offHandItem) {
			stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f));

			if (item.getItem() instanceof ShieldItem) {
				stack.translate(0, 0.125, 0.25);
				stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
			}
		}
	}

	@Override
	protected void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName,
			ExtendedRendererEntity currentEntity, IBone bone) {

	}

	@Override
	protected ItemStack getArmorForBone(String boneName, ExtendedRendererEntity currentEntity) {
		return switch (boneName) {
			case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_2_IDENT,
					DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_2_IDENT -> this.bootsItem;
			case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_2_IDENT,
					DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_2_IDENT -> this.leggingsItem;
			case DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT -> this.chestplateItem;
			case DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT -> this.helmetItem;
			default -> null;
		};
	}

	@Override
	protected EquipmentSlot getEquipmentSlotForArmorBone(String boneName, ExtendedRendererEntity currentEntity) {
		return switch (boneName) {
			case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_2_IDENT,
					DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_2_IDENT -> EquipmentSlot.FEET;
			case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_2_IDENT,
					DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_2_IDENT -> EquipmentSlot.LEGS;
			case DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT -> !currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
			case DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT -> currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
			case DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT -> EquipmentSlot.CHEST;
			case DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT -> EquipmentSlot.HEAD;
			default -> null;
		};
	}

	@Override
	protected ModelPart getArmorPartForBone(String name, BipedEntityModel<?> armorModel) {
		return switch (name) {
			case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_2_IDENT,
					DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_2_IDENT -> armorModel.leftLeg;
			case DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT,
					DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_2_IDENT,
					DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_2_IDENT -> armorModel.rightLeg;
			case DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT -> armorModel.rightArm;
			case DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT -> armorModel.leftArm;
			case DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT -> armorModel.body;
			case DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT -> armorModel.head;
			default -> null;
		};
	}

	@Override
	protected BlockState getHeldBlockForBone(String boneName, ExtendedRendererEntity currentEntity) {
		return null;
	}

	@Override
	protected void preRenderBlock(MatrixStack stack, BlockState block, String boneName,
			ExtendedRendererEntity currentEntity) {

	}

	@Override
	protected void postRenderBlock(MatrixStack stack, BlockState block, String boneName,
			ExtendedRendererEntity currentEntity) {
	}

	@Override
	protected Identifier getTextureForBone(String boneName, ExtendedRendererEntity animatable) {
		if ("bipedCape".equals(boneName))
			return EntityResources.EXTENDED_CAPE_TEXTURE;

		return null;
	}

	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return bone.getName().startsWith("armor");
	}

}
