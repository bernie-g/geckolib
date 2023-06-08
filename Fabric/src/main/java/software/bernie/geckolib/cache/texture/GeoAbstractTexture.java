package software.bernie.geckolib.cache.texture;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Abstract texture wrapper for GeckoLib textures.<br>
 * Mostly just handles boilerplate
 */
public abstract class GeoAbstractTexture extends AbstractTexture {
	/**
	 * Generates the texture instance for the given path with the given appendix if it hasn't already been generated
	 */
	protected static void generateTexture(ResourceLocation texturePath, Consumer<TextureManager> textureManagerConsumer) {
		if (!RenderSystem.isOnRenderThreadOrInit())
			throw new IllegalThreadStateException("Texture loading called outside of the render thread! This should DEFINITELY not be happening.");

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();

		if (!(textureManager.getTexture(texturePath, MissingTextureAtlasSprite.getTexture()) instanceof GeoAbstractTexture))
			textureManagerConsumer.accept(textureManager);
	}

	@Override
	public final void load(ResourceManager resourceManager) throws IOException {
		RenderCall renderCall = loadTexture(resourceManager, Minecraft.getInstance());

		if (renderCall == null)
			return;

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
	protected void printDebugImageToDisk(ResourceLocation id, NativeImage newImage) {
		try {
			File file = new File(FabricLoader.getInstance().getGameDir().toFile(), "GeoTexture Debug Printouts");

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
	 * Called at {@link AbstractTexture#load} time to load this texture for the first time into the render cache.
	 * Generate and apply the necessary functions here, then return the RenderCall to submit to the render pipeline.
	 * @return The RenderCall to submit to the render pipeline, or null if no further action required
	 */
	@Nullable
	protected abstract RenderCall loadTexture(ResourceManager resourceManager, Minecraft mc) throws IOException;

	/**
	 * No-frills helper method for uploading {@link NativeImage images} into memory for use
	 */
	public static void uploadSimple(int texture, NativeImage image, boolean blur, boolean clamp) {
		TextureUtil.prepareImage(texture, 0, image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, true);
	}

	public static ResourceLocation appendToPath(ResourceLocation location, String suffix) {
		String path = location.getPath();
		int i = path.lastIndexOf('.');

		return new ResourceLocation(location.getNamespace(), path.substring(0, i) + suffix + path.substring(i));
	}
}
