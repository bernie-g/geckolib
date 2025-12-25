package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibClientServices;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;

import java.util.List;
import java.util.function.Function;

/**
 * Base {@link GeoRenderer} class for rendering anything that isn't already handled by the other builtin GeoRenderer subclasses
 * <p>
 * Before using this class you should ensure your use-case isn't already covered by one of the other existing renderers
 * <p>
 * It is <b>strongly</b> recommended you override {@link GeoRenderer#getInstanceId} if using this renderer
 *
 * @param <T> Animatable class type
 * @param <O> Associated object class type, or {@link Void} if none
 * @param <R> RenderState class type
 */
public class GeoObjectRenderer<T extends GeoAnimatable, O, R extends GeoRenderState> implements GeoRenderer<T, O, R> {
	protected final GeoRenderLayersContainer<T, O, R> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	public GeoObjectRenderer(GeoModel<T> model) {
		this.model = model;
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
        renderPassInfo.poseStack().translate(0.5f, 0.51f, 0.5f);
    }

    /**
     * Initial access point for performing a single render pass; it all begins here.<br>
     * The {@link GeoRenderState} should have already been filled by this stage.
     * <p>
     * All GeckoLib renderers should immediately defer their respective default {@code submit} calls to this, for consistent handling
     * @see #performRenderPass(GeoAnimatable, Object, PoseStack, SubmitNodeCollector, CameraRenderState, int, int, RenderPassInfo.BoneUpdater)
     */
    public void performRenderPass(T animatable, O relatedObject, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState, int packedLight, int partialTick) {
        performRenderPass(animatable, relatedObject, poseStack, renderTasks, cameraState, packedLight, partialTick, null);
    }

    /**
     * Initial access point for performing a single render pass, with an optional pre-defined {@link RenderPassInfo.BoneUpdater} to allow for pre-positioning models from outside the renderer
     */
	public void performRenderPass(T animatable, O relatedObject, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState, int packedLight, int partialTick,
                                  RenderPassInfo.@Nullable BoneUpdater<R> boneUpdater) {
		R renderState = fillRenderState(animatable, relatedObject, createRenderState(animatable, null), partialTick);

		renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);
		GeoRenderer.super.performRenderPass(renderState, poseStack, renderTasks, cameraState, boneUpdater);
	}

    /**
     * Called to create the {@link GeoRenderState} for this render pass
     */
    @Override
    public R createRenderState(T animatable, O relatedObject) {
        return (R)new GeoRenderState.Impl();
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
    public List<GeoRenderLayer<T, O, R>> getRenderLayers() {
        return this.renderLayers.getRenderLayers();
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoObjectRenderer<T, O, R> withRenderLayer(Function<? super GeoObjectRenderer<T, O, R>, GeoRenderLayer<T, O, R>> renderLayer) {
        return withRenderLayer(renderLayer.apply(this));
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoObjectRenderer<T, O, R> withRenderLayer(GeoRenderLayer<T, O, R> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

    /**
     * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
     */
    public GeoObjectRenderer<T, O, R> withScale(float scale) {
        return withScale(scale, scale);
    }

    /**
     * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
     */
    public GeoObjectRenderer<T, O, R> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    /**
     * Create and fire the relevant {@code CompileLayers} event hook for this renderer
     */
    @Override
    public void fireCompileRenderLayersEvent() {
        GeckoLibClientServices.EVENTS.fireCompileObjectRenderLayers(this);
    }

    /**
     * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
     */
    @Override
    public void fireCompileRenderStateEvent(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        GeckoLibClientServices.EVENTS.fireCompileObjectRenderState(this, renderState, animatable, relatedObject);
    }

    /**
     * Create and fire the relevant {@code Pre-Render} event hook for this renderer
     *
     * @return Whether the renderer should proceed based on the cancellation state of the event
     */
    @Override
    public boolean firePreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return GeckoLibClientServices.EVENTS.fireObjectPreRender(renderPassInfo, renderTasks);
    }
    //</editor-fold>
}
