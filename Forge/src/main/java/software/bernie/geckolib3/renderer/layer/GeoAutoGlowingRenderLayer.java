package software.bernie.geckolib3.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.renderer.GeoRenderer;

/**
 * {@link GeoRenderLayer} for rendering the auto-generated glowlayer functionality implemented by Geckolib using
 * the <i>_glowing</i> appendixed texture files.
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Emissive-Textures-Glow-Layer">GeckoLib Wiki - Glow Layers</a>
 */
public class GeoAutoGlowingRenderLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
	public GeoAutoGlowingRenderLayer(GeoRenderer<T> renderer) {
		super(renderer);
	}

	/**
	 * Get the render type to use for this glowlayer renderer.<br>
	 * Uses {@link RenderType#eyes(ResourceLocation)} by default, which may not be ideal in all circumstances.
	 */
	protected RenderType getRenderType(T animatable) {
		return RenderType.eyes(AutoGlowingTexture.getEmissiveResource(getTextureResource(animatable)));
	}

	@Override
	public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		RenderType emissiveRenderType = getRenderType(animatable);

		renderModel(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, emissiveRenderType,
				bufferSource.getBuffer(emissiveRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
				1, 1, 1, 1);
	}

}
