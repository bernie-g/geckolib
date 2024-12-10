package software.bernie.geckolib.cache.texture;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.GeckoLibServices;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Abstract texture wrapper for GeckoLib textures
 * <p>
 * Mostly just handles boilerplate
 */
public abstract class GeoAbstractTexture extends ReloadableTexture {
	public GeoAbstractTexture(ResourceLocation resourceId) {
		super(resourceId);
	}

	/**
	 * Generates the texture instance for the given path with the given appendix if it hasn't already been generated
	 */
	protected static void generateTexture(ResourceLocation texturePath, Consumer<TextureManager> textureManagerConsumer) {
		if (!RenderSystem.isOnRenderThreadOrInit())
			throw new IllegalThreadStateException("Texture loading called outside of the render thread! This should DEFINITELY not be happening.");

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();

		if (!(textureManager.getTexture(texturePath) instanceof GeoAbstractTexture))
			textureManagerConsumer.accept(textureManager);
	}

	@Override
	public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
		return loadTexture(resourceManager, Minecraft.getInstance());
	}

	/**
	 * Debugging function to write out the generated glowmap image to disk
	 */
	protected void printDebugImageToDisk(ResourceLocation id, NativeImage newImage) {
		try {
			File file = new File(GeckoLibServices.PLATFORM.getGameDir().toFile(), "GeoTexture Debug Printouts");

			if (!file.exists()) {
				file.mkdirs();
			}
			else if (!file.isDirectory()) {
				file.delete();
				file.mkdirs();
			}

			file = new File(file, id.getPath().replace('/', '.'));

			if (!file.exists())
				file.createNewFile();

			newImage.writeToFile(file);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Called from {@link ReloadableTexture#loadContents} to create the {@link NativeImage} for this texture to be uploaded to memory
	 * <p>
	 * Perform any necessary pre-computation work here, then return this texture's associated TextureContents
	 *
	 * @return The TextureContents to submit to for upload
	 */
	protected abstract TextureContents loadTexture(ResourceManager resourceManager, Minecraft mc) throws IOException;

	/**
	 * No-frills helper method for uploading {@link AbstractTexture textures} into memory for use
	 */
	public static void uploadTexture(AbstractTexture texture, TextureContents textureContents) {
		boolean clamp = textureContents.clamp();
		boolean blur = texture.defaultBlur = textureContents.blur();
		NativeImage image = textureContents.image();
		RenderCall uploadTask = () -> {
			TextureUtil.prepareImage(texture.getId(), 0, image.getWidth(), image.getHeight());
			texture.setFilter(blur, false);
			texture.setClamp(clamp);
			image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), true);
		};

		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(uploadTask);
		}
		else {
			uploadTask.execute();
		}
	}

	/**
	 * Append a suffix to a given ResourceLocation's path
	 * <p>
	 * E.G.
	 * <code>("minecraft:test_path", "_extended") -> "minecraft:test_path_extended"</code>
	 *
	 * @param location The base ResourceLocation
	 * @param suffix The suffix to append literally to the base location's path
	 *
	 * @return The newly created ResourceLocation
	 */
	public static ResourceLocation appendToPath(ResourceLocation location, String suffix) {
		String path = location.getPath();
		int i = path.lastIndexOf('.');

		return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), path.substring(0, i) + suffix + path.substring(i));
	}
}
