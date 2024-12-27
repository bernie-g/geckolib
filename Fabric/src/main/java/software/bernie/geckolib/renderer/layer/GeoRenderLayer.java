package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

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
		return getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable, getRenderer()));
	}

	/**
	 * Get the renderer responsible for the current render operation
	 */
	public GeoRenderer<T> getRenderer() {
		return this.renderer;
	}

	/**
	 * Get the texture resource path for the given {@link GeoAnimatable}.<br>
	 * By default, falls back to {@link GeoRenderer#getTextureLocation(GeoAnimatable)}
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
	public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType,
								MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
								int packedLight, int packedOverlay) {}

	/**
	 * This method is called by the {@link GeoRenderer} for each bone being rendered.<br>
	 * This is a more expensive call, particularly if being used to render something on a different buffer.<br>
	 * It does however have the benefit of having the matrix translations and other transformations already applied from render-time.<br>
	 * It's recommended to avoid using this unless necessary.<br>
	 * <br>
	 * The {@link GeoBone} in question has already been rendered by this stage.<br>
	 * <br>
	 * If you <i>do</i> use it, and you render something that changes the {@link VertexConsumer buffer}, you need to reset it back to the previous buffer
	 * using {@link MultiBufferSource#getBuffer} before ending the method
	 */
	public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType,
							  MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {}
}