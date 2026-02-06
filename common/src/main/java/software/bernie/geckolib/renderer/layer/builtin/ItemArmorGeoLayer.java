package software.bernie.geckolib.renderer.layer.builtin;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibClientServices;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.cache.model.cuboid.CuboidGeoBone;
import software.bernie.geckolib.cache.model.cuboid.GeoCube;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.service.GeckoLibClient;
import software.bernie.geckolib.util.RenderStateUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/// Builtin class for handling dynamic armor rendering on GeckoLib entities
///
/// Supports both [GeckoLib][GeoItem] and vanilla armor models
///
/// Unlike a traditional armor renderer, this renderer renders per-bone, giving much more flexible armor rendering
///
/// @param <T> Animatable class type. Inherited from the renderer this layer is attached to
/// @param <O> Associated object class type, or [Void] if none. Inherited from the renderer this layer is attached to
/// @param <R> RenderState class type. Inherited from the renderer this layer is attached to
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class ItemArmorGeoLayer<T extends LivingEntity & GeoAnimatable, O, R extends EntityRenderState & GeoRenderState> extends GeoRenderLayer<T, O, R> {
	protected final EquipmentLayerRenderer equipmentRenderer;
	protected final EquipmentAssetManager equipmentAssets;
	protected final Function<SkullBlock.Type, @Nullable SkullModelBase> skullModels;
    protected final PlayerSkinRenderCache skinCache;

	public ItemArmorGeoLayer(GeoRenderer<T, O, R> geoRenderer, EntityRendererProvider.Context context) {
		super(geoRenderer);

		this.equipmentRenderer = context.getEquipmentRenderer();
		this.equipmentAssets = context.getEquipmentAssets();
		this.skullModels = Util.memoize(type -> SkullBlockRenderer.createModel(context.getModelSet(), type));
        this.skinCache = context.getPlayerSkinRenderCache();
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
		public static RenderData head(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.HEAD);
		}

		public static RenderData body(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.CHEST);
		}

		public static RenderData leftArm(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.LEFT_ARM);
		}

		public static RenderData rightArm(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.RIGHT_ARM);
		}

		public static RenderData leftLeg(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.LEFT_LEG);
		}

		public static RenderData rightLeg(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.RIGHT_LEG);
		}

		public static RenderData leftFoot(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.LEFT_FOOT);
		}

		public static RenderData rightFoot(String boneName) {
			return new RenderData(boneName, GeoArmorRenderer.ArmorSegment.RIGHT_FOOT);
		}
	}

	/// Override to add any custom [DataTicket]s you need to capture for rendering.
	///
	/// The animatable is discarded from the rendering context after this, so any data needed
	/// for rendering should be captured in the renderState provided
	///
	/// @param animatable The animatable instance being rendered
	/// @param relatedObject An object related to the render pass or null if not applicable.
	///                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	/// @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
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
    }

	/// Register per-bone render operations, to be rendered after the main model is done.
	///
	/// Even though the task is called after the main model renders, the [PoseStack] provided will be posed as if the bone
	/// is currently rendering.
	///
	/// @param consumer The registrar to accept the per-bone render tasks
	@Override
    public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
		for (RenderData renderData : getRelevantBones(renderPassInfo)) {
            renderPassInfo.model().getBone(renderData.boneName).filter(CuboidGeoBone.class::isInstance)
                    .ifPresentOrElse(bone -> createPerBoneRender(renderPassInfo, bone, renderData, consumer),
                                     () -> GeckoLibConstants.LOGGER.error("Unable to find bone for ItemArmorGeoLayer: {}, skipping", renderData.boneName));
		}
	}

	private void createPerBoneRender(RenderPassInfo<R> renderPassInfo, GeoBone bone, RenderData renderData, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        GeoArmorRenderer.ArmorSegment armorSegment = renderData.armorSegment;
		ItemStack stack = getEquipmentStack(renderPassInfo, bone, armorSegment.equipmentSlot);

		if (!stack.isEmpty()) {
			consumer.accept(bone, (renderPassInfo2, bone2, renderTasks) ->
					buildRenderTask(renderPassInfo2, armorSegment.equipmentSlot, armorSegment.modelPartGetter, stack, (CuboidGeoBone)bone2, renderTasks));
		}
	}

	/// Perform the actual rendering operation for the given bone and equipment
	protected void buildRenderTask(RenderPassInfo<R> renderPassInfo, EquipmentSlot slot, Function<HumanoidModel<?>, ModelPart> modelPartFactory, ItemStack equipmentStack, CuboidGeoBone bone,
                                   SubmitNodeCollector renderTasks) {
		// TODO Rewrite this Geo layer to make work :(
		if (true || equipmentStack.isEmpty())
			return;

        final PoseStack poseStack = renderPassInfo.poseStack();
        final R renderState = renderPassInfo.renderState();

		if (equipmentStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractSkullBlock skullBlock) {
            renderSkullAsArmor(renderPassInfo, bone, equipmentStack, skullBlock, renderTasks);
		}
        else if (RenderUtil.getGeckoLibArmorRenderer(equipmentStack, slot) instanceof GeoArmorRenderer geoArmorRenderer) {
            EnumMap<EquipmentSlot, R> perSlotData = (EnumMap)renderState.getGeckolibData(DataTickets.PER_SLOT_RENDER_DATA);

            if (perSlotData != null) {
                R slotRenderState = perSlotData.get(slot);

                if (slotRenderState != null) {
                    poseStack.pushPose();
                    poseStack.scale(-1, -1, 1);

                    GeoRenderState humanoidRenderState = getOrCreateHumanoidRenderState(slotRenderState, false);
                    RenderPassInfo.BoneUpdater<R> boneUpdater = positionModelPartFromBone(poseStack, bone, modelPartFactory.apply(slotRenderState.getGeckolibData(DataTickets.HUMANOID_MODEL)));

                    renderPassInfo.addBoneUpdater(boneUpdater);

                    geoArmorRenderer.performRenderPass(humanoidRenderState, poseStack, renderTasks, renderPassInfo.cameraState());
                    poseStack.popPose();
                }
            }
        }
		else {
			Model<?> vanillaModel = getArmorModelForRender(bone, slot, equipmentStack, renderState);
			ModelPart modelPart = vanillaModel instanceof HumanoidModel<?> humanoidModel ? modelPartFactory.apply(humanoidModel) : vanillaModel.root();

			if (!modelPart.cubes.isEmpty()) {
				poseStack.pushPose();
				poseStack.scale(-1, -1, 1);

                Equippable equippable = equipmentStack.get(DataComponents.EQUIPPABLE);

                if (equippable != null) {
                    equippable.assetId().ifPresent(assetId -> {
                        positionModelPartFromBone(poseStack, bone, modelPart);
                        renderVanillaArmorPiece(renderPassInfo, poseStack, bone, slot, equipmentStack, equippable, assetId, vanillaModel, modelPart, renderTasks);
                    });
                }

				poseStack.popPose();
			}
		}
	}

	/// Helper method to retrieve a stored held or worn ItemStack by the slot it's in, as computed in [GeoRenderLayer#addRenderData(GeoAnimatable, Object, GeoRenderState, float)]
	protected ItemStack getEquipmentStack(RenderPassInfo<R> renderPassInfo, GeoBone bone, EquipmentSlot slot) {
		return (ItemStack)renderPassInfo.getGeckolibData(DataTickets.EQUIPMENT_BY_SLOT).getOrDefault(slot, ItemStack.EMPTY);
	}

	/// Get the LayerType for the given armor piece. This defines the asset type to use in rendering a vanilla armor piece.
	protected EquipmentClientInfo.LayerType getEquipmentLayerType(RenderPassInfo<R> renderPassInfo, GeoBone bone, EquipmentSlot slot, ItemStack armorStack, ResourceKey<EquipmentAsset> assetId) {
		if (slot == EquipmentSlot.LEGS)
			return EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS;

		if (slot == EquipmentSlot.CHEST && !this.equipmentAssets.get(assetId).getLayers(EquipmentClientInfo.LayerType.WINGS).isEmpty())
			return EquipmentClientInfo.LayerType.WINGS;

		return EquipmentClientInfo.LayerType.HUMANOID;
	}

	/// Renders an individual armor piece base on the given [GeoBone] and [ItemStack]
	protected void renderVanillaArmorPiece(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, GeoBone bone, EquipmentSlot slot, ItemStack armorStack,
										   Equippable equippable, ResourceKey<EquipmentAsset> assetId, Model<?> model, ModelPart modelPart, SubmitNodeCollector renderTasks) {
		EquipmentClientInfo.LayerType layerType = getEquipmentLayerType(renderPassInfo, bone, slot, armorStack, assetId);
		Model modelToRender = model;

		if (layerType == EquipmentClientInfo.LayerType.WINGS) {
			if (model instanceof HumanoidModel humanoidModel && modelPart != humanoidModel.body)
				return;

			modelToRender = checkForElytraModel(renderPassInfo, layerType, bone, poseStack);
		}

		this.equipmentRenderer.renderLayers(layerType, assetId, modelToRender, renderPassInfo.renderState(), armorStack, poseStack, renderTasks, renderPassInfo.packedLight(), renderPassInfo.renderState().outlineColor);
	}

	/// Check for the presence of [Elytra][ElytraModel] wings, and adjust the model as necessary
	protected Model checkForElytraModel(RenderPassInfo<R> renderPassInfo, EquipmentClientInfo.LayerType layerType, GeoBone bone, PoseStack poseStack) {
		ElytraModel model = GeckoLibClient.GENERIC_ELYTRA_MODEL.get();
		HumanoidRenderState humanoidRenderState = new HumanoidRenderState();
        R renderState = renderPassInfo.renderState();
		Vec3 elytraRotation = renderState.getOrDefaultGeckolibData(DataTickets.ELYTRA_ROTATION, Vec3.ZERO);
		humanoidRenderState.isCrouching = renderState.getOrDefaultGeckolibData(DataTickets.IS_CROUCHING, false);
		humanoidRenderState.elytraRotX = (float)elytraRotation.x;
		humanoidRenderState.elytraRotY = (float)elytraRotation.y;
		humanoidRenderState.elytraRotZ = (float)elytraRotation.z;

		model.setupAnim(humanoidRenderState);
		poseStack.translate(0, -1.5f, 0.125f);

		return model;
	}

	/// Returns a cached instance of a base HumanoidModel that is used for rendering/modelling the provided [ItemStack]
	@ApiStatus.Internal
	protected <S extends HumanoidRenderState & GeoRenderState> Model<?> getArmorModelForRender(GeoBone bone, EquipmentSlot slot, ItemStack stack, R renderState) {
		final S humanoidRenderState = renderState instanceof HumanoidRenderState humanoidRenderState1 ? (S)humanoidRenderState1 : (S)new HumanoidRenderState();
		final EquipmentClientInfo.LayerType layerType = slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
		final HumanoidModel defaultModel = GeckoLibClient.HUMANOID_ARMOR_MODEL.get().get(slot);

		return GeckoLibClientServices.ITEM_RENDERING.getArmorModelForItem(humanoidRenderState, stack, slot, layerType, defaultModel);
	}

	/// Render a given [AbstractSkullBlock] as a worn armor piece in relation to a given [GeoBone]
	protected void renderSkullAsArmor(RenderPassInfo<R> renderPassInfo, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock, SubmitNodeCollector renderTasks) {
		SkullBlock.Type type = skullBlock.getType();
		SkullModelBase model = this.skullModels.apply(type);

		if (model == null)
			return;

		ResolvableProfile profile = stack.get(DataComponents.PROFILE);
		RenderType renderType = profile == null ? PlayerSkinRenderCache.DEFAULT_PLAYER_SKIN_RENDER_TYPE : this.skinCache.getOrDefault(profile).renderType();
		PoseStack poseStack = renderPassInfo.poseStack();

		poseStack.pushPose();
		RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
		poseStack.scale(1.1875f, 1.1875f, 1.1875f);
		poseStack.translate(-0.5f, 0, -0.5f);

		SkullBlockRenderer.submitSkull(null, 0, 0, poseStack, renderTasks, renderPassInfo.packedLight(), model, renderType, renderPassInfo.renderState().outlineColor, null);
		poseStack.popPose();
	}

	/// Prepares the given [ModelPart] for render by setting its translation, position, and rotation values based on the provided [GeoBone]
	///
	/// This implementation uses the **<u>FIRST</u>** cube in the source part
	/// to determine the scale and position of the GeoArmor to be rendered
	///
	/// @param poseStack The PoseStack being used for rendering
	/// @param bone The GeoBone to base the translations on
	/// @param sourcePart The ModelPart to translate
	protected RenderPassInfo.BoneUpdater<R> positionModelPartFromBone(PoseStack poseStack, CuboidGeoBone bone, ModelPart sourcePart) {
		final GeoCube firstCube = bone.cubes[0];
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
        final RenderPassInfo.BoneUpdater<R> modelPositioner = (renderPassInfo, snapshots) -> {
            final BoneSnapshot snapshot = snapshots.get(bone);

            sourcePart.setPos(-(bone.pivotX() - ((bone.pivotX() * scaleX) - bone.pivotX()) / scaleX),
                              -(bone.pivotY() - ((bone.pivotY() * scaleY) - bone.pivotY()) / scaleY),
                              (bone.pivotZ() - ((bone.pivotZ() * scaleZ) - bone.pivotZ()) / scaleZ));

            sourcePart.xRot = -snapshot.getRotX();
            sourcePart.yRot = -snapshot.getRotY();
            sourcePart.zRot = snapshot.getRotZ();
        };

		poseStack.scale(scaleX, scaleY, scaleZ);

        return modelPositioner;
	}

    /// Convert an existing RenderState to a HumanoidRenderState, either by casting or creating a new one, for the purposes of RenderState filling
    protected <S extends HumanoidRenderState & GeoRenderState> S getOrCreateHumanoidRenderState(R renderState, boolean forceNew) {
        S newState = (S)(!forceNew && renderState instanceof HumanoidRenderState state ? state : new HumanoidRenderState());

        if (newState != renderState)
            RenderStateUtil.makeMinimalArmorRenderingClone(newState, renderState);

        return newState;
    }
}
