package software.bernie.example.client.renderer.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelPart;
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
import software.bernie.example.client.model.entity.ExampleExtendedRendererEntityModel;
import software.bernie.example.entity.ExtendedRendererEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class ExampleExtendedRendererEntityRenderer extends ExtendedGeoEntityRenderer<ExtendedRendererEntity> {

	private static final Identifier TEXTURE = new Identifier(GeckoLib.ModID,
			"textures/entity/extendedrendererentity.png");
	private static final Identifier MODEL_RESLOC = new Identifier(GeckoLib.ModID,
			"geo/extendedrendererentity.geo.json");

	public ExampleExtendedRendererEntityRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager,
				new ExampleExtendedRendererEntityModel<ExtendedRendererEntity>(MODEL_RESLOC, TEXTURE, "extendedrendererentity"));
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
	protected Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		switch (boneName) {
		case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
			return Mode.THIRD_PERSON_RIGHT_HAND;
		case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
			return Mode.THIRD_PERSON_RIGHT_HAND;
		default:
			return Mode.NONE;
		}
	}

	@Override
	protected void preRenderItem(MatrixStack stack, ItemStack item, String boneName, ExtendedRendererEntity currentEntity,
			IBone bone) {
		if (item == this.mainHand || item == this.offHand) {
			stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
			boolean shieldFlag = item.getItem() instanceof ShieldItem;
			if (item == this.mainHand) {
				if (shieldFlag) {
					stack.translate(0.0, 0.125, -0.25);
				} else {

				}
			} else {
				if (shieldFlag) {
					stack.translate(0, 0.125, 0.25);
					stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
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
	protected EquipmentSlot getEquipmentSlotForArmorBone(String boneName, ExtendedRendererEntity currentEntity) {
		switch (boneName) {
		case "armorBipedLeftFoot":
		case "armorBipedRightFoot":
		case "armorBipedLeftFoot2":
		case "armorBipedRightFoot2":
			return EquipmentSlot.FEET;
		case "armorBipedLeftLeg":
		case "armorBipedRightLeg":
		case "armorBipedLeftLeg2":
		case "armorBipedRightLeg2":
			return EquipmentSlot.LEGS;
		case "armorBipedRightArm":
			return !currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
		case "armorBipedLeftArm":
			return currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
		case "armorBipedBody":
			return EquipmentSlot.CHEST;
		case "armorBipedHead":
			return EquipmentSlot.HEAD;
		default:
			return null;
		}
	}

	@Override
	protected ModelPart getArmorPartForBone(String name, BipedEntityModel<?> armorModel) {
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
	protected void preRenderBlock(BlockState block, String boneName, ExtendedRendererEntity currentEntity) {

	}

	@Override
	protected void postRenderBlock(BlockState block, String boneName, ExtendedRendererEntity currentEntity) {
	}

	@Override
	protected Identifier getTextureForBone(String boneName, ExtendedRendererEntity currentEntity) {
		switch (boneName) {
		default:
			return null;
		}
	}
	
	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return bone.getName().startsWith("armor");
	}

}
