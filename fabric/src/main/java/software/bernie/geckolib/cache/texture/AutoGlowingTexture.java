package software.bernie.geckolib.cache.texture;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.fabricmc.loader.api.FabricLoader;
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
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.resource.GeoGlowingTextureMeta;

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
						.setWriteMaskState(WRITE_MASK).createCompositeState(false));
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
	 * Additionally prepares the texture manager for the missing texture if the resource is not present
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
		AbstractTexture originalTexture;

		try {
			originalTexture = mc.submit(() -> mc.getTextureManager().getTexture(this.textureBase)).get();
		}
		catch (InterruptedException | ExecutionException e) {
			throw new IOException("Failed to load original texture: " + this.textureBase, e);
		}

		Resource textureBaseResource = resourceManager.getResource(this.textureBase).get();
		NativeImage baseImage = originalTexture instanceof DynamicTexture dynamicTexture ?
				dynamicTexture.getPixels() : NativeImage.read(textureBaseResource.open());
		NativeImage glowImage = null;
		Optional<TextureMetadataSection> textureBaseMeta = textureBaseResource.metadata().getSection(TextureMetadataSection.SERIALIZER);
		boolean blur = textureBaseMeta.isPresent() && textureBaseMeta.get().isBlur();
		boolean clamp = textureBaseMeta.isPresent() && textureBaseMeta.get().isClamp();

		try {
			Optional<Resource> glowLayerResource = resourceManager.getResource(this.glowLayer);
			GeoGlowingTextureMeta glowLayerMeta = null;

			if (glowLayerResource.isPresent()) {
				glowImage = NativeImage.read(glowLayerResource.get().open());
				glowLayerMeta = GeoGlowingTextureMeta.fromExistingImage(glowImage);
			}
			else {
				Optional<GeoGlowingTextureMeta> meta = textureBaseResource.metadata().getSection(GeoGlowingTextureMeta.DESERIALIZER);

				if (meta.isPresent()) {
					glowLayerMeta = meta.get();
					glowImage = new NativeImage(baseImage.getWidth(), baseImage.getHeight(), true);
				}
			}

			if (glowLayerMeta != null) {
				glowLayerMeta.createImageMask(baseImage, glowImage);

				if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
					printDebugImageToDisk(this.textureBase, baseImage);
					printDebugImageToDisk(this.glowLayer, glowImage);
				}
			}
		}
		catch (IOException e) {
			GeckoLib.LOGGER.warn("Resource failed to open for glowlayer meta: {}", this.glowLayer, e);
		}

		NativeImage mask = glowImage;

		if (mask == null)
			return null;

		return () -> {
			uploadSimple(getId(), mask, blur, clamp);

			if (originalTexture instanceof DynamicTexture dynamicTexture) {
				dynamicTexture.upload();
			}
			else {
				uploadSimple(originalTexture.getId(), baseImage, blur, clamp);
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
