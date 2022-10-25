package software.bernie.geckolib3.file;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.util.json.JsonAnimationUtils;

public class AnimationFileLoader {

	public AnimationFile loadAllAnimations(MolangParser parser, Identifier location, ResourceManager manager) {
		AnimationFile animationFile = new AnimationFile();
		JsonObject jsonRepresentation = loadFile(location, manager);
		for (Map.Entry<String, JsonElement> entry : JsonAnimationUtils.getAnimations(jsonRepresentation)) {
			String animationName = entry.getKey();
			Animation animation;
			try {
				animation = JsonAnimationUtils.deserializeJsonToAnimation(
						JsonAnimationUtils.getAnimation(jsonRepresentation, animationName), parser);
				animationFile.putAnimation(animationName, animation);
			} catch (ShaderParseException e) {
				GeckoLib.LOGGER.error("Could not load animation: {}", animationName, e);
				throw new RuntimeException(e);
			}
		}
		return animationFile;
	}

	/**
	 * Internal method for handling reloads of animation files. Do not override.
	 */
	private JsonObject loadFile(Identifier location, ResourceManager manager) {
		String content = getResourceAsString(location, manager);
		Gson GSON = new Gson();
		return JsonHelper.deserialize(GSON, content, JsonObject.class);
	}

	public static String getResourceAsString(Identifier location, ResourceManager manager) {
		try (InputStream inputStream = manager.getResource(location).getInputStream()) {
			return IOUtils.toString(inputStream, Charset.defaultCharset());
		} catch (Exception e) {
			String message = "Couldn't load " + location;
			GeckoLib.LOGGER.error(message, e);
			throw new RuntimeException(new FileNotFoundException(location.toString()));
		}
	}

}
