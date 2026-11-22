package software.bernie.geckolib.cache.texture;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.resource.GeoGlowingTextureMeta;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Texture object type responsible for GeckoLib's emissive render textures
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Emissive-Textures-Glow-Layer">GeckoLib Wiki - Glow Layers</a>
 */
public class AutoGlowingTexture extends GeoAbstractTexture {
	private static final RenderStateShard.ShaderStateShard SHADER_STATE = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentEmissiveShader);
	private static final RenderStateShard.TransparencyStateShard TRANSPARENCY_STATE = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	private static final RenderStateShard.WriteMaskStateShard WRITE_MASK = new RenderStateShard.WriteMaskStateShard(true, true);
	private static final BiFunction<ResourceLocation, Boolean, RenderType> GLOWING_RENDER_TYPE = Util.memoize((texture, isGlowing) -> {
		RenderStateShard.TextureStateShard textureState = new RenderStateShard.TextureStateShard(texture, false, false);

		return RenderType.create("geo_glowing_layer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
				RenderType.CompositeState.builder()
						.setShaderState(SHADER_STATE)
						.setTextureState(textureState)
						.setTransparencyState(TRANSPARENCY_STATE)
						.setOverlayState(new RenderStateShard.OverlayStateShard(true))
						.setWriteMaskState(WRITE_MASK).createCompositeState(isGlowing));
	});
	/**
	 * @deprecated Use {@link #GLOWING_RENDER_TYPE}
	 */
	@Deprecated(forRemoval = true)
	private static final Function<ResourceLocation, RenderType> RENDER_TYPE_FUNCTION = Util.memoize(texture -> GLOWING_RENDER_TYPE.apply(texture, false));
	private static final String APPENDIX = "_glowmask";
	/**
	 * Set to true <u><b>IN DEV</b></u> to have GeckoLib print out the base texture and generated glowlayer textures to the base game directory (./run)
	 */
	public static boolean PRINT_DEBUG_IMAGES = false;

	protected final ResourceLocation textureBase;
	protected final ResourceLocation glowLayer;

	public AutoGlowingTexture(ResourceLocation originalLocation, ResourceLocation location) {
		this.textureBase = originalLocation;
		this.glowLayer = location;
	}

	/**
	 * Get the emissive resource equivalent of the input resource path
	 * <p>
	 * Additionally, prepares the texture manager for the missing texture if the resource is not present
	 *
	 * @return The glowlayer resourcepath for the provided input path
	 */
	public static ResourceLocation getEmissiveResource(ResourceLocation baseResource) {
		ResourceLocation path = appendToPath(baseResource, APPENDIX);

		generateTexture(path, textureManager -> textureManager.register(path, new AutoGlowingTexture(baseResource, path)));

		return path;
	}

	/**
	 * Generates the glow layer {@link NativeImage} and appropriately modifies the base texture for use in glow render layers
	 */
	@Nullable
	@Override
	protected RenderCall loadTexture(ResourceManager resourceManager, Minecraft mc) throws IOException {
		final AbstractTexture baseTexture;

		try {
			baseTexture = mc.submit(() -> mc.getTextureManager().getTexture(this.textureBase)).get();
		}
		catch (InterruptedException | ExecutionException ex) {
			GeckoLibConstants.LOGGER.error("Failed to load base texture for AutoGlowingTexture: {}", this.textureBase, ex);

			return null;
		}

		final Resource baseResource = resourceManager.getResource(this.textureBase).orElse(null);
		final NativeImage baseImage;

		if (baseTexture instanceof DynamicTexture dynamicTexture) {
			baseImage = dynamicTexture.getPixels();
		}
		else if (baseResource != null) {
			try (InputStream stream = baseResource.open()) {
				baseImage = NativeImage.read(stream);
			}
			catch (IOException ex) {
				GeckoLibConstants.LOGGER.error("Unable to load base texture for AutoGlowingTexture: {}", this.textureBase, ex);

				return null;
			}
		}
		else {
			baseImage = null;
		}

		if (baseImage == null) {
			GeckoLibConstants.LOGGER.error("Unable to find base texture '{}' for AutoGlowingTexture creation", this.textureBase);

			return null;
		}

		final Resource glowLayerResource = resourceManager.getResource(this.glowLayer).orElse(null);
		NativeImage glowImage = null;
		GeoGlowingTextureMeta glowLayerMeta = null;

		try {
			if (glowLayerResource != null) {
				try (InputStream stream = glowLayerResource.open()) {
					glowImage = NativeImage.read(stream);
				}

				if (glowImage.getWidth() != baseImage.getWidth() || glowImage.getHeight() != baseImage.getHeight())
					throw new IllegalStateException("Glowmask texture dimensions do not match base texture dimensions! Mask: " + this.glowLayer + ", Base: " + this.textureBase);

				glowLayerMeta = GeoGlowingTextureMeta.fromExistingImage(glowImage);
			}
			else if (baseResource != null) {
				glowLayerMeta = baseResource.metadata().getSection(GeoGlowingTextureMeta.DESERIALIZER).orElse(null);

				if (glowLayerMeta != null)
					glowImage = new NativeImage(baseImage.getWidth(), baseImage.getHeight(), true);
			}
		}
		catch (IOException | RuntimeException ex) {
			GeckoLibConstants.LOGGER.error("Failed to load glowmask for AutoGlowingTexture: {}", this.glowLayer, ex);

			glowLayerMeta = null;
		}

		if (glowLayerMeta == null) {
			if (glowImage != null)
				glowImage.close();

			if (!(baseTexture instanceof DynamicTexture))
				baseImage.close();

			return null;
		}

		glowLayerMeta.createImageMask(baseImage, glowImage);

		TextureMetadataSection textureMeta = null;

		if (baseResource != null) {
			try {
				textureMeta = baseResource.metadata().getSection(TextureMetadataSection.SERIALIZER).orElse(null);
			}
			catch (IOException | RuntimeException ex) {
				GeckoLibConstants.LOGGER.warn("Failed to read texture metadata for AutoGlowingTexture base: {}", this.textureBase, ex);
			}
		}

		final boolean blur = textureMeta != null && textureMeta.isBlur();
		final boolean clamp = textureMeta != null && textureMeta.isClamp();
		final NativeImage mask = glowImage;
		final boolean animated = baseTexture instanceof AnimatableTexture animatableTexture && animatableTexture.isAnimated();

		if (animated)
			((AnimatableTexture)baseTexture).animationContents.animatedTexture.setGlowMaskTexture(this, baseImage, mask);

		return () -> {
			if (!animated)
				uploadSimple(getId(), mask, blur, clamp);

			if (baseTexture instanceof DynamicTexture dynamicTexture) {
				dynamicTexture.upload();
			}
			else {
				uploadSimple(baseTexture.getId(), baseImage, blur, clamp);
			}
		};
	}

	/**
	 * Return a cached instance of the RenderType for the given texture for AutoGlowingGeoLayer rendering
	 *
	 * @param texture The texture of the resource to apply a glow layer to
	 */
	public static RenderType getRenderType(ResourceLocation texture) {
		return GLOWING_RENDER_TYPE.apply(getEmissiveResource(texture), false);
	}

	/**
	 * Return a cached instance of the RenderType for the given texture for AutoGlowingGeoLayer rendering, while the entity has an outline
	 *
	 * @param texture The texture of the resource to apply a glow layer to
	 */
	public static RenderType getOutlineRenderType(ResourceLocation texture) {
		return GLOWING_RENDER_TYPE.apply(getEmissiveResource(texture), true);
	}
}
