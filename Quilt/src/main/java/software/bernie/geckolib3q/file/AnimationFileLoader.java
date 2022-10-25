package software.bernie.geckolib3q.file;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3q.GeckoLib;
import software.bernie.geckolib3q.util.json.JsonAnimationUtils;

public class AnimationFileLoader {

	public AnimationFile loadAllAnimations(MolangParser parser, ResourceLocation location, ResourceManager manager) {
		AnimationFile animationFile = new AnimationFile();
		JsonObject jsonRepresentation = loadFile(location, manager);
		for (Map.Entry<String, JsonElement> entry : JsonAnimationUtils.getAnimations(jsonRepresentation)) {
			String animationName = entry.getKey();
			Animation animation;
			try {
				animation = JsonAnimationUtils.deserializeJsonToAnimation(
						JsonAnimationUtils.getAnimation(jsonRepresentation, animationName), parser);
				animationFile.putAnimation(animationName, animation);
			} catch (ChainedJsonException e) {
				GeckoLib.LOGGER.error("Could not load animation: {}", animationName, e);
				throw new RuntimeException(e);
			}
		}
		return animationFile;
	}

	/**
	 * Internal method for handling reloads of animation files. Do not override.
	 */
	private JsonObject loadFile(ResourceLocation location, ResourceManager manager) {
		String content = getResourceAsString(location, manager);
		Gson GSON = new Gson();
		return GsonHelper.fromJson(GSON, content, JsonObject.class);
	}

	public static String getResourceAsString(ResourceLocation location, ResourceManager manager) {
		try (InputStream inputStream = manager.getResource(location).getInputStream()) {
			return IOUtils.toString(inputStream, Charset.defaultCharset());
		} catch (Exception e) {
			String message = "Couldn't load " + location;
			GeckoLib.LOGGER.error(message, e);
			throw new RuntimeException(new FileNotFoundException(location.toString()));
		}
	}

}
