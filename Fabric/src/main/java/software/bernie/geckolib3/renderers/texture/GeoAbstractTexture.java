package software.bernie.geckolib3.renderers.texture;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/*
 * Copyright: DerToaster98, Meldexun - 13.06.2022
 * 
 * Generic base class for custom texture objects
 * 
 * Originally developed for chocolate quest repoured
 */
public abstract class GeoAbstractTexture extends AbstractTexture {

	protected static final Logger LOGGER = LogManager.getLogger();
	protected final ResourceLocation originalLocation;
	protected final ResourceLocation location;

	protected GeoAbstractTexture(ResourceLocation originalLocation, ResourceLocation location) {
		this.originalLocation = originalLocation;
		this.location = location;
	}

	protected static ResourceLocation get(ResourceLocation originalLocation, String appendix,
			BiFunction<ResourceLocation, ResourceLocation, AbstractTexture> constructor) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			throw new IllegalThreadStateException();
		}
		ResourceLocation location = appendBeforeEnding(originalLocation, appendix);
		TextureManager texManager = Minecraft.getInstance().getTextureManager();
		// Necessary, some time after 1.16 this was changed. Method with just the
		// location will try to create a new simpletexture fromt aht which will fail
		// here
		// Overload with second param (default value) will just call getOrDefault() on
		// the internal map
		texManager.getTexture(location, MissingTextureAtlasSprite.getTexture());
		return location;
	}

	public static ResourceLocation appendBeforeEnding(ResourceLocation location, String suffix) {
		String path = location.getPath();
		int i = path.lastIndexOf('.');
		return new ResourceLocation(location.getNamespace(), path.substring(0, i) + suffix + path.substring(i));
	}

	@Override
	public void load(ResourceManager resourceManager) throws IOException {
		Minecraft mc = Minecraft.getInstance();
		TextureManager textureManager = mc.getTextureManager();
		AbstractTexture originalTexture;
		try {
			originalTexture = mc.submit(() -> textureManager.getTexture(this.originalLocation)).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IOException("Failed loading original texture: " + this.originalLocation, e);
		}

		NativeImage originalImage = originalTexture instanceof DynamicTexture
				? ((DynamicTexture) originalTexture).getPixels()
				: NativeImage.read(resourceManager.getResource(location).get().open());
		Optional<TextureMetadataSection> textureMetadata = resourceManager.getResource(originalLocation).get()
				.metadata().getSection(TextureMetadataSection.SERIALIZER);
		NativeImage newImage = new NativeImage(originalImage.getWidth(), originalImage.getHeight(), true);
		boolean updateOriginal = this.onLoadTexture(resourceManager.getResource(originalLocation).get(), originalImage,
				newImage);

		boolean blur = textureMetadata != null && textureMetadata.get().isBlur();
		boolean clamp = textureMetadata != null && textureMetadata.get().isClamp();

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			debugWriteGeneratedImageToDisk(originalImage, originalLocation);
			debugWriteGeneratedImageToDisk(newImage, this.location);
		}

		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> {
				uploadSimple(this.getId(), newImage, blur, clamp);

				if (updateOriginal) {
					if (originalTexture instanceof DynamicTexture) {
						((DynamicTexture) originalTexture).upload();
					} else {
						uploadSimple(originalTexture.getId(), originalImage, blur, clamp);
					}
				}
			});
		} else {
			uploadSimple(this.getId(), newImage, blur, clamp);

			if (updateOriginal) {
				if (originalTexture instanceof DynamicTexture) {
					((DynamicTexture) originalTexture).upload();
				} else {
					uploadSimple(originalTexture.getId(), originalImage, blur, clamp);
				}
			}
		}
	}

	private final void debugWriteGeneratedImageToDisk(NativeImage newImage, ResourceLocation id) {
		try {
			File file = new File(FabricLoader.getInstance().getGameDir().toFile(), "autoglow-gen");
			if (!file.exists()) {
				file.mkdirs();
			} else if (!file.isDirectory()) {
				file.delete();
				file.mkdirs();
			}
			file = new File(file, id.getPath().replace('/', '#'));
			if (!file.exists()) {
				file.createNewFile();
			}
			newImage.writeToFile(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @return true to indicate that the original texture was changed and should be
	 *         updated.
	 */
	protected abstract boolean onLoadTexture(Resource resource, NativeImage originalImage, NativeImage newImage);

	protected static void uploadSimple(int texture, NativeImage image, boolean blur, boolean clamp) {
		TextureUtil.prepareImage(texture, 0, image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, true);
	}

}
