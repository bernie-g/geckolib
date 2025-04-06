package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Render layer base class for rendering additional layers of effects or textures over an existing model at runtime
 * <p>
 * Contains the base boilerplate and helper code for various render layer features
 */
public abstract class GeoRenderLayer<T extends GeoAnimatable, O, R extends GeoRenderState> {
	protected final GeoRenderer<T, O, R> renderer;

	public GeoRenderLayer(GeoRenderer<T, O, R> entityRendererIn) {
		this.renderer = entityRendererIn;
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
	protected ResourceLocation getTextureResource(R renderState) {
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
	 *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	 * @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
	 */
	@ApiStatus.OverrideOnly
	public void addRenderData(T animatable, O relatedObject, R renderState) {}

	/**
	 * This method is called by the {@link GeoRenderer} before rendering, immediately after {@link GeoRenderer#preRender} has been called
	 * <p>
	 * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones.
	 * <p>
	 * <b><u>NOTE:</u></b> Changing VertexConsumers or RenderTypes must not be performed here<br>
	 * <b><u>NOTE:</u></b> If the passed {@link VertexConsumer buffer} is null, then the animatable was not actually rendered (invisible, etc)
	 * and you may need to factor this in to your design
	 */
	public void preRender(R renderState, PoseStack poseStack, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
						  int packedLight, int packedOverlay, int renderColor) {}

	/**
	 * This is the method that is actually called by the render for your render layer to function
	 * <p>
	 * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags
	 * <p>
	 * <b><u>NOTE:</u></b> If the passed {@link VertexConsumer buffer} is null, then the animatable was not actually rendered (invisible, etc)
	 * and you may need to factor this in to your design
	 */
	public void render(R renderState, PoseStack poseStack, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
					   int packedLight, int packedOverlay, int renderColor) {}

	/**
	 * This method is called by the {@link GeoRenderer} for each bone being rendered
	 * <p>
	 * You would use this to render something at or for a given GeoBone's position and orientation.
	 * <p>
	 * You <b><u>MUST NOT</u></b> perform any rendering operations here, and instead must contain all your functionality in the returned Runnable
	 */
	@Nullable
	public Runnable createPerBoneRender(R renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource) {
		return null;
	}
}