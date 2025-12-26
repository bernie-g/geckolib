package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.function.BiConsumer;

/**
 * Render layer base class for rendering additional layers of effects or textures over an existing model at runtime
 * <p>
 * Contains the base boilerplate and helper code for various render layer features
 *
 * @param <T> Animatable class type. Inherited from the renderer this layer is attached to
 * @param <O> Associated object class type, or {@link Void} if none. Inherited from the renderer this layer is attached to
 * @param <R> RenderState class type. Inherited from the renderer this layer is attached to
 */
public abstract class GeoRenderLayer<T extends GeoAnimatable, O, R extends GeoRenderState> {
	protected final GeoRenderer<T, O, R> renderer;

	public GeoRenderLayer(GeoRenderer<T, O, R> renderer) {
		this.renderer = renderer;
	}

	/**
	 * Get the {@link GeoModel} currently being rendered
	 */
	public GeoModel<T> getGeoModel() {
		return this.renderer.getGeoModel();
	}

	/**
	 * Gets the {@link BakedGeoModel} instance that is currently being used.
	 * <p>
	 * This can be directly used for re-rendering
	 */
	public BakedGeoModel getDefaultBakedModel(GeoRenderState renderState) {
		return getGeoModel().getBakedModel(getGeoModel().getModelResource(renderState));
	}

	/**
	 * Get the renderer responsible for the current render operation
	 */
	public GeoRenderer<T, O, R> getRenderer() {
		return this.renderer;
	}

	/**
	 * Get the texture resource path for the given {@link GeoRenderState}.
	 * <p>
	 * By default, falls back to {@link GeoModel#getTextureResource(GeoRenderState)}
	 */
	protected Identifier getTextureResource(R renderState) {
		return this.renderer.getTextureLocation(renderState);
	}

	/**
	 * Override to add any custom {@link DataTicket}s you need to capture for rendering.
	 * <p>
	 * The animatable is discarded from the rendering context after this, so any data needed
	 * for rendering should be captured in the renderState provided
	 *
	 * @param animatable The animatable instance being rendered
	 * @param relatedObject An object related to the render pass or null if not applicable.
	 *                         (E.G., ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	 * @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
     * @param partialTick The fraction of a tick that has elapsed as of the current render pass
	 */
	@ApiStatus.OverrideOnly
	public void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {}

	/**
	 * This method is called by the {@link GeoRenderer} before rendering, immediately after {@link GeoRenderer#preRenderPass} has been called
	 * <p>
	 * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones.
	 * <p>
	 * <b><u>NOTE:</u></b> Changing VertexConsumers or RenderTypes must not be performed here<br>
	 * <b><u>NOTE:</u></b> If the passed {@link VertexConsumer buffer} is null, then the animatable was not actually rendered (invisible, etc.)
	 * and you may need to factor this in to your design
	 */
	public void preRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {}

	/**
	 * This is the method that is actually called by the render for your render layer to function
	 * <p>
	 * This is called <i>after</i> the animatable has been submitted for rendering, but before supplementary rendering submissions like nametags
	 */
	public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {}

	/**
	 * Register per-bone render operations, to be rendered after the main model is done.
	 * <p>
	 * Even though the task is called after the main model renders, the {@link PoseStack} provided will be posed as if the bone
	 * is currently rendering.
	 *
     * @param renderPassInfo The collated render-related data for this render pass
	 * @param consumer The registrar to accept the per-bone render tasks
	 */
	public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {}
}