package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.internal.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;

import java.util.EnumMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Base {@link GeoRenderer} for rendering in-world armor specifically
 * <p>
 * All custom armor added to be rendered in-world by GeckoLib should use an instance of this class
 *
 * @see GeoItem
 */
public class GeoArmorRenderer<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> implements GeoRenderer<T, GeoArmorRenderer.RenderData, R> {
    protected static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	protected final GeoRenderLayersContainer<T, GeoArmorRenderer.RenderData, R> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

    /**
     * Creates a new defaulted renderer instance, using the item's registered id as the file name for its assets
     */
	public <I extends T> GeoArmorRenderer(I armorItem) {
		this(new DefaultedGeoModel<>(BuiltInRegistries.ITEM.getKey(armorItem)) {
            @Override
            protected String subtype() {
                return "armor";
            }
        });
	}

	public GeoArmorRenderer(GeoModel<T> model) {
		this.model = model;
	}

    /**
     * Return the list of {@link ArmorSegment}s that should be rendered for the given {@link EquipmentSlot} for this render pass.
     * <p>
     * Override this if your armor piece renders different pieces than the default setup
     */
    public List<ArmorSegment> getSegmentsForSlot(R renderState, EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> List.of(ArmorSegment.HEAD);
            case CHEST -> List.of(ArmorSegment.CHEST, ArmorSegment.LEFT_ARM, ArmorSegment.RIGHT_ARM);
            case LEGS -> List.of(ArmorSegment.LEFT_LEG, ArmorSegment.RIGHT_LEG);
            case FEET -> List.of(ArmorSegment.LEFT_FOOT, ArmorSegment.RIGHT_FOOT);
            default -> List.of();
        };
    }

    /**
     * Return the equivalent bone name for the given {@link ArmorSegment} for this render pass.
     * <p>
     * Override this if your armor has different bone names for some reason.
     */
    public String getBoneNameForSegment(R renderState, ArmorSegment segment) {
        return switch (segment) {
            case HEAD -> "armorHead";
            case CHEST -> "armorBody";
            case LEFT_ARM -> "armorLeftArm";
            case RIGHT_ARM -> "armorRightArm";
            case LEFT_LEG -> "armorLeftLeg";
            case RIGHT_LEG -> "armorRightLeg";
            case LEFT_FOOT -> "armorLeftBoot";
            case RIGHT_FOOT -> "armorRightBoot";
        };
    }

	/**
	 * Data container for additional render context information for creating the RenderState for this renderer
	 *
	 * @param itemStack The ItemStack about to be rendered
	 * @param slot The EquipmentSlot the ItemStack is in
	 * @param entity The entity wearing the item
     * @param baseModel The base vanilla model for the armor piece being rendered
	 */
	public record RenderData(ItemStack itemStack, EquipmentSlot slot, LivingEntity entity, HumanoidModel<?> baseModel) {}

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">
    /**
     * Gets the model instance for this renderer
     */
    @Override
    public GeoModel<T> getGeoModel() {
        return this.model;
    }

    /**
     * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
     */
    @Override
    public List<GeoRenderLayer<T, RenderData, R>> getRenderLayers() {
        return this.renderLayers.getRenderLayers();
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoArmorRenderer<T, R> withRenderLayer(Function<? super GeoArmorRenderer<T, R>, GeoRenderLayer<T, GeoArmorRenderer.RenderData, R>> renderLayer) {
        return withRenderLayer(renderLayer.apply(this));
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoArmorRenderer<T, R> withRenderLayer(GeoRenderLayer<T, GeoArmorRenderer.RenderData, R> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

    /**
     * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
     */
    public GeoArmorRenderer<T, R> withScale(float scale) {
        return withScale(scale, scale);
    }

    /**
     * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
     */
    public GeoArmorRenderer<T, R> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    /**
     * Gets a tint-applying color to render the given animatable with
     * <p>
     * Returns opaque white by default, multiplied by any inherent vanilla item dye color
     */
    @Override
    public int getRenderColor(T animatable, RenderData stackAndSlot, float partialTick) {
        return GeckoLibServices.Client.ITEM_RENDERING.getDyedItemColor(stackAndSlot.itemStack(), 0xFFFFFFFF);
    }

    /**
     * Gets the {@link RenderType} to render the current render pass with
     * <p>
     * Uses the {@link RenderTypes#entityCutoutNoCull} {@code RenderType} by default
     * <p>
     * Override this to change the way a model will render (such as translucent models, etc)
     *
     * @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions from taking place
     */
    @Nullable
    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.armorCutoutNoCull(texture);
    }

    /**
     * Gets the id that represents the current animatable's instance for animation purposes.
     *
     * @param animatable The Animatable instance being renderer
     * @param stackAndSlot An object related to the render pass or null if not applicable.
     *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
     */
    @ApiStatus.OverrideOnly
    @Override
    public long getInstanceId(T animatable, RenderData stackAndSlot) {
        long stackId = GeoItem.getId(stackAndSlot.itemStack());

        if (stackId == Long.MAX_VALUE) {
            int id = stackAndSlot.entity().getId() * 13;

            return (long)id * id * id * -(stackAndSlot.slot().ordinal() + 1);
        }

        return -stackId;
    }

    /**
     * Internal method for capturing the common RenderState data for all animatable objects
     */
    @ApiStatus.Internal
    @Override
    public void captureDefaultRenderState(T animatable, RenderData renderData, R renderState, float partialTick) {
        GeoRenderer.super.captureDefaultRenderState(animatable, renderData, renderState, partialTick);

        renderState.addGeckolibData(DataTickets.TICK, (double)renderState.ageInTicks);
        renderState.addGeckolibData(DataTickets.POSITION, renderData.entity().position());
        renderState.addGeckolibData(DataTickets.IS_GECKOLIB_WEARER, renderData.entity() instanceof GeoAnimatable);
        renderState.addGeckolibData(DataTickets.EQUIPMENT_SLOT, renderData.slot());
        renderState.addGeckolibData(DataTickets.HAS_GLINT, renderData.itemStack().hasFoil());
        renderState.addGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, renderState instanceof LivingEntityRenderState livingRenderState && livingRenderState.isInvisibleToPlayer);
        renderState.addGeckolibData(DataTickets.HUMANOID_MODEL, renderData.baseModel);
    }

    /**
     * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
     * <p>
     * Override and call <code>super</code> with modified scale values as needed to further modify the scale of the model
     */
    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        GeoRenderer.super.scaleModelForRender(renderPassInfo, this.scaleWidth * widthScale, this.scaleHeight * heightScale);
    }

    /**
     * Transform the {@link PoseStack} in preparation for rendering the model.
     * <p>
     * This is called after {@link #scaleModelForRender}, and so any transformations here will be scaled appropriately.
     * If you need to do pre-scale translations, use {@link #preRenderPass}
     * <p>
     * PoseStack translations made here are kept until the end of the render process
     */
    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        renderPassInfo.poseStack().translate(0, 24 / 16f, 0);
        renderPassInfo.poseStack().scale(-1, -1, 1);
    }

    /**
     * Build and submit the actual render task to the {@link OrderedSubmitNodeCollector} here.
     * <p>
     * Once the render task has been submitted here, no further manipulations of the render pass should be made.
     * <p>
     * If the provided {@link RenderType} is null, no submission will be made
     */
    @Override
    public void submitRenderTasks(RenderPassInfo<R> renderPassInfo, OrderedSubmitNodeCollector renderTasks, @Nullable RenderType renderType) {
        if (renderType == null)
            return;

        final int packedLight = renderPassInfo.packedLight();
        final int packedOverlay = renderPassInfo.packedOverlay();
        final int renderColor = renderPassInfo.renderColor();

        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, vertexConsumer) -> {
            final PoseStack poseStack = renderPassInfo.poseStack();
            final R renderState = renderPassInfo.renderState();
            final EquipmentSlot slot = renderState.getGeckolibData(DataTickets.EQUIPMENT_SLOT);
            final HumanoidModel baseModel = renderState.getGeckolibData(DataTickets.HUMANOID_MODEL);
            final BakedGeoModel bakedModel = renderPassInfo.model();

            renderPassInfo.addBoneUpdater((renderPassInfo1, snapshots) -> {
                final List<ArmorSegment> segments = getSegmentsForSlot(renderState, slot);

                if (!segments.isEmpty()) {
                    baseModel.setupAnim(renderState);

                    for (ArmorSegment segment : getSegmentsForSlot(renderState, slot)) {
                        snapshots.get(getBoneNameForSegment(renderState, segment)).ifPresent(snapshot -> {
                            final ModelPart modelPart = segment.modelPartGetter.apply(baseModel);
                            final Vector3f bonePos = segment.modelPartMatcher.apply(new Vector3f(modelPart.x, modelPart.y, modelPart.z));

                            snapshot.setRotX(-modelPart.xRot)
                                    .setRotY(-modelPart.yRot)
                                    .setRotZ(modelPart.zRot)
                                    .setTranslateX(bonePos.x)
                                    .setTranslateY(bonePos.y)
                                    .setTranslateZ(bonePos.z);
                        });
                    }
                }
            });

            poseStack.pushPose();
            poseStack.last().set(pose);
            renderPassInfo.renderPosed(() -> {
                for (ArmorSegment segment : getSegmentsForSlot(renderState, slot)) {
                    bakedModel.getBone(getBoneNameForSegment(renderState, segment)).ifPresent(bone -> {
                        bone.render(renderPassInfo, poseStack, vertexConsumer, packedLight, packedOverlay, renderColor);
                    });
                }

                renderPassInfo.model().render(renderPassInfo, vertexConsumer, packedLight, packedOverlay, renderColor);
            });
            poseStack.popPose();
        });
    }

    /**
     * Create and fire the relevant {@code CompileLayers} event hook for this renderer
     */
    @Override
    public void fireCompileRenderLayersEvent() {
        GeckoLibServices.Client.EVENTS.fireCompileArmorRenderLayers(this);
    }

    /**
     * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
     */
    @Override
    public void fireCompileRenderStateEvent(T animatable, RenderData relatedObject, R renderState, float partialTick) {
        GeckoLibServices.Client.EVENTS.fireCompileArmorRenderState(this, renderState, animatable, relatedObject);
    }

    /**
     * Create and fire the relevant {@code Pre-Render} event hook for this renderer
     *
     * @return Whether the renderer should proceed based on the cancellation state of the event
     */
    @Override
    public boolean firePreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return GeckoLibServices.Client.EVENTS.fireArmorPreRender(renderPassInfo, renderTasks);
    }

    /**
     * Enum representing the different parts of a humanoid armor GeckoLib handles for rendering.
     */
    public enum ArmorSegment {
        HEAD(EquipmentSlot.HEAD, model -> model.head, pos -> pos.mul(1, -1, 1)),
        CHEST(EquipmentSlot.CHEST, model -> model.body, pos -> pos.mul(1, -1, 1)),
        LEFT_ARM(EquipmentSlot.CHEST, model -> model.leftArm, pos -> pos.set(pos.x - 5, 2 - pos.y, pos.z)),
        RIGHT_ARM(EquipmentSlot.CHEST, model -> model.rightArm, pos -> pos.set(pos.x + 5, 2 - pos.y, pos.z)),
        LEFT_LEG(EquipmentSlot.LEGS, model -> model.leftLeg, pos -> pos.set(pos.x - 2, 12 - pos.y, pos.z)),
        RIGHT_LEG(EquipmentSlot.LEGS, model -> model.rightLeg, pos -> pos.set(pos.x + 2, 12 - pos.y, pos.z)),
        LEFT_FOOT(EquipmentSlot.FEET, model -> model.leftLeg, pos -> pos.set(pos.x - 2, 12 - pos.y, pos.z)),
        RIGHT_FOOT(EquipmentSlot.FEET, model -> model.rightLeg, pos -> pos.set(pos.x + 2, 12 - pos.y, pos.z));

        public final EquipmentSlot equipmentSlot;
        public final Function<HumanoidModel<?>, ModelPart> modelPartGetter;
        public final UnaryOperator<Vector3f> modelPartMatcher;

        ArmorSegment(EquipmentSlot slot, Function<HumanoidModel<?>, ModelPart> modelPartGetter, UnaryOperator<Vector3f> modelPartMatcher) {
            this.equipmentSlot = slot;
            this.modelPartGetter = modelPartGetter;
            this.modelPartMatcher = modelPartMatcher;
        }
    }

    /**
     * Helper class to consolidate the retrieval and validation of an individual slot for rendering
     */
    @ApiStatus.Internal
    private record StackForRender(ItemStack stack, EquipmentSlot slot, GeoArmorRenderer renderer, HumanoidModel<?> baseModel) {
        @Nullable
        private static <S extends HumanoidRenderState, A extends HumanoidModel<S>> StackForRender find(
                ItemStack stack, EquipmentSlot slot, S entityRenderState, BiFunction<S, EquipmentSlot, A> modelFunction) {
            final Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            GeoRenderProvider geckolibRenderers;

            if (equippable == null || !HumanoidArmorLayer.shouldRender(equippable, slot) || (geckolibRenderers = GeoRenderProvider.of(stack)) == GeoRenderProvider.DEFAULT)
                return null;

            final A baseModel = modelFunction.apply(entityRenderState, slot);
            final GeoArmorRenderer armorRenderer = geckolibRenderers.getGeoArmorRenderer(stack, slot);

            if (armorRenderer == null)
                return null;

            return new StackForRender(stack, slot, armorRenderer, baseModel);
        }
    }

    /**
     * Attempt to render a GeckoLib {@link GeoArmorRenderer armor piece} for the given slot
     * <p>
     * This is typically only called by an internal mixin
     *
     * @return true if the armor piece was a GeckoLib armor piece and was rendered
     */
    @ApiStatus.Internal
    public static <R extends HumanoidRenderState & GeoRenderState, A extends HumanoidModel<R>> boolean tryRenderGeoArmorPiece(
            BiFunction<R, EquipmentSlot, A> modelFunction, PoseStack poseStack, SubmitNodeCollector renderTasks, ItemStack stack, EquipmentSlot slot, int packedLight, R entityRenderState) {
        final StackForRender stackForRender = StackForRender.find(stack, slot, entityRenderState, modelFunction);
        EnumMap<EquipmentSlot, R> perSlotData;

        if (stackForRender == null || !(entityRenderState instanceof GeoRenderState geoRenderState))
            return false;

        if ((perSlotData = geoRenderState.getOrDefaultGeckolibData(DataTickets.PER_SLOT_RENDER_DATA, null)) == null || !perSlotData.containsKey(slot))
            return false;

        R perSlotRenderState = perSlotData.get(slot);
        stackForRender.renderer.performRenderPass(perSlotRenderState, poseStack, renderTasks, Minecraft.getInstance().levelRenderer.levelRenderState.cameraRenderState, null);

        return true;
    }

	/**
	 * Capture and assign RenderState data for each GeckoLib-relevant equipment slot for rendering
     * <p>
     * Called internally by a mixin
	 */
	@ApiStatus.Internal
	public static <R extends HumanoidRenderState & GeoRenderState, A extends HumanoidModel<R>> void captureRenderStates(
            R baseRenderState, LivingEntity entity, float partialTick, BiFunction<R, EquipmentSlot, A> modelFunction, Function<EquipmentSlot, R> renderStateSupplier) {
		final List<StackForRender> relevantSlots = getRelevantSlotsForRendering(entity, baseRenderState, modelFunction);

		if (relevantSlots == null)
			return;

		final EnumMap<EquipmentSlot, R> slotRenderData = new EnumMap<>(EquipmentSlot.class);

        for (StackForRender entry : relevantSlots) {
            RenderData renderData = new RenderData(entry.stack, entry.slot, entity, entry.baseModel);
            R slotRenderState = renderStateSupplier.apply(entry.slot);

            entry.renderer.fillRenderState((GeoAnimatable)entry.stack.getItem(), renderData, slotRenderState, partialTick);
            slotRenderData.put(entry.slot, slotRenderState);
        }

		baseRenderState.addGeckolibData(DataTickets.PER_SLOT_RENDER_DATA, slotRenderData);
	}

    /**
     * Compile an array of GeckoLib-relevant equipment pieces for the purposes of rendering
     */
	@Nullable
	@ApiStatus.Internal
	private static <R extends HumanoidRenderState & GeoRenderState, A extends HumanoidModel<R>> List<StackForRender> getRelevantSlotsForRendering(
            LivingEntity entity, R entityRenderState, BiFunction<R, EquipmentSlot, A> modelFunction) {
		List<StackForRender> relevantSlots = null;

        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            final EquipmentSlot slot = ARMOR_SLOTS[i];
            final StackForRender stackForRender = StackForRender.find(entity.getItemBySlot(slot), slot, entityRenderState, modelFunction);

            if (stackForRender == null)
                continue;

            if (relevantSlots == null)
                relevantSlots = new ObjectArrayList<>(ARMOR_SLOTS.length - i);

            relevantSlots.add(stackForRender);
        }

		return relevantSlots;
	}

    /**
     * Disabled because we use the {@link HumanoidRenderState} that vanilla compiles for the entity
     */
    @ApiStatus.Internal
    @Deprecated
    @Override
    public R createRenderState(T animatable, RenderData relatedObject) {
        return (R)new HumanoidRenderState();
    }

    //</editor-fold>
}