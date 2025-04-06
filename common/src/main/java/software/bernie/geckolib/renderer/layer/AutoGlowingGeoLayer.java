package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

/**
 * {@link GeoRenderLayer} for rendering the auto-generated glowlayer functionality implemented by Geckolib using the <i>_glowing</i> appendixed texture files
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Emissive-Textures-Glow-Layer">GeckoLib Wiki - Glow Layers</a>
 */
public class AutoGlowingGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
	public AutoGlowingGeoLayer(GeoRenderer<T, O, R> renderer) {
		super(renderer);
	}

	/**
	 * Return the texture for the emissive rendering layer
	 * <p>
	 * You probably shouldn't override this unless you know what you're doing
	 */
	protected ResourceLocation getTextureForGlowlayer(R renderState) {
		return getTextureResource(renderState);// AutoGlowingTexture.getOrCreateEmissiveTexture(getTextureResource(renderState));
	}

	/**
	 * Get the render type to use for this glowlayer renderer, or null if the layer should not render
	 * <p>
	 * Uses a custom RenderType similar to {@link RenderType#eyes(ResourceLocation)} by default, which may not be ideal in all circumstances.<br>
	 * Automatically accounts for entity states like invisibility and glowing
	 */
	@Nullable
	protected RenderType getRenderType(R renderState) {
		return null;
		/*ResourceLocation texture = getTextureForGlowlayer(renderState);

		if (!(renderState instanceof EntityRenderState entityRenderState))
			return AutoGlowingTexture.getRenderType(texture);

		boolean invisible = entityRenderState.isInvisible;

		if (invisible && !renderState.getGeckolibData(DataTickets.INVISIBLE_TO_PLAYER))
			return RenderType.itemEntityTranslucentCull(texture);

		if (renderState.getGeckolibData(DataTickets.IS_GLOWING)) {
			if (invisible)
				return RenderType.outline(texture);

			return AutoGlowingTexture.getOutlineRenderType(texture);
		}

		return invisible ? null : AutoGlowingTexture.getRenderType(texture);*/
	}

	/**
	 * This is the method that is actually called by the render for your render layer to function
	 * <p>
	 * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags
	 */
	@Override
	public void render(R renderState, PoseStack poseStack, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
					   int packedLight, int packedOverlay, int renderColor) {
		renderType = getRenderType(renderState);

		if (renderType != null)
			getRenderer().reRender(renderState, poseStack, bakedModel, bufferSource, renderType, bufferSource.getBuffer(renderType), LightTexture.FULL_SKY, packedOverlay, renderColor);
	}
}
