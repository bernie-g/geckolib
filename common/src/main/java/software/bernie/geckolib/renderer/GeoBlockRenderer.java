package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.function.Function;

/**
 * Base {@link GeoRenderer} class for rendering {@link BlockEntity Blocks} specifically
 * <p>
 * All blocks added to be rendered by GeckoLib should use an instance of this class.
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
	 * <p>
	 * You generally shouldn't need to override this
	 *
	 * @param animatable The Animatable instance being renderer
	 * @param relatedObject An object related to the render pass or null if not applicable.
	 *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	 */
	@ApiStatus.Internal
	@Override
	public long getInstanceId(T animatable, Void relatedObject) {
		return animatable.getBlockPos().hashCode();
	}

	@ApiStatus.Internal
	@Override
	public R captureDefaultRenderState(T animatable, Void relatedObject, R renderState, float partialTick) {
		GeoRenderer.super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);

		renderState.addGeckolibData(DataTickets.BLOCKSTATE, animatable.getBlockState());
		renderState.addGeckolibData(DataTickets.POSITION, Vec3.atCenterOf(animatable.getBlockPos()));
		renderState.addGeckolibData(DataTickets.BLOCK_FACING, getFacing(animatable));

		return renderState;
	}

    /**
     * Called at the start of the render compilation pass. PoseState manipulations have not yet taken place and typically should not be made here.
     * <p>
     * Use this method to handle any preparation or pre-work required for the render submission.
     * <p>
     * Manipulation of the model's bones is not permitted here
     */
	@Override
	public void preRender(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
						  int packedLight, int packedOverlay, int renderColor) {
        poseStack.translate(0.5, 0, 0.5);
	}

	/**
	 * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
	 * <p>
	 * Override and call <code>super</code> with modified scale values as needed to further modify the scale of the model
	 */
	@Override
	public void scaleModelForRender(R renderState, float widthScale, float heightScale, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
		GeoRenderer.super.scaleModelForRender(renderState, widthScale * this.scaleWidth, heightScale * this.scaleHeight, poseStack, model, cameraState);
	}

    /**
     * Transform the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
     * <p>
     * This is called after {@link #scaleModelForRender}, and so any transformations here will be scaled appropriately.
     * If you need to do pre-scale translations, use {@link #preRender}
     */
    @Override
    public void adjustRenderPose(R renderState, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
        rotateBlock(renderState.getGeckolibData(DataTickets.BLOCK_FACING), poseStack);
    }

    @ApiStatus.Internal
    @Override
    public void submit(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraRenderState) {
        submitRenderTasks(renderState, poseStack, renderTasks, cameraRenderState, null);
    }

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderBone(R renderState, PoseStack poseStack, GeoBone bone, VertexConsumer buffer, CameraRenderState cameraState,
                           int packedLight, int packedOverlay, int renderColor) {
		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());
			Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, renderState.getGeckolibData(DataTickets.OBJECT_RENDER_POSE));
			Matrix4f worldState = new Matrix4f(localMatrix);
			BlockPos pos = renderState.blockPos;

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, renderState.getGeckolibData(DataTickets.MODEL_RENDER_POSE)));
			bone.setLocalSpaceMatrix(localMatrix);
			bone.setWorldSpaceMatrix(worldState.translate(new Vector3f(pos.getX(), pos.getY(), pos.getZ())));
		}

		GeoRenderer.super.renderBone(renderState, poseStack, bone, buffer, cameraState, packedLight, packedOverlay, renderColor);
	}

	/**
	 * Rotate the {@link PoseStack} based on the determined {@link Direction} the block is facing
	 */
	protected void rotateBlock(Direction facing, PoseStack poseStack) {
		switch (facing) {
			case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
			case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
			case EAST -> poseStack.mulPose(Axis.YN.rotationDegrees(90));
			case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
			case DOWN -> poseStack.mulPose(Axis.XN.rotationDegrees(90));
			default -> {}
		}
	}

	/**
	 * Attempt to extract a direction from the block so that the model can be oriented correctly
	 */
	protected Direction getFacing(T blockEntity) {
		BlockState blockState = blockEntity.getBlockState();

		if (blockState.hasProperty(HorizontalDirectionalBlock.FACING))
			return blockState.getValue(HorizontalDirectionalBlock.FACING);

		if (blockState.hasProperty(DirectionalBlock.FACING))
			return blockState.getValue(DirectionalBlock.FACING);

		return Direction.NORTH;
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
    public void extractRenderState(T blockEntity, R renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay damageOverlayState) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, damageOverlayState);
        fillRenderState(blockEntity, null, renderState, partialTick);
    }

    /**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeckoLibServices.Client.EVENTS.fireCompileBlockRenderLayers(this);
	}

	/**
	 * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderStateEvent(T animatable, Void relatedObject, R renderState, float partialTick) {
		GeckoLibServices.Client.EVENTS.fireCompileBlockRenderState(this, renderState, animatable);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
		return GeckoLibServices.Client.EVENTS.fireBlockPreRender(this, renderState, poseStack, model, renderTasks, cameraState);
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
}
