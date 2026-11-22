package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.ClientUtil;

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
	 * Uses a custom RenderType similar to {@link RenderType#eyes(ResourceLocation)} by default, which may not be ideal in all circumstances
	 * @deprecated Use {@link #getRenderType(GeoAnimatable, MultiBufferSource)}
	 */
	@Deprecated(forRemoval = true)
	protected RenderType getRenderType(T animatable) {
		return getRenderType(animatable, null);
	}

	/**
	 * Get the render type to use for this glowlayer renderer, or null if the layer should not render
	 * <p>
	 * Uses a custom RenderType similar to {@link RenderType#eyes(ResourceLocation)} by default, which may not be ideal in all circumstances.<br>
	 * Automatically accounts for entity states like invisibility and glowing
	 *
	 * @param bufferSource Nullable until {@link #getRenderType(GeoAnimatable)} is removed for backward compatibility
	 */
	@Nullable
	protected RenderType getRenderType(T animatable, @Nullable MultiBufferSource bufferSource) {
		final ResourceLocation baseTexture = getTextureResource(animatable);

		if (!(animatable instanceof Entity entity))
			return AutoGlowingTexture.getRenderType(baseTexture);

		final boolean invisible = entity.isInvisible();

		if (invisible && !entity.isInvisibleTo(ClientUtil.getClientPlayer()))
			return RenderType.itemEntityTranslucentCull(AutoGlowingTexture.getEmissiveResource(baseTexture));

		if (Minecraft.getInstance().shouldEntityAppearGlowing(entity)) {
			if (invisible)
				return RenderType.outline(AutoGlowingTexture.getEmissiveResource(baseTexture));

			return AutoGlowingTexture.getOutlineRenderType(baseTexture);
		}

		return invisible ? null : AutoGlowingTexture.getRenderType(baseTexture);
	}

	/**
	 * This is the method that is actually called by the render for your render layer to function
	 * <p>
	 * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags
	 */
	@Override
	public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		renderType = getRenderType(animatable);

		if (renderType != null) {
			getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
								   bufferSource.getBuffer(renderType), partialTick, LightTexture.FULL_SKY, packedOverlay,
					getRenderer().getRenderColor(animatable, partialTick, packedLight).argbInt());
		}
	}
}
