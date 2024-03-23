package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * {@link GeoRenderLayer} for rendering the auto-generated glowlayer functionality implemented by Geckolib using the <i>_glowing</i> appendixed texture files
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Emissive-Textures-Glow-Layer">GeckoLib Wiki - Glow Layers</a>
 */
public class AutoGlowingGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
	public AutoGlowingGeoLayer(GeoRenderer<T> renderer) {
		super(renderer);
	}

	/**
	 * Get the render type to use for this glowlayer renderer
	 * <p>
	 * Uses {@link RenderType#eyes(ResourceLocation)} by default, which may not be ideal in all circumstances
	 */
	protected RenderType getRenderType(T animatable) {
		return AutoGlowingTexture.getRenderType(getTextureResource(animatable));
	}

	/**
	 * This is the method that is actually called by the render for your render layer to function
	 * <p>
	 * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags
	 */
	@Override
	public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		renderType = getRenderType(animatable);

		getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
				bufferSource.getBuffer(renderType), partialTick, 15728640, OverlayTexture.NO_OVERLAY,
				1, 1, 1, 1);
	}
}
