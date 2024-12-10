package software.bernie.geckolib.cache.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.TriState;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.resource.GeoGlowingTextureMeta;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Texture object type responsible for GeckoLib's emissive render textures
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Emissive-Textures-Glow-Layer">GeckoLib Wiki - Glow Layers</a>
 */
public class AutoGlowingTexture extends GeoAbstractTexture {
	private static final RenderStateShard.ShaderStateShard SHADER_STATE = new RenderStateShard.ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE);
	private static final RenderStateShard.TransparencyStateShard TRANSPARENCY_STATE = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	private static final RenderStateShard.WriteMaskStateShard WRITE_MASK = new RenderStateShard.WriteMaskStateShard(true, true);
	private static final BiFunction<ResourceLocation, Boolean, RenderType> GLOWING_RENDER_TYPE = Util.memoize((texture, isGlowing) -> {
		RenderStateShard.TextureStateShard textureState = new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false);

		return RenderType.create("geo_glowing_layer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
				RenderType.CompositeState.builder()
						.setShaderState(SHADER_STATE)
						.setTextureState(textureState)
						.setTransparencyState(TRANSPARENCY_STATE)
						.setOverlayState(new RenderStateShard.OverlayStateShard(true))
						.setWriteMaskState(WRITE_MASK).createCompositeState(isGlowing));
	});
	private static final String APPENDIX = "_glowmask";
	/**
	 * Set to true <u><b>IN DEV</b></u> to have GeckoLib print out the base texture and generated glowlayer textures to the base game directory (./run)
	 */
	public static boolean PRINT_DEBUG_IMAGES = false;

	protected final ResourceLocation textureBase;

	public AutoGlowingTexture(ResourceLocation originalLocation, ResourceLocation location) {
		super(location);

		this.textureBase = originalLocation;
	}

	/**
	 * Get the emissive resource equivalent of the input resource path
	 * <p>
	 * Additionally prepares the texture manager for the missing texture if the resource is not present
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
	@Override
	protected TextureContents loadTexture(ResourceManager resourceManager, Minecraft mc) throws IOException {
		Resource baseTextureResource = resourceManager.getResourceOrThrow(this.textureBase);
		Optional<Resource> glowmaskResource = resourceManager.getResource(resourceId());
		AbstractTexture baseTexture = mc.getTextureManager().getTexture(this.textureBase);
		NativeImage baseImage = baseTexture instanceof DynamicTexture dynamicTexture ? dynamicTexture.getPixels() : null;
		ResourceMetadata baseTextureMeta = baseTextureResource.metadata();
		TextureMetadataSection baseTextureMetaSection = baseTextureMeta.getSection(TextureMetadataSection.TYPE).orElse(null);

		if (baseImage == null) {
			try (InputStream stream = baseTextureResource.open()) {
				baseImage = NativeImage.read(stream);
			}
		}

		NativeImage referenceImage = baseImage;
		Pair<NativeImage, GeoGlowingTextureMeta> contents = glowmaskResource.map(resource -> {
			NativeImage image;

			try (InputStream stream = resource.open()) {
				image = NativeImage.read(stream);
			}
			catch (IOException e) {
                throw new RuntimeException(e);
            }

            return Pair.of(image, GeoGlowingTextureMeta.fromExistingImage(image));
        }).orElseGet(() -> {
			return Pair.of(new NativeImage(referenceImage.getWidth(), referenceImage.getHeight(), true),
					baseTextureMeta.getSection(GeoGlowingTextureMeta.TYPE).orElseGet(() -> {
						GeckoLibConstants.LOGGER.error("Attempting to use a glowmask but no glowmask or .png.mcmeta was found for texture: {}", this.textureBase);

						return new GeoGlowingTextureMeta(List.of());
					}));
		});

		contents.right().createImageMask(referenceImage, contents.left());

		if (PRINT_DEBUG_IMAGES && GeckoLibServices.PLATFORM.isDevelopmentEnvironment()) {
			printDebugImageToDisk(this.textureBase, referenceImage);
			printDebugImageToDisk(resourceId(), contents.left());
		}

		switch (baseTexture) {
			case ReloadableTexture reloadable -> reloadable.apply(new TextureContents(referenceImage, baseTextureMetaSection));
			case DynamicTexture dynamicTexture -> dynamicTexture.upload();
			default -> uploadTexture(baseTexture, new TextureContents(referenceImage, baseTextureMetaSection));
		}

		return new TextureContents(contents.left(), baseTextureMetaSection);
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
