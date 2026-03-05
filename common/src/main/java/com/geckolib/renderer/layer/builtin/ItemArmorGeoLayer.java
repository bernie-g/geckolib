package com.geckolib.renderer.layer.builtin;

import com.geckolib.GeckoLibClientServices;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.cuboid.CuboidGeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.object.VanillaModelModifier;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.geckolib.service.GeckoLibClient;
import com.geckolib.util.RenderStateUtil;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/// Builtin class for handling dynamic worn-item rendering on GeckoLib entities
///
/// Supports both [GeckoLib][GeoItem] and vanilla item and armor models
///
/// Unlike a traditional armor renderer, this renderer renders per-bone, giving much more flexible armor rendering
///
/// @param <T> Animatable class type. Inherited from the renderer this layer is attached to
/// @param <O> Associated object class type, or [Void] if none. Inherited from the renderer this layer is attached to
/// @param <R> RenderState class type. Inherited from the renderer this layer is attached to
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class ItemArmorGeoLayer<T extends LivingEntity & GeoAnimatable, O, R extends EntityRenderState & GeoRenderState> extends GeoRenderLayer<T, O, R> {
	@SuppressWarnings("DataFlowIssue")
    protected static final Function<SkullBlock.Type, @Nullable SkullModelBase> SKULL_MODELS = Util.memoize(type -> SkullBlockRenderer.createModel(Minecraft.getInstance().getEntityModels(), type));
	protected final EquipmentLayerRenderer equipmentRenderer;
	protected final EquipmentAssetManager equipmentAssets;
    protected final PlayerSkinRenderCache skinCache;
    protected final ItemModelResolver itemModelResolver;

	public ItemArmorGeoLayer(GeoRenderer<T, O, R> geoRenderer, EntityRendererProvider.Context context) {
		super(geoRenderer);

		this.equipmentRenderer = context.getEquipmentRenderer();
		this.equipmentAssets = context.getEquipmentAssets();
        this.skinCache = context.getPlayerSkinRenderCache();
		this.itemModelResolver = context.getItemModelResolver();
	}

	/// Return a list of the bone names that this layer will render for.
	///
	/// Ideally, you would cache this list in a static field if you don't need any data from the input renderState or model
	protected abstract List<RenderData> getRelevantBones(RenderPassInfo<R> renderPassInfo);

	/// Container for data needed to render an armor piece for a bone.
	///
	/// @param boneName The name of the bone to render the armor piece for
	/// @param armorSegment The armor segment to render
	public record RenderData(String boneName, GeoArmorRenderer.ArmorSegment armorSegment) {
		/// Create a [RenderData] container for a head slot item with the given bone name
		public static RenderData head(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.HEAD);
		}

		/// Create a [RenderData] container for a chest slot item with the given bone name
		public static RenderData body(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.CHEST);
		}

		/// Create a [RenderData] container for a left-arm slot item with the given bone name
		public static RenderData leftArm(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.LEFT_ARM);
		}

		/// Create a [RenderData] container for a right-arm slot item with the given bone name
		public static RenderData rightArm(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.RIGHT_ARM);
		}

		/// Create a [RenderData] container for a left-leg slot item with the given bone name
		public static RenderData leftLeg(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.LEFT_LEG);
		}

		/// Create a [RenderData] container for a right-leg slot item with the given bone name
		public static RenderData rightLeg(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.RIGHT_LEG);
		}

		/// Create a [RenderData] container for a left-foot slot item with the given bone name
		public static RenderData leftFoot(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.LEFT_FOOT);
		}

		/// Create a [RenderData] container for a right-foot slot item with the given bone name
		public static RenderData rightFoot(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.RIGHT_FOOT);
		}
	}

	//<editor-fold defaultstate="collapsed" desc="<Data Collection>">
	/// Override to add any custom [DataTicket]s you need to capture for rendering.
	///
	/// The animatable is discarded from the rendering context after this, so any data needed
	/// for rendering should be captured in the renderState provided
	///
	/// @param animatable The animatable instance being rendered
	/// @param relatedObject An object related to the render pass or null if not applicable.
	///                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	/// @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
	/// @param partialTick The fraction of a tick that has passed since the previous tick
	@Override
	public void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {
		final EnumMap<EquipmentSlot, ItemStack> equipment = renderState.getOrDefaultGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, new EnumMap<>(EquipmentSlot.class));

        collectArmorData(renderState, animatable, partialTick, equipment);

		if (!equipment.get(EquipmentSlot.CHEST).isEmpty()) {
			renderState.addGeckolibData(DataTickets.ELYTRA_ROTATION, new Vec3(animatable.elytraAnimationState.getRotX(partialTick),
																			  animatable.elytraAnimationState.getRotY(partialTick),
																			  animatable.elytraAnimationState.getRotZ(partialTick)));
		}
	}

	/// Collect the base data this [GeoRenderLayer] uses to function
    protected <S extends HumanoidRenderState & GeoRenderState, A extends HumanoidModel<S>> void collectArmorData(
			R baseRenderState, T animatable, float partialTick, EnumMap<EquipmentSlot, ItemStack> equipment) {
        S headRenderState = getOrCreateHumanoidRenderState(baseRenderState, false);

        equipment.put(EquipmentSlot.HEAD, headRenderState.headEquipment = animatable.getItemBySlot(EquipmentSlot.HEAD));
        equipment.put(EquipmentSlot.CHEST, headRenderState.chestEquipment = animatable.getItemBySlot(EquipmentSlot.CHEST));
        equipment.put(EquipmentSlot.LEGS, headRenderState.legsEquipment = animatable.getItemBySlot(EquipmentSlot.LEGS));
        equipment.put(EquipmentSlot.FEET, headRenderState.feetEquipment = animatable.getItemBySlot(EquipmentSlot.FEET));

        GeoArmorRenderer.captureRenderStates(headRenderState, animatable, partialTick,
                                             (_, slot) -> (A)GeckoLibClient.HUMANOID_ARMOR_MODEL.get().get(slot),
                                             slot -> slot == EquipmentSlot.HEAD ? headRenderState : getOrCreateHumanoidRenderState(baseRenderState, true));

        baseRenderState.addGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, equipment);

        if (headRenderState != baseRenderState && headRenderState.hasGeckolibData(DataTickets.PER_SLOT_RENDER_DATA))
            baseRenderState.addGeckolibData(DataTickets.PER_SLOT_RENDER_DATA, Objects.requireNonNull(headRenderState.getGeckolibData(DataTickets.PER_SLOT_RENDER_DATA)));

		final ItemStack headStack = equipment.get(EquipmentSlot.HEAD);

		if (!headStack.isEmpty() && !HumanoidArmorLayer.shouldRender(headStack, EquipmentSlot.HEAD) &&
			baseRenderState instanceof LivingEntityRenderState livingRenderState && livingRenderState.headItem.isEmpty())
			this.itemModelResolver.updateForLiving(livingRenderState.headItem, headStack, ItemDisplayContext.HEAD, animatable);
    }
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="<Render Submission>">
	/// This is the method actually called by the render for your render layer to function
	///
	/// This is called _after_ the animatable has been submitted for rendering, but before supplementary rendering submissions like nametags
	@Override
	public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
		for (RenderData renderData : getRelevantBones(renderPassInfo)) {
			renderPassInfo.poseStack().pushPose();
			renderPassInfo.model().getBone(renderData.boneName)
					.ifPresentOrElse(bone -> submitRenderForBone(renderPassInfo, renderTasks, renderData, bone),
									 () -> GeckoLibConstants.LOGGER.error("Unable to find bone {} for ItemArmorGeoLayer in model `{}`, skipping", renderData.boneName, renderPassInfo.model().properties().resourcePath()));
			renderPassInfo.poseStack().popPose();
		}
	}

	/// Compile and submit the render task for a specified armor piece
	protected void submitRenderForBone(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks, RenderData renderData, GeoBone bone) {
		final ItemStack stack = getEquipmentStack(renderPassInfo, bone, renderData);
		final EquipmentSlot slot = renderData.armorSegment.equipmentSlot;

		if (!stack.isEmpty()) {
			renderPassInfo.renderPosed(() -> {
				final SkullBlock.Type skullType = slot == EquipmentSlot.HEAD ? findSkullBlockType(renderPassInfo, stack) : null;

				if (skullType != null) {
					submitSkullBlockRender(renderPassInfo, renderTasks, renderData, bone, stack, skullType);
				}
				else if (RenderUtil.getGeckoLibArmorRenderer(stack, slot) instanceof GeoArmorRenderer armorRenderer) {
					submitGeckoLibArmorRender(renderPassInfo, renderTasks, renderData, bone, stack, armorRenderer);
				}
				else if (!HumanoidArmorLayer.shouldRender(stack, slot)) {
					submitVanillaWornItemRender(renderPassInfo, renderTasks, renderData, bone, stack);
				}
				else {
					submitVanillaEquippableRender(renderPassInfo, renderTasks, renderData, bone, stack);
				}
			});
		}
	}

	/// Helper method to retrieve a stored held or worn ItemStack by the slot it's in, as computed in [GeoRenderLayer#addRenderData(GeoAnimatable, Object, GeoRenderState, float)]
	protected ItemStack getEquipmentStack(RenderPassInfo<R> renderPassInfo, GeoBone bone, RenderData renderData) {
		return renderPassInfo.getOrDefaultGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, new EnumMap<>(EquipmentSlot.class))
				.getOrDefault(renderData.armorSegment.equipmentSlot, ItemStack.EMPTY);
	}
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="<Skull Blocks>">
	/// Render a given [AbstractSkullBlock] as a worn item on a specified [GeoBone]
	protected void submitSkullBlockRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks, RenderData renderData, GeoBone bone, ItemStack stack, SkullBlock.Type skullType) {
		final SkullModelBase skullModel = SKULL_MODELS.apply(skullType);
		final RenderType renderType = getSkullBlockRenderType(renderPassInfo, skullType, stack);
		final PoseStack poseStack = renderPassInfo.poseStack();

		if (skullModel == null) {
			GeckoLibConstants.LOGGER.error("Cannot render skull block that doesn't have a registered model! Skull type: {}", skullType);

			return;
		}

		poseStack.pushPose();
		RenderUtil.transformToBone(poseStack, bone);
		poseStack.scale(CustomHeadLayer.SKULL_SCALE, CustomHeadLayer.SKULL_SCALE, CustomHeadLayer.SKULL_SCALE);
		poseStack.translate(-0.5f, 0, -0.5f);
		SkullBlockRenderer.submitSkull(0, poseStack, renderTasks, renderPassInfo.packedLight(), skullModel, renderType, renderPassInfo.renderState().outlineColor, null);
		poseStack.popPose();
	}

	/// Try to identify a [SkullBlock.Type] for the current render pass, either by an already established [LivingEntityRenderState#wornHeadType] or a worn [AbstractSkullBlock]
	protected SkullBlock.@Nullable Type findSkullBlockType(RenderPassInfo<R> renderPassInfo, ItemStack slotStack) {
		if (renderPassInfo.renderState() instanceof LivingEntityRenderState livingState && livingState.wornHeadType != null)
			return livingState.wornHeadType;

		if (slotStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractSkullBlock skullBlock)
			return skullBlock.getType();

		return null;
	}

	/// Get the [RenderType] for a given [SkullBlock.Type] for this render pass
	protected RenderType getSkullBlockRenderType(RenderPassInfo<R> renderPassInfo, SkullBlock.Type skullType, ItemStack wornStack) {
		if (skullType == SkullBlock.Types.PLAYER) {
			final ResolvableProfile skullProfile = renderPassInfo.renderState() instanceof LivingEntityRenderState livingState && livingState.wornHeadProfile != null ?
												   livingState.wornHeadProfile :
												   wornStack.get(DataComponents.PROFILE);

			if (skullProfile != null)
				return this.skinCache.getOrDefault(skullProfile).renderType();
		}

		return SkullBlockRenderer.getSkullRenderType(skullType, null);
	}
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="<GeckoLib Armor>">
	/// Render a given [GeoArmorRenderer](GeckoLib armor) piece as worn on a specified [GeoBone]
	protected void submitGeckoLibArmorRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks, RenderData renderData, GeoBone bone, ItemStack stack, GeoArmorRenderer armorRenderer) {
		final EnumMap<EquipmentSlot, R> perSlotData = (EnumMap<EquipmentSlot, R>)renderPassInfo.renderState().getGeckolibData(DataTickets.PER_SLOT_RENDER_DATA);
		final R slotRenderState;
		final PoseStack poseStack = renderPassInfo.poseStack();
		final EquipmentSlot slot = renderData.armorSegment.equipmentSlot;

		if (perSlotData == null || (slotRenderState = perSlotData.get(slot)) == null)
			return;

		final HumanoidRenderState humanoidRenderState = getOrCreateHumanoidRenderState(slotRenderState, false);
		final HumanoidModel<?> humanoidModel = humanoidRenderState.getGeckolibData(DataTickets.HUMANOID_MODEL);

		if (humanoidModel == null)
			return;

		final ModelPart modelPart = renderData.armorSegment.modelPartGetter.apply(humanoidModel);
		final Vec3 relativeScale = getScaleFactorForBone(bone, modelPart);
		final List<RenderPassInfo.BoneUpdater<R>> boneUpdaters = ObjectArrayList.of(
				positionModelPartFromBone(renderPassInfo, bone, modelPart, humanoidModel, renderData, armorRenderer, humanoidRenderState, relativeScale),
				hideUnusedBones(renderPassInfo, renderData, bone, stack, armorRenderer, humanoidRenderState));

		poseStack.pushPose();
		RenderUtil.transformToBone(poseStack, bone);
		bone.translateAwayFromPivotPoint(poseStack);
		poseStack.scale(-1, -1, 1);
		poseStack.scale((float)relativeScale.x, (float)relativeScale.y, (float)relativeScale.z);
		armorRenderer.performRenderPass(humanoidRenderState, poseStack, renderTasks, renderPassInfo.cameraState(), boneUpdaters);
		poseStack.popPose();
	}

	/// Prepares the given [ModelPart] for render by setting its translation, position, and rotation values based on the provided [GeoBone]
	///
	/// Additionally, scales the [PoseStack] if the bone is a [CuboidGeoBone] to match the size of the [ModelPart]
	protected RenderPassInfo.BoneUpdater<R> positionModelPartFromBone(RenderPassInfo<R> renderPassInfo, GeoBone wearerBone, ModelPart modelPart, HumanoidModel humanoidModel, RenderData renderData,
																	  GeoArmorRenderer<?, HumanoidRenderState> armorRenderer, HumanoidRenderState humanoidRenderState, Vec3 relativeScale) {
		return (_, snapshots) -> {
			final List<GeoArmorRenderer.ArmorSegment> segments = armorRenderer.getSegmentsForSlot(humanoidRenderState, renderData.armorSegment.equipmentSlot);

			if (segments.isEmpty())
				return;

			final BoneSnapshot wearerSnapshot = wearerBone.frameSnapshot;

			humanoidModel.setupAnim(humanoidRenderState);
			modelPart.setPos(-(wearerBone.pivotX() - ((wearerBone.pivotX() * (float)relativeScale.x) - wearerBone.pivotX()) / (float)relativeScale.x),
							  -(wearerBone.pivotY() - ((wearerBone.pivotY() * (float)relativeScale.y) - wearerBone.pivotY()) / (float)relativeScale.y),
							  (wearerBone.pivotZ() - ((wearerBone.pivotZ() * (float)relativeScale.z) - wearerBone.pivotZ()) / (float)relativeScale.z));

			modelPart.xRot = wearerSnapshot == null ? 0 : -wearerSnapshot.getRotX();
			modelPart.yRot = wearerSnapshot == null ? 0 : -wearerSnapshot.getRotY();
			modelPart.zRot = wearerSnapshot == null ? 0 : wearerSnapshot.getRotZ();

			for (GeoArmorRenderer.ArmorSegment segment : segments) {
				snapshots.get(armorRenderer.getBoneNameForSegment(humanoidRenderState, segment)).ifPresent(snapshot -> {
					final ModelPart segmentPart = segment.modelPartGetter.apply(humanoidModel);
					final Vector3f bonePos = segment.modelPartMatcher.apply(new Vector3f(segmentPart.x, segmentPart.y, segmentPart.z));

					snapshot.setRotX(-segmentPart.xRot)
							.setRotY(-segmentPart.yRot)
							.setRotZ(segmentPart.zRot)
							.setTranslateX(bonePos.x)
							.setTranslateY(bonePos.y)
							.setTranslateZ(bonePos.z);
				});
			}
		};
	}

	/// Hide all [GeoBone]s of a given [BakedGeoModel] that aren't the used bone for this render section
	protected RenderPassInfo.BoneUpdater<R> hideUnusedBones(RenderPassInfo<R> renderPassInfo, RenderData renderData, GeoBone bone, ItemStack stack, GeoArmorRenderer armorRenderer,
															HumanoidRenderState humanoidRenderState) {
		return (_, snapshots) -> {
			for (Object segment : armorRenderer.getSegmentsForSlot(humanoidRenderState, renderData.armorSegment.equipmentSlot)) {
				if (segment != renderData.armorSegment) {
					snapshots.get(armorRenderer.getBoneNameForSegment(humanoidRenderState, (GeoArmorRenderer.ArmorSegment)segment))
							.ifPresent(bone2 -> bone2.skipRender(true).skipChildrenRender(true));
				}
			}
		};
	}
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="<Vanilla Non-Equippable Items>">
	/// Render a non-[Equippable] [ItemStack] on a specified [GeoBone]
	protected void submitVanillaWornItemRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks, RenderData renderData, GeoBone bone, ItemStack stack) {
		final PoseStack poseStack = renderPassInfo.poseStack();
		final ItemStackRenderState itemRenderState = getNonEquippableItemRenderState(renderPassInfo, renderData, bone, stack);

		if (itemRenderState == null)
			return;

		poseStack.pushPose();
		RenderUtil.transformToBone(poseStack, bone);
		poseStack.translate(0, 0.25f, 0);
		poseStack.scale(CustomHeadLayer.ITEM_SCALE, CustomHeadLayer.ITEM_SCALE, CustomHeadLayer.ITEM_SCALE);
		itemRenderState.submit(poseStack, renderTasks, renderPassInfo.packedLight(), OverlayTexture.NO_OVERLAY, renderPassInfo.renderState().outlineColor);
		poseStack.popPose();
	}

	/// Retrieve or create an [ItemStackRenderState] for the given [ItemStack] to render a non-[Equippable] item
	protected @Nullable ItemStackRenderState getNonEquippableItemRenderState(RenderPassInfo<R> renderPassInfo, RenderData renderData, GeoBone bone, ItemStack stack) {
		final EquipmentSlot slot = renderData.armorSegment.equipmentSlot;
		final R renderState = renderPassInfo.renderState();

		if (slot == EquipmentSlot.HEAD && renderState instanceof LivingEntityRenderState livingRenderState && !livingRenderState.headItem.isEmpty())
			return livingRenderState.headItem;

		final ItemStackRenderState itemRenderState = new ItemStackRenderState();
		final ItemDisplayContext displayContext = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE, ItemDisplayContext.NONE);

		this.itemModelResolver.updateForTopItem(itemRenderState, stack, displayContext, null, null, displayContext.ordinal());

		return itemRenderState;
	}
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="<Vanilla Equippable>">
	/// Render a given [Equippable]'s model on a specified [GeoBone]
	protected void submitVanillaEquippableRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks, RenderData renderData, GeoBone bone, ItemStack stack) {
		final Model<?> equippedModel = getEquippableModel(renderPassInfo, renderData, bone, stack);
		final ModelPart modelPart = equippedModel instanceof HumanoidModel<?> humanoidModel ? renderData.armorSegment.modelPartGetter.apply(humanoidModel) : equippedModel.root();
		final Equippable equippable = stack.get(DataComponents.EQUIPPABLE);

		if (equippable == null || equippable.assetId().isEmpty() || modelPart.cubes.isEmpty())
			return;

		final ResourceKey<EquipmentAsset> assetId = equippable.assetId().get();
		final PoseStack poseStack = renderPassInfo.poseStack();
		final Vec3 relativeScale = getScaleFactorForBone(bone, modelPart);

		poseStack.pushPose();
		RenderUtil.transformToBone(poseStack, bone);
		bone.translateAwayFromPivotPoint(poseStack);
		poseStack.scale(-1, -1, 1);
		poseStack.scale((float)relativeScale.x, (float)relativeScale.y, (float)relativeScale.z);
		renderVanillaEquippable(renderPassInfo, renderTasks, renderData, bone, stack, poseStack, equippedModel, modelPart, assetId);
		poseStack.popPose();
	}

	/// Renders an individual [Equippable] model piece base on the given [GeoBone] and [ItemStack]
	protected void renderVanillaEquippable(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks, RenderData renderData, GeoBone bone, ItemStack stack,
										   PoseStack poseStack, Model<?> equippedModel, ModelPart modelPart, ResourceKey<EquipmentAsset> assetId) {
		final EquipmentClientInfo.LayerType layerType = getEquipmentLayerType(renderPassInfo, renderData, bone, stack, equippedModel, modelPart, assetId);
		final HumanoidRenderState cleanRenderState = getOrCreateHumanoidRenderState(renderPassInfo.renderState(), false);

		if (layerType == EquipmentClientInfo.LayerType.WINGS) {
			if (equippedModel instanceof HumanoidModel humanoidModel && modelPart != humanoidModel.body)
				return;

			equippedModel = checkForElytraModel(renderPassInfo, layerType, bone, poseStack);
		}

		if (equippedModel instanceof HumanoidModel humanoidModel) {
			final Vec3 relativeScale = getScaleFactorForBone(bone, modelPart);

			VanillaModelModifier.addModifierToState(cleanRenderState, humanoidModel, setUnusedModelPartVisibility(modelPart, humanoidModel));
			VanillaModelModifier.addModifierToState(cleanRenderState, humanoidModel, VanillaModelModifier.ofSetupOnly(_ -> {
				modelPart.setPos(-(bone.pivotX() - ((bone.pivotX() * (float)relativeScale.x) - bone.pivotX()) / (float)relativeScale.x),
								 -(bone.pivotY() - ((bone.pivotY() * (float)relativeScale.y) - bone.pivotY()) / (float)relativeScale.y),
								 (bone.pivotZ() - ((bone.pivotZ() * (float)relativeScale.z) - bone.pivotZ()) / (float)relativeScale.z));
			}));
		}

		this.equipmentRenderer.renderLayers(layerType, assetId, (Model)equippedModel, cleanRenderState, stack, poseStack, renderTasks,
											renderPassInfo.packedLight(), cleanRenderState.outlineColor);
	}
	///  Get the [Model] instance to render for a given [Equippable] [ItemStack]
	protected <S extends HumanoidRenderState & GeoRenderState> Model<?> getEquippableModel(RenderPassInfo<R> renderPassInfo, RenderData renderData, GeoBone bone, ItemStack stack) {
		final S humanoidRenderState = renderPassInfo.renderState() instanceof HumanoidRenderState humanoidRenderState1 ? (S)humanoidRenderState1 : (S)new HumanoidRenderState();
		final EquipmentSlot slot = renderData.armorSegment.equipmentSlot;
		final EquipmentClientInfo.LayerType layerType = slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
		final HumanoidModel defaultModel = GeckoLibClient.HUMANOID_ARMOR_MODEL.get().get(slot);

		return GeckoLibClientServices.ITEM_RENDERING.getArmorModelForItem(humanoidRenderState, stack, slot, layerType, defaultModel);
	}

	/// Get the [EquipmentClientInfo.LayerType] for the given [Equippable]. This defines the asset type to use in rendering a vanilla `Equippable` model.
	protected EquipmentClientInfo.LayerType getEquipmentLayerType(RenderPassInfo<R> renderPassInfo, RenderData renderData, GeoBone bone, ItemStack stack,
																  Model<?> equippedModel, ModelPart modelPart, ResourceKey<EquipmentAsset> assetId) {
		if (renderData.armorSegment.equipmentSlot == EquipmentSlot.LEGS)
			return EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS;

		if (renderData.armorSegment.equipmentSlot == EquipmentSlot.CHEST && !this.equipmentAssets.get(assetId).getLayers(EquipmentClientInfo.LayerType.WINGS).isEmpty())
			return EquipmentClientInfo.LayerType.WINGS;

		return EquipmentClientInfo.LayerType.HUMANOID;
	}

	/// Hide all [ModelPart]s of a given [HumanoidModel] that aren't the used part for this render section
	protected <S extends HumanoidRenderState, M extends HumanoidModel<S>> VanillaModelModifier<S, M> setUnusedModelPartVisibility(ModelPart usedPart, M model) {
		final ModelPart[] parts = new ModelPart[] {model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg};

		return VanillaModelModifier.of(_ -> {
			for (ModelPart part : parts) {
				if (part != usedPart)
					part.skipDraw = true;
			}
		}, _ -> {
			for (ModelPart part : parts) {
				if (part != usedPart)
					part.skipDraw = false;
			}
		});
	}

	/// Check for the presence of [Elytra][ElytraModel] wings and adjust the model as necessary
	protected Model checkForElytraModel(RenderPassInfo<R> renderPassInfo, EquipmentClientInfo.LayerType layerType, GeoBone bone, PoseStack poseStack) {
		final ElytraModel model = GeckoLibClient.GENERIC_ELYTRA_MODEL.get();
		final HumanoidRenderState humanoidRenderState = new HumanoidRenderState();
		final R renderState = renderPassInfo.renderState();
		final Vec3 elytraRotation = renderState.getOrDefaultGeckolibData(DataTickets.ELYTRA_ROTATION, Vec3.ZERO);
		humanoidRenderState.isCrouching = renderState.getOrDefaultGeckolibData(DataTickets.IS_CROUCHING, false);
		humanoidRenderState.elytraRotX = (float)elytraRotation.x;
		humanoidRenderState.elytraRotY = (float)elytraRotation.y;
		humanoidRenderState.elytraRotZ = (float)elytraRotation.z;

		poseStack.translate(0, -1.5f, 0.125f);

		return model;
	}
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="<Internal Methods>">
	/// Determine the scale factor for a worn item on the target [GeoBone], based on its size relative to a standard player
	protected Vec3 getScaleFactorForBone(GeoBone targetBone, ModelPart modelPart) {
		if (!(targetBone instanceof CuboidGeoBone cuboidBone))
			return new Vec3(1, 1, 1);

		final Cube armorCube = modelPart.cubes.getFirst();
		final double playerArmorSizeX = Math.abs(armorCube.maxX - armorCube.minX);
		final double playerArmorSizeY = Math.abs(armorCube.maxY - armorCube.minY);
		final double playerArmorSizeZ = Math.abs(armorCube.maxZ - armorCube.minZ);
		final double bodyPartSizeX = cuboidBone.cubes[0].size().x();
		final double bodyPartSizeY = cuboidBone.cubes[0].size().y();
		final double bodyPartSizeZ = cuboidBone.cubes[0].size().z();

		return new Vec3(bodyPartSizeX / playerArmorSizeX, bodyPartSizeY / playerArmorSizeY, bodyPartSizeZ / playerArmorSizeZ);
	}

    /// Convert an existing RenderState to a HumanoidRenderState, either by casting or creating a new one, for the purposes of RenderState filling
    protected <S extends HumanoidRenderState & GeoRenderState> S getOrCreateHumanoidRenderState(R renderState, boolean forceNew) {
        S newState = (S)(!forceNew && renderState instanceof HumanoidRenderState state ? state : new HumanoidRenderState());

        if (newState != renderState)
            RenderStateUtil.makeMinimalArmorRenderingClone(newState, renderState);

        return newState;
    }
	//</editor-fold>
}
