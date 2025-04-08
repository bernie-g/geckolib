package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

/**
 * Base {@link GeoRenderer} class for rendering anything that isn't already handled by the other builtin GeoRenderer subclasses
 * <p>
 * Before using this class you should ensure your use-case isn't already covered by one of the other existing renderers
 * <p>
 * It is <b>strongly</b> recommended you override {@link GeoRenderer#getInstanceId} if using this renderer
 */
public class GeoObjectRenderer<T extends GeoAnimatable> implements GeoRenderer<T, Void, GeoRenderState> {
	protected final GeoRenderLayersContainer<T, Void, GeoRenderState> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	protected Matrix4f objectRenderTranslations = new Matrix4f();
	protected Matrix4f modelRenderTranslations = new Matrix4f();

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
	public List<GeoRenderLayer<T, Void, GeoRenderState>> getRenderLayers() {
		return this.renderLayers.getRenderLayers();
	}

	/**
	 * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
	 */
	public GeoObjectRenderer<T> addRenderLayer(GeoRenderLayer<T, Void, GeoRenderState> renderLayer) {
		this.renderLayers.addLayer(renderLayer);

		return this;
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoObjectRenderer<T> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoObjectRenderer<T> withScale(float scaleWidth, float scaleHeight) {
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;

		return this;
	}

	/**
	 * The entry render point for this renderer
	 * <p>
	 * Call this whenever you want to render your object
	 *
	 * @param poseStack The PoseStack to render under
	 * @param animatable The {@link T} instance to render
	 * @param bufferSource The BufferSource to render with, or null to use the default
	 * @param renderType The specific RenderType to use, or null to fall back to {@link GeoRenderer#getRenderType}
	 * @param buffer The VertexConsumer to use for rendering, or null to use the default for the RenderType
	 * @param packedLight The light level at the given render position for rendering
	 */
	public void render(PoseStack poseStack, T animatable, @Nullable MultiBufferSource bufferSource, @Nullable RenderType renderType,
					   @Nullable VertexConsumer buffer, int packedLight, float partialTick) {
		if (bufferSource == null)
			bufferSource = Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();

		defaultRender(fillRenderState(animatable, null, new GeoRenderState.Impl(), partialTick), poseStack, bufferSource, renderType, buffer);
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		this.objectRenderTranslations = new Matrix4f(poseStack.last().pose());

		scaleModelForRender(renderState, this.scaleWidth, this.scaleHeight, poseStack, model, isReRender);

		if (!isReRender)
			poseStack.translate(0.5f, 0.51f, 0.5f);
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType,
							   MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		poseStack.pushPose();

		if (!isReRender)
			getGeoModel().handleAnimations(createAnimationState(renderState));

		this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (buffer != null)
			GeoRenderer.super.actuallyRender(renderState, poseStack, model, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);

		poseStack.popPose();
	}

	/**
	 * Called after all render operations are completed and the render pass is considered functionally complete.
	 * <p>
	 * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render maintenance tasks as required
	 */
	@Override
	public void doPostRenderCleanup() {
		this.objectRenderTranslations = null;
		this.modelRenderTranslations = null;
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(GeoRenderState renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.objectRenderTranslations));
		}

		GeoRenderer.super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
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
	public void fireCompileRenderStateEvent(T animatable, Void relatedObject, GeoRenderState renderState) {
		GeckoLibServices.Client.EVENTS.fireCompileObjectRenderState(this, renderState, animatable);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
		return GeckoLibServices.Client.EVENTS.fireObjectPreRender(this, renderState, poseStack, model, bufferSource);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
		GeckoLibServices.Client.EVENTS.fireObjectPostRender(this, renderState, poseStack, model, bufferSource);
	}
}
