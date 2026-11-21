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
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.resource.GeoGlowingTextureMeta;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Texture object type responsible for GeckoLib's emissive render textures
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
	private static final Function<ResourceLocation, RenderType> RENDER_TYPE_FUNCTION = Util.memoize(texture -> {
		RenderStateShard.TextureStateShard textureState = new RenderStateShard.TextureStateShard(texture, false, false);

		return RenderType.create("geo_glowing_layer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
								 RenderType.CompositeState.builder()
										 .setShaderState(SHADER_STATE)
										 .setTextureState(textureState)
										 .setTransparencyState(TRANSPARENCY_STATE)
										 .setWriteMaskState(WRITE_MASK).createCompositeState(true));
	});
	private static final String APPENDIX = "_glowmask";

	protected final ResourceLocation textureBase;
	protected final ResourceLocation glowLayer;

	public AutoGlowingTexture(ResourceLocation originalLocation, ResourceLocation location) {
		this.textureBase = originalLocation;
		this.glowLayer = location;
	}

	/**
	 * Get the emissive resource equivalent of the input resource path.<br>
	 * Additionally, prepares the texture manager for the missing texture if the resource is not present
	 * @return The glowlayer resourcepath for the provided input path
	 */
	private static ResourceLocation getEmissiveResource(ResourceLocation baseResource) {
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
			GeckoLib.LOGGER.error("Minecraft failed to create base texture for AutoGlowingTexture: {}", this.textureBase, ex);

			return null;
		}

		final Optional<Resource> baseTextureResource = resourceManager.getResource(this.textureBase);
		final NativeImage baseImage;

		if (baseTexture instanceof DynamicTexture dynamicTexture) {
			baseImage = dynamicTexture.getPixels();
		}
		else if (baseTextureResource.isPresent()) {
			try (InputStream in = baseTextureResource.get().open()) {
				baseImage = NativeImage.read(in);
			}
			catch (IOException ex) {
				GeckoLib.LOGGER.error("Unable to load base texture for AutoGlowingTexture: {}", this.textureBase, ex);

				return null;
			}
		}
		else {
			baseImage = null;
		}

		if (baseImage == null) {
			GeckoLib.LOGGER.error("Unable to find base texture '{}' for AutoGlowingTexture creation", this.textureBase);

			return null;
		}

		final Optional<Resource> glowLayerResource = resourceManager.getResource(this.glowLayer);
		NativeImage glowImage = null;
		GeoGlowingTextureMeta glowLayerMeta = null;

		if (glowLayerResource.isPresent()) {
			try (InputStream in = glowLayerResource.get().open()) {
				glowImage = NativeImage.read(in);
				glowLayerMeta = GeoGlowingTextureMeta.fromExistingImage(glowImage);

				if (glowImage.getWidth() != baseImage.getWidth() || glowImage.getHeight() != baseImage.getHeight()) {
					GeckoLib.LOGGER.error("Glowmask texture dimensions do not match base texture dimensions! Mask: {}, Base: {}", this.glowLayer, this.textureBase);

					if (!(baseTexture instanceof DynamicTexture))
						baseImage.close();

					glowImage.close();

					return null;
				}
			}
			catch (IOException | RuntimeException ex) {
				GeckoLib.LOGGER.error("Failed to load glowmask resources for AutoGlowingTexture: {}", this.glowLayer, ex);

				if (!(baseTexture instanceof DynamicTexture))
					baseImage.close();

				if (glowImage != null)
					glowImage.close();

				return null;
			}
		}
		else if (baseTextureResource.isPresent()) {
			final Optional<GeoGlowingTextureMeta> autoGlowMeta = baseTextureResource.flatMap(resource -> {
				try {
					return resource.metadata().getSection(GeoGlowingTextureMeta.DESERIALIZER);
				}
				catch (Exception ignored) {
					return Optional.empty();
				}
			});

			if (autoGlowMeta.isPresent()) {
				try {
					glowLayerMeta = autoGlowMeta.get();
					glowImage = new NativeImage(baseImage.getWidth(), baseImage.getHeight(), true);
				}
				catch (IllegalArgumentException ex) {
					GeckoLib.LOGGER.error("Texture '{}' for AutoGlowingTexture is empty", this.textureBase);
				}
			}
		}

		if (glowLayerMeta == null || glowImage == null) {
			if (!(baseTexture instanceof DynamicTexture))
				baseImage.close();

			return null;
		}

		glowLayerMeta.createImageMask(baseImage, glowImage);

		final Optional<TextureMetadataSection> textureBaseMeta = baseTextureResource.flatMap(resource -> {
			try {
				return resource.metadata().getSection(TextureMetadataSection.SERIALIZER);
			}
			catch (Exception ignored) {
				return Optional.empty();
			}
		});
		final boolean blur = textureBaseMeta.isPresent() && textureBaseMeta.get().isBlur();
		final boolean clamp = textureBaseMeta.isPresent() && textureBaseMeta.get().isClamp();
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
	 * Return a cached instance of the RenderType for the given texture for GeoGlowingLayer rendering.
	 * @param texture The texture of the resource to apply a glow layer to
	 */
	public static RenderType getRenderType(ResourceLocation texture) {
		return RENDER_TYPE_FUNCTION.apply(getEmissiveResource(texture));
	}
}
