package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderModelPositioner;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.function.Function;

/**
 * Base {@link GeoRenderer} class for rendering anything that isn't already handled by the other builtin GeoRenderer subclasses
 * <p>
 * Before using this class you should ensure your use-case isn't already covered by one of the other existing renderers
 * <p>
 * It is <b>strongly</b> recommended you override {@link GeoRenderer#getInstanceId} if using this renderer
 */
public class GeoObjectRenderer<T extends GeoAnimatable, E, R extends GeoRenderState> implements GeoRenderer<T, E, R> {
	protected final GeoRenderLayersContainer<T, E, R> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	public GeoObjectRenderer(GeoModel<T> model) {
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
	public List<GeoRenderLayer<T, E, R>> getRenderLayers() {
		return this.renderLayers.getRenderLayers();
	}

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoObjectRenderer<T, E, R> withRenderLayer(Function<? super GeoObjectRenderer<T, E, R>, GeoRenderLayer<T, E, R>> renderLayer) {
        return withRenderLayer(renderLayer.apply(this));
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoObjectRenderer<T, E, R> withRenderLayer(GeoRenderLayer<T, E, R> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoObjectRenderer<T, E, R> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoObjectRenderer<T, E, R> withScale(float scaleWidth, float scaleHeight) {
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;

		return this;
	}

    /**
     * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
     * <p>
     * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child entities)
     */
    @Override
    public void scaleModelForRender(R renderState, float widthScale, float heightScale, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
        GeoRenderer.super.scaleModelForRender(renderState, widthScale * this.scaleWidth, heightScale * this.scaleHeight, poseStack, model, cameraState);
    }

    /**
     * Transform the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
     */
    @Override
    public void adjustRenderPose(R renderState, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
        poseStack.translate(0.5f, 0.51f, 0.5f);
    }

    /**
	 * The entry render point for this renderer
	 * <p>
	 * Call this whenever you want to render your object
	 *
	 * @param poseStack The PoseStack to render under
	 * @param animatable The {@link T} instance to render
     * @param relatedObject The {@link E} instance containing associated data for preparing the render pass
	 * @param renderTasks The render task collector for the render pass
     * @param cameraState The current camera rendering state, usually provided by the gui or {@link LevelRenderState}
	 * @param packedLight The light level at the given render position for rendering
     * @param modelPositioner The model positioner for rendering
	 */
	public void submit(PoseStack poseStack, T animatable, E relatedObject, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                       int packedLight, float partialTick, @Nullable RenderModelPositioner<R> modelPositioner) {
		R renderState = fillRenderState(animatable, relatedObject, createRenderState(animatable, null), partialTick);

		renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);

		submitRenderTasks(renderState, poseStack, renderTasks, cameraState, modelPositioner);
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
    public void renderBone(R renderState, PoseStack poseStack, GeoBone bone, VertexConsumer buffer, CameraRenderState cameraState,
                           int packedLight, int packedOverlay, int renderColor) {
		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());

            bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, renderState.getGeckolibData(DataTickets.OBJECT_RENDER_POSE)));
            bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, renderState.getGeckolibData(DataTickets.MODEL_RENDER_POSE)));
		}

		GeoRenderer.super.renderBone(renderState, poseStack, bone, buffer, cameraState, packedLight, packedOverlay, renderColor);
	}

    /**
     * Called to create the {@link GeoRenderState} for this render pass
     */
    @Override
    public R createRenderState(T animatable, E relatedObject) {
        return (R)new GeoRenderState.Impl();
    }

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeckoLibServices.Client.EVENTS.fireCompileObjectRenderLayers(this);
	}

	/**
	 * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderStateEvent(T animatable, @Nullable E relatedObject, R renderState, float partialTick) {
		GeckoLibServices.Client.EVENTS.fireCompileObjectRenderState(this, renderState, animatable, relatedObject);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
		return GeckoLibServices.Client.EVENTS.fireObjectPreRender(this, renderState, poseStack, model, renderTasks, cameraState);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
		GeckoLibServices.Client.EVENTS.fireObjectPostRender(this, renderState, poseStack, model, renderTasks, cameraState);
	}
}
