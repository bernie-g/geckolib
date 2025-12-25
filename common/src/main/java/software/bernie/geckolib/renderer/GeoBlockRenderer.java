package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibClientServices;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;

import java.util.List;
import java.util.function.Function;

/**
 * Base {@link GeoRenderer} class for rendering {@link BlockEntity Blocks} specifically
 * <p>
 * All blocks added to be rendered by GeckoLib should use an instance of this class.
 *
 * @param <T> BlockEntity animatable class type
 * @param <R> RenderState class type
 */
public class GeoBlockRenderer<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> implements GeoRenderer<T, Void, R>, BlockEntityRenderer<T, R> {
	protected final GeoRenderLayersContainer<T, Void, R> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

    /**
     * Creates a new defaulted renderer instance, using the blockentity's registered id as the file name for its assets
     */
    public GeoBlockRenderer(BlockEntityType<? extends T> blockEntityType) {
        this(new DefaultedBlockGeoModel<>(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType)));
    }

	public GeoBlockRenderer(GeoModel<T> model) {
		this.model = model;
	}

    /**
     * Attempt to extract a direction from the block so that the model can be oriented correctly
     */
    protected Direction getBlockStateDirection(T blockEntity) {
        BlockState blockState = blockEntity.getBlockState();

        for (EnumProperty<Direction> property : new EnumProperty[] {BlockStateProperties.FACING, BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.VERTICAL_DIRECTION , BlockStateProperties.FACING_HOPPER }) {
            if (blockState.hasProperty(property))
                return blockState.getValue(property);
        }

        return Direction.NORTH;
    }

    /**
     * Rotate the {@link PoseStack} based on the determined {@link Direction} the block is facing
     */
    protected void tryRotateByBlockstate(RenderPassInfo<R> renderPassInfo, PoseStack poseStack) {
        final Direction facing = renderPassInfo.getGeckolibData(DataTickets.BLOCK_FACING);

        switch (facing) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case EAST -> poseStack.mulPose(Axis.YN.rotationDegrees(90));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case DOWN -> poseStack.mulPose(Axis.XN.rotationDegrees(90));
            default -> {}
        }
    }

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
    public List<GeoRenderLayer<T, Void, R>> getRenderLayers() {
        return this.renderLayers.getRenderLayers();
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoBlockRenderer<T, R> withRenderLayer(Function<? super GeoBlockRenderer<T, R>, GeoRenderLayer<T, Void, R>> renderLayer) {
        return withRenderLayer(renderLayer.apply(this));
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoBlockRenderer<T, R> withRenderLayer(GeoRenderLayer<T, Void, R> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

    /**
     * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
     */
    public GeoBlockRenderer<T, R> withScale(float scale) {
        return withScale(scale, scale);
    }

    /**
     * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
     */
    public GeoBlockRenderer<T, R> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    /**
     * Gets the id that represents the current animatable's instance for animation purposes.
     *
     * @param animatable The Animatable instance being renderer
     */
    @ApiStatus.OverrideOnly
    @Override
    public long getInstanceId(T animatable, Void ignored) {
        return animatable.getBlockPos().hashCode();
    }

    /**
     * Internal method for capturing the common RenderState data for all animatable objects
     */
    @ApiStatus.Internal
    @Override
    public void captureDefaultRenderState(T animatable, Void relatedObject, R renderState, float partialTick) {
        GeoRenderer.super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);

        renderState.addGeckolibData(DataTickets.BLOCKSTATE, animatable.getBlockState());
        renderState.addGeckolibData(DataTickets.POSITION, Vec3.atCenterOf(animatable.getBlockPos()));
        renderState.addGeckolibData(DataTickets.BLOCK_FACING, getBlockStateDirection(animatable));
    }

    /**
     * Called at the start of the render compilation pass. PoseState manipulations have not yet taken place and typically should not be made here.
     * <p>
     * Manipulation of the model's bones is not permitted here
     * <p>
     * Use this method to handle any preparation or pre-work required for the render submission.
     *
     * @see #scaleModelForRender
     * @see #adjustRenderPose
     * @see #adjustModelBonesForRender
     */
    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        renderPassInfo.poseStack().translate(0.5d, 0, 0.5d);
    }

    /**
     * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
     * <p>
     * Override and call {@code super} with modified scale values as needed to further modify the scale of the model
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
        tryRotateByBlockstate(renderPassInfo, renderPassInfo.poseStack());
    }

    /**
     * Initial access point for vanilla's {@link BlockEntityRenderer} interface<br>
     * Immediately defers to {@link GeoRenderer#performRenderPass(GeoRenderState, PoseStack, SubmitNodeCollector, CameraRenderState)}
     */
    @ApiStatus.Internal
    @Override
    public void submit(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraRenderState) {
        GeoRenderer.super.performRenderPass(renderState, poseStack, renderTasks, cameraRenderState);
    }

    /**
     * Default return for creating the {@link BlockEntityRenderState}.
     * <p>
     * You generally shouldn't need to override or use this
     */
    @ApiStatus.Internal
    @Override
    public R createRenderState() {
        return (R)new BlockEntityRenderState();
    }

    /**
     * Create the contextually relevant {@link BlockEntityRenderState} for the current render pass
     */
    @Override
    public void extractRenderState(T blockEntity, R renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay damageOverlayState) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, damageOverlayState);
        fillRenderState(blockEntity, null, renderState, partialTick);
    }

    /**
     * Create and fire the relevant {@code CompileLayers} event hook for this renderer
     */
    @Override
    public void fireCompileRenderLayersEvent() {
        GeckoLibClientServices.EVENTS.fireCompileBlockRenderLayers(this);
    }

    /**
     * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
     */
    @Override
    public void fireCompileRenderStateEvent(T animatable, Void relatedObject, R renderState, float partialTick) {
        GeckoLibClientServices.EVENTS.fireCompileBlockRenderState(this, renderState, animatable);
    }

    /**
     * Create and fire the relevant {@code Pre-Render} event hook for this renderer
     *
     * @return Whether the renderer should proceed based on the cancellation state of the event
     */
    @Override
    public boolean firePreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return GeckoLibClientServices.EVENTS.fireBlockPreRender(renderPassInfo, renderTasks);
    }

    /**
     * @deprecated Unusable because of vanilla implementation. Use {@link #createRenderState()}
     */
    @Deprecated
    @ApiStatus.Internal
    @Override
    public final R createRenderState(T animatable, Void relatedObject) {
        return (R)new BlockEntityRenderState();
    }
    //</editor-fold>
}
