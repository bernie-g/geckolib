package software.bernie.geckolib.loading;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.util.JsonUtil;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Extracts raw information from given files, and other similar functions
 */
public final class FileLoader {

	public static BakedAnimations loadAnimationsFile(ResourceLocation location, ResourceManager manager) {
		try {
			JsonObject json = loadFile(location, manager);
			return JsonUtil.GEO_GSON.fromJson(GsonHelper.getAsJsonObject(json, "animations"), BakedAnimations.class);
		} catch (Exception e) {
			GeckoLib.LOGGER.warn("Couldn't load " + location);
			return new BakedAnimations();
		}
	}

	public static Model loadModelFile(ResourceLocation location, ResourceManager manager) {
		try {
			JsonObject json = loadFile(location, manager);
			return JsonUtil.GEO_GSON.fromJson(json, Model.class);
		} catch (Exception e) {
			GeckoLib.LOGGER.warn("Couldn't load " + location);
			return new Model();
		}
	}

	public static JsonObject loadFile(ResourceLocation location, ResourceManager manager) {
		try {
			String contents = getFileContents(location, manager);
			return GsonHelper.fromJson(JsonUtil.GEO_GSON, contents, JsonObject.class);
		} catch (Exception e) {
			GeckoLib.LOGGER.warn("Couldn't load " + location);
			return new JsonObject();
		}
	}

	public static String getFileContents(ResourceLocation location, ResourceManager manager) {
		try (InputStream inputStream = manager.getResourceOrThrow(location).open()) {
			return IOUtils.toString(inputStream, Charset.defaultCharset());
		} catch (Exception e) {
			GeckoLib.LOGGER.warn("Couldn't load " + location);
			return "";
		}
	}
}
