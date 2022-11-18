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
import software.bernie.example.client.model.entity.MutantZombieModel;
import software.bernie.example.entity.MutantZombieEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.cache.object.GeoBone;
import software.bernie.geckolib3.renderer.ExtendedGeoEntityRenderer;
import software.bernie.geckolib3.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib3.renderer.layer.ItemArmorGeoLayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Example {@link ExtendedGeoEntityRenderer} implementation
 * @see MutantZombieEntity
 */
public class MutantZombieRenderer extends ExtendedGeoEntityRenderer<MutantZombieEntity> {
	// Pre-define our bone names for easy and consistent reference later
	private static final String LEFT_HAND = "bipedHandLeft";
	private static final String RIGHT_HAND = "bipedHandRight";
	private static final String LEFT_BOOT = "armorBipedLeftFoot";
	private static final String RIGHT_BOOT = "armorBipedRightFoot";
	private static final String LEFT_BOOT_2 = "armorBipedLeftFoot2";
	private static final String RIGHT_BOOT_2 = "armorBipedRightFoot2";
	private static final String LEFT_ARMOR_LEG = "armorBipedLeftLeg";
	private static final String RIGHT_ARMOR_LEG = "armorBipedRightLeg";
	private static final String LEFT_ARMOR_LEG_2 = "armorBipedLeftLeg2";
	private static final String RIGHT_ARMOR_LEG_2 = "armorBipedRightLeg2";
	private static final String CHESTPLATE = "armorBipedBody";
	private static final String RIGHT_SLEEVE = "armorBipedRightArm";
	private static final String LEFT_SLEEVE = "armorBipedLeftArm";
	private static final String HELMET = "armorBipedHead";

	protected final ResourceLocation CAPE_TEXTURE = new ResourceLocation(GeckoLib.MOD_ID, "textures/entity/mutant_zombie_cape.png");

	protected ItemStack mainHandItem;
	protected ItemStack offhandItem;

	public MutantZombieRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new MutantZombieModel());

		// Add some armor rendering
		addRenderLayer(new ItemArmorGeoLayer<>(this) {
			@Nullable
			@Override
			protected ItemStack getArmorItemForBone(GeoBone bone, MutantZombieEntity animatable) {
				// Return the items relevant to the bones being rendered for additional rendering
				return switch (bone.getName()) {
					case LEFT_BOOT, RIGHT_BOOT, LEFT_BOOT_2, RIGHT_BOOT_2 -> this.bootsStack;
					case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG, LEFT_ARMOR_LEG_2, RIGHT_ARMOR_LEG_2 -> this.leggingsStack;
					case CHESTPLATE, RIGHT_SLEEVE, LEFT_SLEEVE -> this.chestplateStack;
					case HELMET -> this.helmetStack;
					default -> null;
				};
			}

			// Return the equipment slot relevant to the bone we're using
			@Nonnull
			@Override
			protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, MutantZombieEntity animatable) {
				return switch (bone.getName()) {
					case LEFT_BOOT, RIGHT_BOOT, LEFT_BOOT_2, RIGHT_BOOT_2 -> EquipmentSlot.FEET;
					case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG, LEFT_ARMOR_LEG_2, RIGHT_ARMOR_LEG_2 -> EquipmentSlot.LEGS;
					case RIGHT_SLEEVE -> !animatable.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
					case LEFT_SLEEVE -> animatable.isLeftHanded() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
					case CHESTPLATE -> EquipmentSlot.CHEST;
					case HELMET -> EquipmentSlot.HEAD;
					default -> super.getEquipmentSlotForBone(bone, stack, animatable);
				};
			}

			// Return the ModelPart responsible for the armor pieces we want to render
			@Nonnull
			@Override
			protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, MutantZombieEntity animatable, HumanoidModel<?> baseModel) {
				return switch (bone.getName()) {
					case LEFT_BOOT, RIGHT_BOOT, LEFT_BOOT_2, RIGHT_BOOT_2 -> baseModel.leftLeg;
					case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG, LEFT_ARMOR_LEG_2, RIGHT_ARMOR_LEG_2 -> baseModel.rightLeg;
					case RIGHT_SLEEVE -> baseModel.rightArm;
					case LEFT_SLEEVE -> baseModel.leftArm;
					case CHESTPLATE -> baseModel.body;
					case HELMET -> baseModel.head;
					default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
				};
			}
		});

		// Add some held item rendering
		addRenderLayer(new BlockAndItemGeoLayer<>(this) {
			@Nullable
			@Override
			protected ItemStack getStackForBone(GeoBone bone, MutantZombieEntity animatable) {
				// Retrieve the items in the entity's hands for the relevant bone
				return switch (bone.getName()) {
					case LEFT_HAND -> animatable.isLeftHanded() ?
							MutantZombieRenderer.this.mainHandItem : MutantZombieRenderer.this.offhandItem;
					case RIGHT_HAND -> animatable.isLeftHanded() ?
							MutantZombieRenderer.this.offhandItem : MutantZombieRenderer.this.mainHandItem;
					default -> null;
				};
			}

			@Override
			protected TransformType getTransformTypeForStack(GeoBone bone, ItemStack stack, MutantZombieEntity animatable) {
				// Apply the camera transform for the given hand
				return switch (bone.getName()) {
					case LEFT_HAND, RIGHT_HAND -> TransformType.THIRD_PERSON_RIGHT_HAND;
					default -> TransformType.NONE;
				};
			}

			// Do some quick render modifications depending on what the item is
			@Override
			protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, MutantZombieEntity animatable,
											  MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
				if (stack == MutantZombieRenderer.this.mainHandItem) {
					poseStack.mulPose(Vector3f.XP.rotationDegrees(-90f));

					if (stack.getItem() instanceof ShieldItem)
						poseStack.translate(0, 0.125, -0.25);
				}
				else if (stack == MutantZombieRenderer.this.offhandItem) {
					poseStack.mulPose(Vector3f.XP.rotationDegrees(-90f));

					if (stack.getItem() instanceof ShieldItem) {
						poseStack.translate(0, 0.125, 0.25);
						poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
					}
				}

				super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
			}
		});
	}

	// Apply the cape texture for the cape bone
	@Nullable
	@Override
	protected ResourceLocation getTextureOverrideForBone(GeoBone bone, MutantZombieEntity animatable, float partialTick) {
		return "bipedCape".equals(bone.getName()) ? CAPE_TEXTURE : null;
	}

	@Override
	public void preRender(PoseStack poseStack, MutantZombieEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.preRender(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

		this.mainHandItem = animatable.getMainHandItem();
		this.offhandItem = animatable.getOffhandItem();
	}
}