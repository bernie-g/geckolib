package software.bernie.geckolib3.loading;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.animation.Animation;
import software.bernie.geckolib3.loading.object.BakedAnimations;
import software.bernie.geckolib3.util.json.JsonDeserializer;
import software.bernie.geckolib3.util.json.JsonUtil;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Extracts raw information from given files, and other similar functions
 */
public final class FileLoader {
	public static BakedAnimations loadAllAnimations(ResourceLocation location, ResourceManager manager) {
		Map<String, Animation> bakedAnimations = new Object2ObjectOpenHashMap<>();
		JsonObject fileObj = loadFile(location, manager);

		for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(fileObj, "animations").entrySet()) {
			String name = entry.getKey();
			Animation animation;

			try {
				animation = JsonDeserializer.deserializeJsonToAnimation(
						JsonDeserializer.getAnimation(fileObj, name), parser);
				animationFile.putAnimation(name, animation);
			}
			catch (Exception e) {
				GeckoLib.LOGGER.error("Could not load animation: {}", name, e);

				throw new RuntimeException(e);
			}
		}
		return animationFile;
	}

	private static JsonObject loadFile(ResourceLocation location, ResourceManager manager) {
		return GsonHelper.fromJson(JsonUtil.GEO_GSON, getFileContents(location, manager), JsonObject.class);
	}

	public static String getFileContents(ResourceLocation location, ResourceManager manager) {
		try (InputStream inputStream = manager.getResourceOrThrow(location).open()) {
			return IOUtils.toString(inputStream, Charset.defaultCharset());
		}
		catch (Exception e) {
			GeckoLib.LOGGER.error("Couldn't load " + location, e);

			throw new RuntimeException(new FileNotFoundException(location.toString()));
		}
	}
}
