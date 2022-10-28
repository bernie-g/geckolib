package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.example.client.DefaultBipedBoneIdents;
import software.bernie.example.client.model.entity.ExampleExtendedRendererEntityModel;
import software.bernie.example.entity.ExtendedRendererEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class ExampleExtendedRendererEntityRenderer extends ExtendedGeoEntityRenderer<ExtendedRendererEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(GeckoLib.ModID,
			"textures/entity/extendedrendererentity.png");
	private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(GeckoLib.ModID,
			"geo/extendedrendererentity.geo.json");

	protected ItemStack mainHandItem, offHandItem, helmetItem, chestplateItem, leggingsItem, bootsItem;

	public ExampleExtendedRendererEntityRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ExampleExtendedRendererEntityModel<>(MODEL_RESLOC, TEXTURE, "testentity"));
	}

	@Override
	public void renderEarly(ExtendedRendererEntity animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
							VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);

		this.mainHandItem = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
		this.offHandItem = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
		this.helmetItem = animatable.getItemBySlot(EquipmentSlot.HEAD);
		this.chestplateItem = animatable.getItemBySlot(EquipmentSlot.CHEST);
		this.leggingsItem = animatable.getItemBySlot(EquipmentSlot.LEGS);
		this.bootsItem = animatable.getItemBySlot(EquipmentSlot.FEET);
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
	protected TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		return switch (boneName) {
			case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT, DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT -> TransformType.THIRD_PERSON_RIGHT_HAND; // Do Defaults
			default -> TransformType.NONE;
		};
	}

	@Override
	protected void preRenderItem(PoseStack stack, ItemStack item, String boneName, ExtendedRendererEntity currentEntity, IBone bone) {
		if (item == this.mainHandItem) {
			stack.mulPose(Vector3f.XP.rotationDegrees(-90f));

			if (item.getItem() instanceof ShieldItem)
				stack.translate(0, 0.125, -0.25);
		}
		else if (item == this.offHandItem) {
			stack.mulPose(Vector3f.XP.rotationDegrees(-90f));

			if (item.getItem() instanceof ShieldItem) {
				stack.translate(0, 0.125, 0.25);
				stack.mulPose(Vector3f.YP.rotationDegrees(180));
			}
		}
	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, ExtendedRendererEntity currentEntity, IBone bone) {}

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
	protected ModelPart getArmorPartForBone(String name, HumanoidModel<?> armorModel) {
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
	protected void preRenderBlock(PoseStack stack, BlockState block, String boneName,
			ExtendedRendererEntity currentEntity) {

	}

	@Override
	protected void postRenderBlock(PoseStack stack, BlockState block, String boneName,
			ExtendedRendererEntity currentEntity) {
	}

	protected final ResourceLocation CAPE_TEXTURE = new ResourceLocation(GeckoLib.ModID,
			"textures/entity/extendedrendererentity_cape.png");

	@Override
	protected ResourceLocation getTextureForBone(String boneName, ExtendedRendererEntity animatable) {
		if ("bipedCape".equals(boneName))
			return CAPE_TEXTURE;

		return null;
	}

	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return bone.getName().startsWith("armor");
	}

}
