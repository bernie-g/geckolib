package software.bernie.geckolib3.cache.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.resource.GeoGlowingTextureMeta;

import java.io.IOException;

/**
 * Texture object type responsible for GeckoLib's emissive render textures
 */
public class AutoGlowingTexture extends GeoAbstractTexture {
	private static final String APPENDIX = "_glowing";

	public AutoGlowingTexture(ResourceLocation originalLocation, ResourceLocation location) {
		super(originalLocation, location);
	}

	/**
	 * Get the emissive resource equivalent of the input resource path.<br>
	 * Additionally prepares the texture manager for the missing texture if the resource is not present
	 * @return The glowlayer resourcepath for the provided input path
	 */
	public static ResourceLocation getEmissiveResource(ResourceLocation originalLocation) {
		return checkAndAppendPath(originalLocation, APPENDIX);
	}

	@Override
	protected boolean onLoadTexture(Resource resource, NativeImage originalImage, NativeImage newImage) {
		try {
			resource.metadata().getSection(GeoGlowingTextureMeta.DESERIALIZER)
					.ifPresent(meta -> meta.createImageMask(originalImage, newImage));
		}
		catch (IOException e) {
			GeckoLib.LOGGER.warn("Resource failed to open for glowlayer meta: {}", location, e);

			return false;
		}

		return true;
	}

}
