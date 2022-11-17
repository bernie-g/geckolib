package software.bernie.geckolib3.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.model.GeoModel;
import software.bernie.geckolib3.renderer.GeoRenderer;

/**
 * Render layer base class for rendering additional layers of effects or textures over an existing model at runtime.<br>
 * Contains the base boilerplate and helper code for various render layer features
 */
public abstract class GeoRenderLayer<T extends GeoAnimatable> {
	protected final GeoRenderer<T> renderer;

	public GeoRenderLayer(GeoRenderer<T> entityRendererIn) {
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
	 * This can be directly used for re-rendering
	 */
	public BakedGeoModel getDefaultBakedModel(T animatable) {
		return getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));
	}

	/**
	 * Get the renderer responsible for the current render operation
	 */
	public GeoRenderer<T> getRenderer(){
		return this.renderer;
	}

	/**
	 * Get the texture resource path for the given {@link GeoAnimatable}.<br>
	 * By default, falls back to {@link GeoModel#getTextureResource(GeoAnimatable)}
	 */
	protected ResourceLocation getTextureResource(T animatable) {
		return this.renderer.getTextureLocation(animatable);
	}

	/**
	 * This method is called by the {@link GeoRenderer} before rendering, immediately after {@link GeoRenderer#preRender} has been called.<br>
	 * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones
	 */
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType,
						  MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
						  int packedLight, int packedOverlay) {}

	/**
	 * This is the method that is actually called by the render for your render layer to function.<br>
	 * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags.
	 */
	public abstract void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType,
								MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
								int packedLight, int packedOverlay);

	/**
	 * Renders the provided {@link BakedGeoModel} using the existing {@link GeoRenderer}.<br>
	 * Usually you'd use this for rendering alternate {@link RenderType} layers or for sub-model rendering
	 */
	protected final void renderModel(BakedGeoModel model, PoseStack poseStack, MultiBufferSource bufferSource, T animatable,
							   RenderType renderType, VertexConsumer buffer, float partialTick,
							   int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		getRenderer().actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}
}