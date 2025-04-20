package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentAssetManager;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.service.GeckoLibClient;
import software.bernie.geckolib.util.RenderUtil;

import java.util.EnumMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Builtin class for handling dynamic armor rendering on GeckoLib entities
 * <p>
 * Supports both {@link GeoItem GeckoLib} and vanilla armor models
 * <p>
 * Unlike a traditional armor renderer, this renderer renders per-bone, giving much more flexible armor rendering
 */
public abstract class ItemArmorGeoLayer<T extends LivingEntity & GeoAnimatable, O, R extends EntityRenderState & GeoRenderState> extends GeoRenderLayer<T, O, R> {
	protected final EquipmentLayerRenderer equipmentRenderer;
	protected final EquipmentAssetManager equipmentAssets;
	protected final Function<SkullBlock.Type, SkullModelBase> skullModels;

	public ItemArmorGeoLayer(GeoRenderer<T, O, R> geoRenderer, EntityRendererProvider.Context context) {
		super(geoRenderer);

		this.equipmentRenderer = context.getEquipmentRenderer();
		this.equipmentAssets = context.getEquipmentAssets();
		this.skullModels = Util.memoize(type -> SkullBlockRenderer.createModel(Minecraft.getInstance().getEntityModels(), type));
	}

	/**
	 * Return a list of the bone names that this layer will render for.
	 * <p>
	 * Ideally, you would cache this list in a static field if you don't need any data from the input renderState or model
	 */
	protected abstract List<RenderData> getRelevantBones(R renderState, BakedGeoModel model);

	/**
	 * Container for data needed to render an armor piece for a bone.
	 *
	 * @param boneName The name of the bone to render the armor piece for
	 * @param slot The slot the armor piece is in
	 * @param modelPartFactory A function that returns a {@link ModelPart} for the armor piece, used to position and scale it
	 */
	public record RenderData(String boneName, EquipmentSlot slot, Function<HumanoidModel<?>, ModelPart> modelPartFactory) {
		public static RenderData head(String boneName) {
			return new RenderData(boneName, EquipmentSlot.HEAD, model -> model.head);
		}

		public static RenderData body(String boneName) {
			return new RenderData(boneName, EquipmentSlot.CHEST, model -> model.body);
		}

		public static RenderData leftArm(String boneName) {
			return new RenderData(boneName, EquipmentSlot.CHEST, model -> model.leftArm);
		}

		public static RenderData rightArm(String boneName) {
			return new RenderData(boneName, EquipmentSlot.CHEST, model -> model.rightArm);
		}

		public static RenderData leftLeg(String boneName) {
			return new RenderData(boneName, EquipmentSlot.LEGS, model -> model.leftLeg);
		}

		public static RenderData rightLeg(String boneName) {
			return new RenderData(boneName, EquipmentSlot.LEGS, model -> model.rightLeg);
		}

		public static RenderData leftFoot(String boneName) {
			return new RenderData(boneName, EquipmentSlot.FEET, model -> model.leftLeg);
		}

		public static RenderData rightFoot(String boneName) {
			return new RenderData(boneName, EquipmentSlot.FEET, model -> model.rightLeg);
		}
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
		EnumMap<EquipmentSlot, ItemStack> equipment = renderState.getOrDefaultGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, new EnumMap<>(EquipmentSlot.class));

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			equipment.put(slot, animatable.getItemBySlot(slot));
		}

		renderState.addGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, equipment);

		if (animatable instanceof LivingEntity livingEntity && !equipment.get(EquipmentSlot.CHEST).isEmpty()) {
			float partialTick = renderState.getGeckolibData(DataTickets.PARTIAL_TICK);

			renderState.addGeckolibData(DataTickets.ELYTRA_ROTATION, new Vec3(livingEntity.elytraAnimationState.getRotX(partialTick),
																			  livingEntity.elytraAnimationState.getRotY(partialTick),
																			  livingEntity.elytraAnimationState.getRotZ(partialTick)));
		}
	}

	/**
	 * Register per-bone render operations, to be rendered after the main model is done.
	 * <p>
	 * Even though the task is called after the main model renders, the {@link PoseStack} provided will be posed as if the bone
	 * is currently rendering.
	 *
	 * @param consumer The registrar to accept the per-bone render tasks
	 */
	@Override
	public void addPerBoneRender(R renderState, BakedGeoModel model, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
		for (RenderData renderData : getRelevantBones(renderState, model)) {
			model.getBone(renderData.boneName).ifPresentOrElse(bone -> createPerBoneRender(bone, renderData, consumer, renderState), () ->
					GeckoLibConstants.LOGGER.error("Unable to find bone for ItemArmorGeoLayer: {}, skipping", renderData.boneName));
		}
	}

	private void createPerBoneRender(GeoBone bone, RenderData renderData, BiConsumer<GeoBone, PerBoneRender<R>> consumer, R renderState) {
		ItemStack stack = getEquipmentStack(bone, renderData.slot, renderState);

		if (!stack.isEmpty()) {
			consumer.accept(bone, (renderState2, poseStack, bone2, renderType, bufferSource, packedLight, packedOverlay, renderColor) ->
					renderForBone(renderState2, renderData.slot, renderData.modelPartFactory, stack, poseStack, bone2, renderType, bufferSource, packedLight, packedOverlay, renderColor));
		}
	}

	/**
	 * Perform the actual rendering operation for the given bone and equipment
	 */
	protected void renderForBone(R renderState, EquipmentSlot slot, Function<HumanoidModel<?>, ModelPart> modelPartFactory, ItemStack equipmentStack, PoseStack poseStack, GeoBone bone,
								 RenderType renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay, int renderColor) {
		if (equipmentStack.isEmpty())
			return;

		if (equipmentStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractSkullBlock skullBlock) {
			renderSkullAsArmor(poseStack, bone, equipmentStack, skullBlock, bufferSource, packedLight);
		}
		else {
			HumanoidModel<?> model = getHumanoidModelForRender(bone, slot, equipmentStack, renderState);
			ModelPart modelPart = modelPartFactory.apply(model);

			if (!modelPart.cubes.isEmpty()) {
				poseStack.pushPose();
				poseStack.scale(-1, -1, 1);

				if (model instanceof GeoArmorRenderer<?, ?> geoArmorRenderer) {
					prepHumanoidModelForRender(poseStack, bone, modelPart);
					geoArmorRenderer.applyBoneVisibilityByPart(slot, modelPart, model);
					geoArmorRenderer.renderToBuffer(poseStack, null, packedLight, packedOverlay, 0xFFFFFFFF);
				}
				else {
					Equippable equippable = equipmentStack.get(DataComponents.EQUIPPABLE);

					if (equippable != null) {
						equippable.assetId().ifPresent(assetId -> {
							prepHumanoidModelForRender(poseStack, bone, modelPart);
							renderVanillaArmorPiece(poseStack, renderState, bone, slot, equipmentStack, equippable, assetId, model, modelPart, bufferSource, packedLight, packedOverlay);
						});
					}
				}

				poseStack.popPose();
			}
		}
	}

	/**
	 * Helper method to retrieve a stored held or worn ItemStack by the slot it's in, as computed in {@link #addRenderData(T, O, R)}
	 */
	protected ItemStack getEquipmentStack(GeoBone bone, EquipmentSlot slot, R renderState) {
		return (ItemStack)renderState.getGeckolibData(DataTickets.EQUIPMENT_BY_SLOT).getOrDefault(slot, ItemStack.EMPTY);
	}

	/**
	 * Get the LayerType for the given armor piece. This defines the asset type to use in rendering a vanilla armor piece.
	 */
	protected EquipmentClientInfo.LayerType getEquipmentLayerType(R renderState, GeoBone bone, EquipmentSlot slot, ItemStack armorStack, ResourceKey<EquipmentAsset> assetId) {
		if (slot == EquipmentSlot.LEGS)
			return EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS;

		if (slot == EquipmentSlot.CHEST && !this.equipmentAssets.get(assetId).getLayers(EquipmentClientInfo.LayerType.WINGS).isEmpty())
			return EquipmentClientInfo.LayerType.WINGS;

		return EquipmentClientInfo.LayerType.HUMANOID;
	}

	/**
	 * Renders an individual armor piece base on the given {@link GeoBone} and {@link ItemStack}
	 */
	protected void renderVanillaArmorPiece(PoseStack poseStack, R renderState, GeoBone bone, EquipmentSlot slot, ItemStack armorStack,
										   Equippable equippable, ResourceKey<EquipmentAsset> assetId, HumanoidModel<?> model, ModelPart modelPart,
										   MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		EquipmentClientInfo.LayerType layerType = getEquipmentLayerType(renderState, bone, slot, armorStack, assetId);
		Model modelToRender = model;

		if (layerType == EquipmentClientInfo.LayerType.WINGS) {
			if (modelPart != model.body)
				return;

			modelToRender = checkForElytraModel(layerType, renderState, bone, poseStack);
		}
		else {
			setVanillaModelPartVisibility(renderState, armorStack, bone, model, modelPart, slot);
		}

		this.equipmentRenderer.renderLayers(layerType, assetId, modelToRender,
											armorStack, poseStack, bufferSource, packedLight);
	}

	/**
	 * Check for the presence of {@link ElytraModel Elytra} wings, and adjust the model as necessary
	 */
	protected Model checkForElytraModel(EquipmentClientInfo.LayerType layerType, R renderState, GeoBone bone, PoseStack poseStack) {
		ElytraModel model = GeckoLibClient.GENERIC_ELYTRA_MODEL.get();
		HumanoidRenderState humanoidRenderState = new HumanoidRenderState();
		Vec3 elytraRotation = renderState.getOrDefaultGeckolibData(DataTickets.ELYTRA_ROTATION, Vec3.ZERO);
		humanoidRenderState.isCrouching = renderState.getGeckolibData(DataTickets.IS_CROUCHING);
		humanoidRenderState.elytraRotX = (float)elytraRotation.x;
		humanoidRenderState.elytraRotY = (float)elytraRotation.y;
		humanoidRenderState.elytraRotZ = (float)elytraRotation.z;

		model.setupAnim(humanoidRenderState);
		poseStack.translate(0, -1.5f, 0.125f);

		return model;
	}

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
	@ApiStatus.Internal
	protected <S extends HumanoidRenderState & GeoRenderState> HumanoidModel<?> getHumanoidModelForRender(GeoBone bone, EquipmentSlot slot, ItemStack stack, R renderState) {
		S humanoidRenderState = renderState instanceof HumanoidRenderState humanoidRenderState1 ? (S)humanoidRenderState1 : (S)new HumanoidRenderState();
		EquipmentClientInfo.LayerType layerType = slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
		HumanoidModel defaultModel = slot == EquipmentSlot.LEGS ? GeckoLibClient.GENERIC_INNER_ARMOR_MODEL.get() : GeckoLibClient.GENERIC_OUTER_ARMOR_MODEL.get();

		return GeckoLibServices.Client.ITEM_RENDERING.<S>getArmorModelForItem(humanoidRenderState, stack, slot, layerType, defaultModel);
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
	protected void prepHumanoidModelForRender(PoseStack poseStack, GeoBone bone, ModelPart sourcePart) {
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
