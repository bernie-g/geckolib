package software.bernie.geckolib.loading.json.typeadapter;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.keyframe.event.data.CustomInstructionKeyframeData;
import software.bernie.geckolib.core.keyframe.event.data.KeyFrameData;
import software.bernie.geckolib.core.keyframe.event.data.ParticleKeyframeData;
import software.bernie.geckolib.core.keyframe.event.data.SoundKeyframeData;
import software.bernie.geckolib.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

/**
 * {@link Gson} {@link JsonDeserializer} for {@link Animation.Keyframes}.<br>
 * Acts as the deserialization interface for {@code Keyframes}
 */
public class KeyFramesAdapter implements JsonDeserializer<Animation.Keyframes> {
	@Override
	public Animation.Keyframes deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		SoundKeyframeData[] sounds = buildSoundFrameData(obj);
		ParticleKeyframeData[] particles = buildParticleFrameData(obj);
		CustomInstructionKeyframeData[] customInstructions = buildCustomFrameData(obj);

		return new Animation.Keyframes(sounds, particles, customInstructions);
	}

	private static SoundKeyframeData[] buildSoundFrameData(JsonObject rootObj) {
		return extractKeyframes(GsonHelper.getAsJsonObject(rootObj, "sound_effects", new JsonObject()), SoundKeyframeData[]::new, (timestamp, obj) -> {
			String effect = GsonHelper.getAsString(obj, "effect", "");

			return effect.isEmpty() ? null : new SoundKeyframeData(Double.parseDouble(timestamp) * 20d, effect);
		});
	}

	private static ParticleKeyframeData[] buildParticleFrameData(JsonObject rootObj) {
		return extractKeyframes(GsonHelper.getAsJsonObject(rootObj, "particle_effects", new JsonObject()), ParticleKeyframeData[]::new, (timestamp, obj) -> {
			String effect = GsonHelper.getAsString(obj, "effect", "");
			String locator = GsonHelper.getAsString(obj, "locator", "");
			String script = GsonHelper.getAsString(obj, "pre_effect_script", "");

			return new ParticleKeyframeData(Double.parseDouble(timestamp) * 20d, effect, locator, script);
		});
	}

	private static CustomInstructionKeyframeData[] buildCustomFrameData(JsonObject rootObj) {
		JsonObject customInstructionsObj = GsonHelper.getAsJsonObject(rootObj, "timeline", new JsonObject());
		CustomInstructionKeyframeData[] customInstructions = new CustomInstructionKeyframeData[customInstructionsObj.size()];
		int index = 0;

		for (Map.Entry<String, JsonElement> entry : customInstructionsObj.entrySet()) {
			String instructions = "";

			if (entry.getValue() instanceof JsonArray array) {
				instructions = JsonUtil.GEO_GSON.fromJson(array, ObjectArrayList.class).toString();
			}
			else if (entry.getValue() instanceof JsonPrimitive primitive) {
				instructions = primitive.getAsString();
			}

			customInstructions[index] = new CustomInstructionKeyframeData(Double.parseDouble(entry.getKey()) * 20d, instructions);
			index++;
		}

		return customInstructions;
	}

	private static <T extends KeyFrameData> T[] extractKeyframes(JsonObject section, IntFunction<T[]> arrayFactory, BiFunction<String, JsonObject, @Nullable T> bakery) {
		final List<T> keyframes = new ArrayList<>(section.size());

		for (Map.Entry<String, JsonElement> entry : section.entrySet()) {
			if (entry.getValue() instanceof JsonArray array) {
				for (JsonElement element : array) {
					if (element instanceof JsonObject object) {
						T keyframe = bakery.apply(entry.getKey(), object);

						if (keyframe != null)
							keyframes.add(keyframe);
					}
				}
			}
			else if (entry.getValue() instanceof JsonObject object) {
				T keyframe = bakery.apply(entry.getKey(), object);

				if (keyframe != null)
					keyframes.add(keyframe);
			}
		}

		return keyframes.toArray(arrayFactory);
	}
}
