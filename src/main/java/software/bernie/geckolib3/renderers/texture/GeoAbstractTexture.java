package software.bernie.geckolib3.renderers.texture;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

/*
 * Copyright: DerToaster98, Meldexun - 13.06.2022
 * 
 * Generic base class for custom texture objects
 * 
 * Originally developed for chocolate quest repoured
 */
public abstract class GeoAbstractTexture extends AbstractTexture {

	protected static final Logger LOGGER = LogManager.getLogger();
	protected final Identifier originalLocation;
	protected final Identifier location;

	protected GeoAbstractTexture(Identifier originalLocation, Identifier location) {
		this.originalLocation = originalLocation;
		this.location = location;
	}

	protected static Identifier get(Identifier originalLocation, String appendix,
			BiFunction<Identifier, Identifier, AbstractTexture> constructor) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			throw new IllegalThreadStateException();
		}
		Identifier location = appendBeforeEnding(originalLocation, appendix);
		TextureManager texManager = MinecraftClient.getInstance().getTextureManager();
		if (texManager.getTexture(location) == null) {
			texManager.registerTexture(location, constructor.apply(originalLocation, location));
		}
		return location;
	}

	protected static Identifier appendBeforeEnding(Identifier location, String suffix) {
		String path = location.getPath();
		int i = path.lastIndexOf('.');
		return new Identifier(location.getNamespace(), path.substring(0, i) + suffix + path.substring(i));
	}

	@Override
	public void load(ResourceManager resourceManager) throws IOException {
		MinecraftClient mc = MinecraftClient.getInstance();
		TextureManager textureManager = mc.getTextureManager();
		AbstractTexture originalTexture;
		try {
			originalTexture = mc.submit(() -> {
				AbstractTexture texture = textureManager.getTexture(this.originalLocation);
				if (texture == null) {
					texture = new ResourceTexture(this.originalLocation);
					textureManager.registerTexture(this.originalLocation, texture);
				}
				return texture;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IOException("Failed loading original texture: " + this.originalLocation, e);
		}

		NativeImage originalImage = originalTexture instanceof NativeImageBackedTexture
				? ((NativeImageBackedTexture) originalTexture).getImage()
				: NativeImage.read(resourceManager.getResource(location).get().getInputStream());
		Optional<TextureResourceMetadata> textureMetadata = resourceManager.getResource(originalLocation).get()
				.getMetadata().decode(TextureResourceMetadata.READER);
		NativeImage newImage = new NativeImage(originalImage.getWidth(), originalImage.getHeight(), true);
		boolean updateOriginal = this.onLoadTexture(resourceManager.getResource(originalLocation).get(), originalImage,
				newImage);

		boolean blur = textureMetadata != null && textureMetadata.get().shouldBlur();
		boolean clamp = textureMetadata != null && textureMetadata.get().shouldClamp();

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			debugWriteGeneratedImageToDisk(originalImage, originalLocation);
			debugWriteGeneratedImageToDisk(newImage, this.location);
		}

		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> {
				uploadSimple(this.getGlId(), newImage, blur, clamp);

				if (updateOriginal) {
					if (originalTexture instanceof NativeImageBackedTexture) {
						((NativeImageBackedTexture) originalTexture).upload();
					} else {
						uploadSimple(originalTexture.getGlId(), originalImage, blur, clamp);
					}
				}
			});
		} else {
			uploadSimple(this.getGlId(), newImage, blur, clamp);

			if (updateOriginal) {
				if (originalTexture instanceof NativeImageBackedTexture) {
					((NativeImageBackedTexture) originalTexture).upload();
				} else {
					uploadSimple(originalTexture.getGlId(), originalImage, blur, clamp);
				}
			}
		}
	}

	private final void debugWriteGeneratedImageToDisk(NativeImage newImage, Identifier id) {
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
			newImage.writeTo(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @return true to indicate that the original texture was changed and should be
	 *         updated.
	 */
	protected abstract boolean onLoadTexture(Resource resource, NativeImage originalImage, NativeImage newImage);

	private static void uploadSimple(int texture, NativeImage image, boolean blur, boolean clamp) {
		TextureUtil.prepareImage(texture, 0, image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, true);
	}

}
