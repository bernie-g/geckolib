package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.object.Color;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.service.GeckoLibClient;
import software.bernie.geckolib.util.RenderUtil;

import java.util.function.Function;

/**
 * Builtin class for handling dynamic armor rendering on GeckoLib entities
 * <p>
 * Supports both {@link GeoItem GeckoLib} and {@link ArmorItem Vanilla} armor models
 * <p>
 * Unlike a traditional armor renderer, this renderer renders per-bone, giving much more flexible armor rendering
 */
public class ItemArmorGeoLayer<T extends LivingEntity & GeoAnimatable> extends GeoRenderLayer<T> {
	protected final EquipmentLayerRenderer equipmentRenderer;
	protected final Function<SkullBlock.Type, SkullModelBase> skullModels;

	@Nullable
	protected ItemStack mainHandStack;
	@Nullable protected ItemStack offhandStack;
	@Nullable protected ItemStack helmetStack;
	@Nullable protected ItemStack chestplateStack;
	@Nullable protected ItemStack leggingsStack;
	@Nullable protected ItemStack bootsStack;

	public ItemArmorGeoLayer(GeoRenderer<T> geoRenderer, EquipmentLayerRenderer equipmentLayerRenderer) {
		super(geoRenderer);

		this.equipmentRenderer = equipmentLayerRenderer;
		this.skullModels = Util.memoize(type -> SkullBlockRenderer.createModel(Minecraft.getInstance().getEntityModels(), type));
	}

	/**
	 * Return an EquipmentSlot for a given {@link ItemStack} and animatable instance
	 * <p>
	 * This is what determines the base model to use for rendering a particular stack
	 */
	@NotNull
	protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, T animatable) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
				if (stack == animatable.getItemBySlot(slot))
					return slot;
			}
		}

		return EquipmentSlot.CHEST;
	}

	/**
	 * Return a ModelPart for a given {@link GeoBone}
	 * <p>
	 * This is then transformed into position for the final render
	 */
	@NotNull
	protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, T animatable, HumanoidModel<?> baseModel) {
		return baseModel.body;
	}

	/**
	 * Get the {@link ItemStack} relevant to the bone being rendered
	 *
	 * @return The ItemStack for the bone being rendered, or null if this bone should be ignored
	 */
	@Nullable
	protected ItemStack getArmorItemForBone(GeoBone bone, T animatable) {
		return null;
	}

	/**
	 * This method is called by the {@link GeoRenderer} before rendering, immediately after {@link GeoRenderer#preRender} has been called
	 * <p>
	 * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource,
                          @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int renderColor) {
		this.mainHandStack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
		this.offhandStack = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
		this.helmetStack = animatable.getItemBySlot(EquipmentSlot.HEAD);
		this.chestplateStack = animatable.getItemBySlot(EquipmentSlot.CHEST);
		this.leggingsStack = animatable.getItemBySlot(EquipmentSlot.LEGS);
		this.bootsStack = animatable.getItemBySlot(EquipmentSlot.FEET);
	}

	/**
	 * This method is called by the {@link GeoRenderer} for each bone being rendered
	 * <p>
	 * This is a more expensive call, particularly if being used to render something on a different buffer
	 * It does however have the benefit of having the matrix translations and other transformations already applied from render-time
	 * It's recommended to avoid using this unless necessary
	 * <p>
	 * The {@link GeoBone} in question has already been rendered by this stage
	 * <p>
	 * If you <i>do</i> use it, and you render something that changes the {@link VertexConsumer buffer}, you need to reset it back to the previous buffer
	 * using {@link MultiBufferSource#getBuffer} before ending the method
	 */
	@Override
	public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
							  VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int renderColor) {
		ItemStack armorStack = getArmorItemForBone(bone, animatable);

		if (armorStack == null || armorStack.isEmpty())
			return;

		if (armorStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractSkullBlock skullBlock) {
			renderSkullAsArmor(poseStack, bone, armorStack, skullBlock, bufferSource, packedLight);
		}
		else {
			EquipmentSlot slot = getEquipmentSlotForBone(bone, armorStack, animatable);
			HumanoidModel<?> model = getModelForItem(bone, slot, armorStack, animatable);
			ModelPart modelPart = getModelPartForBone(bone, slot, armorStack, animatable, model);

			if (!modelPart.cubes.isEmpty()) {
				poseStack.pushPose();
				poseStack.scale(-1, -1, 1);

				if (model instanceof GeoArmorRenderer<?> geoArmorRenderer) {
					prepModelPartForRender(poseStack, bone, modelPart);
					geoArmorRenderer.prepForRender(animatable, armorStack, slot, model, bufferSource, partialTick, 0, 0);
					geoArmorRenderer.applyBoneVisibilityByPart(slot, modelPart, model);
					geoArmorRenderer.renderToBuffer(poseStack, null, packedLight, packedOverlay, Color.WHITE.argbInt());
				}
				else {
					Equippable equippable = armorStack.get(DataComponents.EQUIPPABLE);

					if (equippable != null) {
						equippable.assetId().ifPresent(modelPath -> {
							prepModelPartForRender(poseStack, bone, modelPart);
							renderVanillaArmorPiece(poseStack, animatable, bone, slot, armorStack, equippable, modelPath, model, modelPart, bufferSource, partialTick, packedLight, packedOverlay);
						});
					}
				}

				poseStack.popPose();
			}
		}
	}

	/**
	 * Renders an individual armor piece base on the given {@link GeoBone} and {@link ItemStack}
	 */
	protected void renderVanillaArmorPiece(PoseStack poseStack, T animatable, GeoBone bone, EquipmentSlot slot, ItemStack armorStack,
										   Equippable equippable, ResourceKey<EquipmentAsset> equipmentAsset, HumanoidModel<?> model, ModelPart modelPart,
										   MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
		EquipmentClientInfo.LayerType layerType = slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;

		setVanillaModelPartVisibility(animatable, armorStack, bone, model, modelPart, slot, partialTick);
		this.equipmentRenderer.renderLayers(layerType, equipmentAsset, model, armorStack, poseStack, bufferSource, packedLight);
	}

	/**
	 * @deprecated Use {@link #setVanillaModelPartVisibility(LivingEntity, ItemStack, GeoBone, HumanoidModel, ModelPart, EquipmentSlot, float)}
	 */
	@Deprecated(forRemoval = true)
	protected void setVanillaModelPartVisibility(HumanoidModel<?> baseModel, EquipmentSlot slot) {}

	/**
	 * Spiritual replica of {@link net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer#setPartVisibility(HumanoidModel, EquipmentSlot)}.
	 * <p>
	 * Hide all non-relevant parts for the armor-player model, so it only renders the correct one.
	 * <p>
	 * Because GeckoLib models rely on per-bone rendering for armour part alignment, for the most part we just hide all model parts except the one we've specifically called to render
	 */
	protected void setVanillaModelPartVisibility(T animatable, ItemStack armorStack, GeoBone bone, HumanoidModel<?> baseModel, ModelPart modelPart, EquipmentSlot slot, float partialTick) {
		baseModel.setAllVisible(false);
		modelPart.visible = true;
	}

	/**
	 * Returns a cached instance of a base HumanoidModel that is used for rendering/modelling the provided {@link ItemStack}
	 */
	@NotNull
	protected HumanoidModel<?> getModelForItem(GeoBone bone, EquipmentSlot slot, ItemStack stack, T animatable) {
		HumanoidModel<HumanoidRenderState> defaultModel = slot == EquipmentSlot.LEGS ? GeckoLibClient.GENERIC_INNER_ARMOR_MODEL.get() : GeckoLibClient.GENERIC_OUTER_ARMOR_MODEL.get();
		Model model = GeckoLibServices.Client.ITEM_RENDERING.getArmorModelForItem(animatable, getRenderer() instanceof GeoEntityRenderer<T> entityRenderer && entityRenderer.getEntityRenderState() instanceof HumanoidRenderState renderState ? renderState : new HumanoidRenderState(), stack, slot, slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID, defaultModel);

		return model instanceof HumanoidModel<?> humanoidModel ? humanoidModel : defaultModel;
	}

	/**
	 * Render a given {@link AbstractSkullBlock} as a worn armor piece in relation to a given {@link GeoBone}
	 */
	protected void renderSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock, MultiBufferSource bufferSource, int packedLight) {
		SkullBlock.Type type = skullBlock.getType();
		SkullModelBase model = this.skullModels.apply(type);
		RenderType renderType = SkullBlockRenderer.getRenderType(type, stack.get(DataComponents.PROFILE));

		poseStack.pushPose();
		RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
		poseStack.scale(1.1875f, 1.1875f, 1.1875f);
		poseStack.translate(-0.5f, 0, -0.5f);
		SkullBlockRenderer.renderSkull(null, 0, 0, poseStack, bufferSource, packedLight, model, renderType);
		poseStack.popPose();
	}

	/**
	 * Prepares the given {@link ModelPart} for render by setting its translation, position, and rotation values based on the provided {@link GeoBone}
	 *
	 * @param poseStack The PoseStack being used for rendering
	 * @param bone The GeoBone to base the translations on
	 * @param sourcePart The ModelPart to translate
	 */
	protected void prepModelPartForRender(PoseStack poseStack, GeoBone bone, ModelPart sourcePart) {
		final GeoCube firstCube = bone.getCubes().getFirst();
		final Cube armorCube = sourcePart.cubes.getFirst();
		final double armorBoneSizeX = firstCube.size().x();
		final double armorBoneSizeY = firstCube.size().y();
		final double armorBoneSizeZ = firstCube.size().z();
		final double actualArmorSizeX = Math.abs(armorCube.maxX - armorCube.minX);
		final double actualArmorSizeY = Math.abs(armorCube.maxY - armorCube.minY);
		final double actualArmorSizeZ = Math.abs(armorCube.maxZ - armorCube.minZ);
		float scaleX = (float)(armorBoneSizeX / actualArmorSizeX);
		float scaleY = (float)(armorBoneSizeY / actualArmorSizeY);
		float scaleZ = (float)(armorBoneSizeZ / actualArmorSizeZ);

		sourcePart.setPos(-(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
				-(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
				(bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ));

		sourcePart.xRot = -bone.getRotX();
		sourcePart.yRot = -bone.getRotY();
		sourcePart.zRot = bone.getRotZ();

		poseStack.scale(scaleX, scaleY, scaleZ);
	}
}
