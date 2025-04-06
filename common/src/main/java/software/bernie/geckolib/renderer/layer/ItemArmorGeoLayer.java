package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
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
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.service.GeckoLibClient;
import software.bernie.geckolib.util.RenderUtil;

import java.util.function.Function;

/**
 * Builtin class for handling dynamic armor rendering on GeckoLib entities
 * <p>
 * Supports both {@link GeoItem GeckoLib} and vanilla armor models
 * <p>
 * Unlike a traditional armor renderer, this renderer renders per-bone, giving much more flexible armor rendering
 */
public class ItemArmorGeoLayer<T extends LivingEntity & GeoAnimatable, O, R extends EntityRenderState & GeoRenderState> extends GeoRenderLayer<T, O, R> {
	protected final EquipmentLayerRenderer equipmentRenderer;
	protected final Function<SkullBlock.Type, SkullModelBase> skullModels;

	public ItemArmorGeoLayer(GeoRenderer<T, O, R> geoRenderer, EquipmentLayerRenderer equipmentLayerRenderer) {
		super(geoRenderer);

		this.equipmentRenderer = equipmentLayerRenderer;
		this.skullModels = Util.memoize(type -> SkullBlockRenderer.createModel(Minecraft.getInstance().getEntityModels(), type));
	}

	/**
	 * Override to add any custom {@link DataTicket}s you need to capture for rendering.
	 * <p>
	 * The animatable is discarded from the rendering context after this, so any data needed
	 * for rendering should be captured in the renderState provided
	 *
	 * @param animatable The animatable instance being rendered
	 * @param relatedObject An object related to the render pass or null if not applicable.
	 *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	 * @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
	 */
	@Override
	public void addRenderData(T animatable, O relatedObject, R renderState) {
		Reference2ReferenceOpenHashMap<ItemStack, EquipmentSlot> wornArmor = new Reference2ReferenceOpenHashMap<>(4);
		Reference2ReferenceOpenHashMap<EquipmentSlot, ItemStack> equipment = new Reference2ReferenceOpenHashMap<>(6);

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
				ItemStack stack = animatable.getItemBySlot(slot);

				wornArmor.put(stack, slot);
				equipment.put(slot, stack);
			}
			else if (slot.getType() == EquipmentSlot.Type.HAND) {
				ItemStack stack = animatable.getItemBySlot(slot);

				equipment.put(slot, stack);
			}
		}

		renderState.addGeckolibData(DataTickets.WORN_ARMOR_BY_STACK, wornArmor);
		renderState.addGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, equipment);
	}

	/**
	 * Helper method to retrieve a stored held or worn ItemStack by the slot it's in
	 */
	protected ItemStack getStackForSlot(EquipmentSlot slot, R renderState) {
		return (ItemStack)renderState.getGeckolibData(DataTickets.EQUIPMENT_BY_SLOT).getOrDefault(slot, ItemStack.EMPTY);
	}

	/**
	 * Return an EquipmentSlot for a given {@link ItemStack} and animatable instance
	 * <p>
	 * This is what determines the base model to use for rendering a particular stack
	 */
	@NotNull
	protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, R renderState) {
		return (EquipmentSlot)renderState.getGeckolibData(DataTickets.WORN_ARMOR_BY_STACK).getOrDefault(stack, EquipmentSlot.CHEST);
	}

	/**
	 * Return a ModelPart for a given {@link GeoBone}
	 * <p>
	 * This is then transformed into position for the final render
	 */
	@NotNull
	protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, R renderState, HumanoidModel<?> baseModel) {
		return baseModel.body;
	}

	/**
	 * Get the {@link ItemStack} relevant to the bone being rendered
	 *
	 * @return The ItemStack for the bone being rendered, or null if this bone should be ignored
	 */
	@Nullable
	protected ItemStack getArmorItemForBone(GeoBone bone, R renderState) {
		return null;
	}

	/**
	 * This method is called by the {@link GeoRenderer} for each bone being rendered
	 * <p>
	 * You would use this to render something at or for a given GeoBone's position and orientation.
	 * <p>
	 * You <b><u>MUST NOT</u></b> perform any rendering operations here, and instead must contain all your functionality in the returned Runnable
	 */
	@Nullable
	@Override
	public Runnable createPerBoneRender(R renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource) {
		ItemStack armorStack = getArmorItemForBone(bone, renderState);

		if (armorStack == null || armorStack.isEmpty())
			return null;

		return () -> {
			int packedLight = renderState.getGeckolibData(DataTickets.PACKED_LIGHT);
			int packedOverlay = renderState.getGeckolibData(DataTickets.PACKED_OVERLAY);

			if (armorStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractSkullBlock skullBlock) {
				renderSkullAsArmor(poseStack, bone, armorStack, skullBlock, bufferSource, packedLight);
			}
			else {
				EquipmentSlot slot = getEquipmentSlotForBone(bone, armorStack, renderState);
				HumanoidModel<?> model = getModelForItem(bone, slot, armorStack, renderState);
				ModelPart modelPart = getModelPartForBone(bone, slot, armorStack, renderState, model);

				if (!modelPart.cubes.isEmpty()) {
					poseStack.pushPose();
					poseStack.scale(-1, -1, 1);

					if (model instanceof GeoArmorRenderer<?, ?> geoArmorRenderer) {
						prepModelPartForRender(poseStack, bone, modelPart);
						geoArmorRenderer.applyBoneVisibilityByPart(slot, modelPart, model);
						geoArmorRenderer.renderToBuffer(poseStack, null, packedLight, packedOverlay, 0xFFFFFFFF);
					}
					else {
						Equippable equippable = armorStack.get(DataComponents.EQUIPPABLE);

						if (equippable != null) {
							equippable.assetId().ifPresent(modelPath -> {
								prepModelPartForRender(poseStack, bone, modelPart);
								renderVanillaArmorPiece(poseStack, renderState, bone, slot, armorStack, equippable, modelPath, model, modelPart, bufferSource, packedLight, packedOverlay);
							});
						}
					}

					poseStack.popPose();
				}
			}
		};
	}

	/**
	 * Renders an individual armor piece base on the given {@link GeoBone} and {@link ItemStack}
	 */
	protected void renderVanillaArmorPiece(PoseStack poseStack, R renderState, GeoBone bone, EquipmentSlot slot, ItemStack armorStack,
										   Equippable equippable, ResourceKey<EquipmentAsset> equipmentAsset, HumanoidModel<?> model, ModelPart modelPart,
										   MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		EquipmentClientInfo.LayerType layerType = slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;

		setVanillaModelPartVisibility(renderState, armorStack, bone, model, modelPart, slot);
		this.equipmentRenderer.renderLayers(layerType, equipmentAsset, model, armorStack, poseStack, bufferSource, packedLight);
	}

	/**
	 * @deprecated Use {@link #setVanillaModelPartVisibility(EntityRenderState, ItemStack, GeoBone, HumanoidModel, ModelPart, EquipmentSlot)}
	 */
	@Deprecated(forRemoval = true)
	protected void setVanillaModelPartVisibility(HumanoidModel<?> baseModel, EquipmentSlot slot) {}

	/**
	 * Spiritual replica of {@code HumanoidArmorLayer#setPartVisibility}.
	 * <p>
	 * Hide all non-relevant parts for the armor-player model, so it only renders the correct one.
	 * <p>
	 * Because GeckoLib models rely on per-bone rendering for armour part alignment, for the most part we just hide all model parts except the one we've specifically called to render
	 */
	protected void setVanillaModelPartVisibility(R renderState, ItemStack armorStack, GeoBone bone, HumanoidModel<?> baseModel, ModelPart modelPart, EquipmentSlot slot) {
		baseModel.setAllVisible(false);
		modelPart.visible = true;
	}

	/**
	 * Returns a cached instance of a base HumanoidModel that is used for rendering/modelling the provided {@link ItemStack}
	 */
	@NotNull
	protected <S extends HumanoidRenderState & GeoRenderState> HumanoidModel<?> getModelForItem(GeoBone bone, EquipmentSlot slot, ItemStack stack, R renderState) {
		HumanoidModel<HumanoidRenderState> defaultModel = slot == EquipmentSlot.LEGS ? GeckoLibClient.GENERIC_INNER_ARMOR_MODEL.get() : GeckoLibClient.GENERIC_OUTER_ARMOR_MODEL.get();
		Model model = GeckoLibServices.Client.ITEM_RENDERING.<S>getArmorModelForItem(renderState instanceof HumanoidRenderState humanoidRenderState ? (S)humanoidRenderState : (S)new HumanoidRenderState(), stack, slot, slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID, (HumanoidModel)defaultModel);

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
	 * <p>
	 * This implementation uses the <b><u>FIRST</u></b> cube in the source part
	 * to determine the scale and position of the GeoArmor to be rendered
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
