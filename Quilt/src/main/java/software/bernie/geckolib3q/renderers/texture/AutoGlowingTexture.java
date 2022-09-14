package software.bernie.geckolib3q.renderers.texture;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import software.bernie.geckolib3q.resource.data.GlowingMetadataSection;

/*
 * Copyright: DerToaster98 - 13.06.2022
 * 
 * Custom texture that automatically creates the emissive texture on load based on a config in the metadata
 * 
 * Originally developed for chocolate quest repoured
 */
public class AutoGlowingTexture extends GeoAbstractTexture {

	public AutoGlowingTexture(ResourceLocation originalLocation, ResourceLocation location) {
		super(originalLocation, location);
	}

	public static ResourceLocation get(ResourceLocation originalLocation) {
		return get(originalLocation, "_glowing", AutoGlowingTexture::new);
	}

	@Override
	protected boolean onLoadTexture(Resource resource, NativeImage originalImage, NativeImage newImage) {
		GlowingMetadataSection glowingMetadata = null;
		try {
			glowingMetadata = resource.getMetadata(GlowingMetadataSection.SERIALIZER);
		} catch (RuntimeException e) {
			LOGGER.warn("Failed reading glowing metadata of: {}", location, e);
		}

		if (glowingMetadata == null || glowingMetadata.isEmpty()) {
			return false;
		}
		glowingMetadata.getGlowingSections().forEach(section -> section.forEach((x, y) -> {
			newImage.setPixelRGBA(x, y, originalImage.getPixelRGBA(x, y));

			// Remove it from the original
			originalImage.setPixelRGBA(x, y, 0);
		}));
		return true;
	}

}
