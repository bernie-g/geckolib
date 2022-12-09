package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import software.bernie.example.client.model.entity.GremlinModel;
import software.bernie.example.entity.DynamicExampleEntity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Example {@link DynamicGeoEntityRenderer} implementation
 * @see DynamicExampleEntity
 */
public class GremlinRenderer extends DynamicGeoEntityRenderer<DynamicExampleEntity> {
	// Pre-define our bone names for easy and consistent reference later
	private static final String LEFT_HAND = "bipedHandLeft";
	private static final String RIGHT_HAND = "bipedHandRight";
	private static final String LEFT_BOOT = "armorBipedLeftFoot";
	private static final String RIGHT_BOOT = "armorBipedRightFoot";
	private static final String LEFT_ARMOR_LEG = "armorBipedLeftLeg";
	private static final String RIGHT_ARMOR_LEG = "armorBipedRightLeg";
	private static final String CHESTPLATE = "armorBipedBody";
	private static final String RIGHT_SLEEVE = "armorBipedRightArm";
	private static final String LEFT_SLEEVE = "armorBipedLeftArm";
	private static final String HELMET = "armorBipedHead";

	protected final ResourceLocation CAPE_TEXTURE = new ResourceLocation(GeckoLib.MOD_ID, "textures/entity/dynamic_entity_cape.png");

	protected ItemStack mainHandItem;
	protected ItemStack offhandItem;

	public GremlinRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new GremlinModel());

		// Add some armor rendering
		addRenderLayer(new ItemArmorGeoLayer<>(this) {
			@Nullable
			@Override
			protected ItemStack getArmorItemForBone(GeoBone bone, DynamicExampleEntity animatable) {
				// Return the items relevant to the bones being rendered for additional rendering
				return switch (bone.getName()) {
					case LEFT_BOOT, RIGHT_BOOT -> this.bootsStack;
					case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG -> this.leggingsStack;
					case CHESTPLATE, RIGHT_SLEEVE, LEFT_SLEEVE -> this.chestplateStack;
					case HELMET -> this.helmetStack;
					default -> null;
				};
			}

			// Return the equipment slot relevant to the bone we're using
			@Nonnull
			@Override
			protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, DynamicExampleEntity animatable) {
				return switch (bone.getName()) {
					case LEFT_BOOT, RIGHT_BOOT -> EquipmentSlot.FEET;
					case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG -> EquipmentSlot.LEGS;
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
			protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, DynamicExampleEntity animatable, HumanoidModel<?> baseModel) {
				return switch (bone.getName()) {
					case LEFT_BOOT, LEFT_ARMOR_LEG -> baseModel.leftLeg;
					case RIGHT_BOOT, RIGHT_ARMOR_LEG -> baseModel.rightLeg;
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
			protected ItemStack getStackForBone(GeoBone bone, DynamicExampleEntity animatable) {
				// Retrieve the items in the entity's hands for the relevant bone
				return switch (bone.getName()) {
					case LEFT_HAND -> animatable.isLeftHanded() ?
							GremlinRenderer.this.mainHandItem : GremlinRenderer.this.offhandItem;
					case RIGHT_HAND -> animatable.isLeftHanded() ?
							GremlinRenderer.this.offhandItem : GremlinRenderer.this.mainHandItem;
					default -> null;
				};
			}

			@Override
			protected TransformType getTransformTypeForStack(GeoBone bone, ItemStack stack, DynamicExampleEntity animatable) {
				// Apply the camera transform for the given hand
				return switch (bone.getName()) {
					case LEFT_HAND, RIGHT_HAND -> TransformType.THIRD_PERSON_RIGHT_HAND;
					default -> TransformType.NONE;
				};
			}

			// Do some quick render modifications depending on what the item is
			@Override
			protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, DynamicExampleEntity animatable,
											  MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
				if (stack == GremlinRenderer.this.mainHandItem) {
					poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

					if (stack.getItem() instanceof ShieldItem)
						poseStack.translate(0, 0.125, -0.25);
				}
				else if (stack == GremlinRenderer.this.offhandItem) {
					poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

					if (stack.getItem() instanceof ShieldItem) {
						poseStack.translate(0, 0.125, 0.25);
						poseStack.mulPose(Axis.YP.rotationDegrees(180));
					}
				}

				super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
			}
		});
	}

	// Apply the cape texture for the cape bone
	@Nullable
	@Override
	protected ResourceLocation getTextureOverrideForBone(GeoBone bone, DynamicExampleEntity animatable, float partialTick) {
		return "bipedCape".equals(bone.getName()) ? CAPE_TEXTURE : null;
	}

	@Override
	public void preRender(PoseStack poseStack, DynamicExampleEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.preRender(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

		this.mainHandItem = animatable.getMainHandItem();
		this.offhandItem = animatable.getOffhandItem();
	}
}