package software.bernie.geckolib3.cache.texture;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Abstract texture wrapper for GeckoLib textures.<br>
 * Mostly just handles boilerplate.<br>
 * Currently only used for {@link AutoGlowingTexture}
 */
public abstract class GeoAbstractTexture extends AbstractTexture {
	protected final ResourceLocation originalLocation;
	protected final ResourceLocation location;

	protected GeoAbstractTexture(ResourceLocation originalPath, ResourceLocation newPath) {
		this.originalLocation = originalPath;
		this.location = newPath;
	}

	/**
	 * Apply the {@link GeoAbstractTexture#getAppendix() Appendix} to the provided path and prep the
	 * texture manager for its use.
	 * @return The provided path, with the appendix applied to it
	 */
	protected static ResourceLocation checkAndAppendPath(ResourceLocation originalPath, String appendix) {
		if (!RenderSystem.isOnRenderThreadOrInit())
			throw new IllegalThreadStateException("Texture loading called outside of the render thread! This should DEFINITELY not be happening.");


		ResourceLocation location = appendToPath(originalPath, appendix);

		Minecraft.getInstance().getTextureManager().getTexture(location, MissingTextureAtlasSprite.getTexture());

		return location;
	}

	@Override
	public void load(ResourceManager resourceManager) throws IOException {
		Minecraft mc = Minecraft.getInstance();
		AbstractTexture originalTexture;

		try {
			originalTexture = mc.submit(() -> mc.getTextureManager().getTexture(this.originalLocation)).get();
		}
		catch (InterruptedException | ExecutionException e) {
			throw new IOException("Failed to load original texture: " + this.originalLocation, e);
		}

		NativeImage originalImage = originalTexture instanceof DynamicTexture dynamicTexture
				? dynamicTexture.getPixels()
				: NativeImage.read(resourceManager.getResource(this.location).get().open());
		Optional<TextureMetadataSection> textureMetadata = resourceManager.getResource(this.originalLocation).get()
				.metadata().getSection(TextureMetadataSection.SERIALIZER);
		NativeImage newImage = new NativeImage(originalImage.getWidth(), originalImage.getHeight(), true);
		boolean updateOriginal = onLoadTexture(resourceManager.getResource(this.originalLocation).get(), originalImage,
				newImage);

		boolean blur = textureMetadata.isPresent() && textureMetadata.get().isBlur();
		boolean clamp = textureMetadata.isPresent() && textureMetadata.get().isClamp();

		if (!FMLEnvironment.production) {
			debugWriteGeneratedImageToDisk(originalImage, this.originalLocation);
			debugWriteGeneratedImageToDisk(newImage, this.location);
		}

		RenderCall renderCall = () -> {
			uploadSimple(getId(), newImage, blur, clamp);

			if (updateOriginal) {
				if (originalTexture instanceof DynamicTexture dynamicTexture) {
					dynamicTexture.upload();
				}
				else {
					uploadSimple(originalTexture.getId(), originalImage, blur, clamp);
				}
			}
		};

		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(renderCall);
		}
		else {
			renderCall.execute();
		}
	}

	/**
	 * Debugging function to write out the generated glowmap image to disk
	 */
	private void debugWriteGeneratedImageToDisk(NativeImage newImage, ResourceLocation id) {
		try {
			File file = new File(FMLPaths.GAMEDIR.get().toFile(), "autoglow-gen");

			if (!file.exists()) {
				file.mkdirs();
			}
			else if (!file.isDirectory()) {
				file.delete();
				file.mkdirs();
			}

			file = new File(file, id.getPath().replace('/', '#'));

			if (!file.exists())
				file.createNewFile();

			newImage.writeToFile(file);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Callback function for subclasses to handle special loading operations on the original and new images.
	 * @param resource The Resource object responsible
	 * @param originalImage The original image
	 * @param newImage The newly created copy of the original image to upload
	 * @return Whether the original image was also modified or not. Important as the image requires re-uploading if it was modified
	 */
	protected abstract boolean onLoadTexture(Resource resource, NativeImage originalImage, NativeImage newImage);

	/**
	 * No-frills helper method for uploading {@link NativeImage images} into memory for use
	 */
	protected static void uploadSimple(int texture, NativeImage image, boolean blur, boolean clamp) {
		TextureUtil.prepareImage(texture, 0, image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, true);
	}

	public static ResourceLocation appendToPath(ResourceLocation location, String suffix) {
		String path = location.getPath();
		int i = path.lastIndexOf('.');

		return new ResourceLocation(location.getNamespace(), path.substring(0, i) + suffix + path.substring(i));
	}
}
