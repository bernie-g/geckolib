package software.bernie.geckolib.loading.json.typeadapter;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.cache.animation.keyframeevent.CustomInstructionKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.SoundKeyframeData;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/// [Gson] [JsonDeserializer] for [Animation.KeyframeMarkers]
@ApiStatus.Internal
public final class KeyFrameMarkersAdapter {
	/// Create a GSON [JsonDeserializer] for [Animation.KeyframeMarkers]
	public static JsonDeserializer<Animation.KeyframeMarkers> deserializer() throws JsonParseException {
		return KeyFrameMarkersAdapter::fromJson;
	}

	/// Deserialize a [Animation.KeyframeMarkers] from a [JsonElement].
	private static Animation.KeyframeMarkers fromJson(JsonElement json, Type type, JsonDeserializationContext context) {
		JsonObject obj = json.getAsJsonObject();
		SoundKeyframeData[] sounds = buildSoundFrameData(obj);
		ParticleKeyframeData[] particles = buildParticleFrameData(obj);
		CustomInstructionKeyframeData[] customInstructions = buildCustomFrameData(obj);

		return new Animation.KeyframeMarkers(sounds, particles, customInstructions);
	}

	private static SoundKeyframeData[] buildSoundFrameData(JsonObject rootObj) {
		JsonObject soundsObj = GsonHelper.getAsJsonObject(rootObj, "sound_effects", new JsonObject());
        List<SoundKeyframeData> sounds = new ObjectArrayList<>(soundsObj.size());

		for (Map.Entry<String, JsonElement> entry : soundsObj.entrySet()) {
			sounds.add(new SoundKeyframeData(Double.parseDouble(entry.getKey()), GsonHelper.getAsString(entry.getValue().getAsJsonObject(), "effect")));
		}

        sounds.sort(Comparator.comparing(SoundKeyframeData::getTime));

		return sounds.toArray(new SoundKeyframeData[0]);
	}

	private static ParticleKeyframeData[] buildParticleFrameData(JsonObject rootObj) {
		JsonObject particlesObj = GsonHelper.getAsJsonObject(rootObj, "particle_effects", new JsonObject());
        List<ParticleKeyframeData> particles = new ObjectArrayList<>(particlesObj.size());

		for (Map.Entry<String, JsonElement> entry : particlesObj.entrySet()) {
			JsonObject obj = entry.getValue().getAsJsonObject();
			String effect = GsonHelper.getAsString(obj, "effect", "");
			String locator = GsonHelper.getAsString(obj, "locator", "");
			String script = GsonHelper.getAsString(obj, "pre_effect_script", "");

			particles.add(new ParticleKeyframeData(Double.parseDouble(entry.getKey()), effect, locator, script));
		}

        particles.sort(Comparator.comparing(ParticleKeyframeData::getTime));

        return particles.toArray(new ParticleKeyframeData[0]);
	}

	private static CustomInstructionKeyframeData[] buildCustomFrameData(JsonObject rootObj) {
		JsonObject customInstructionsObj = GsonHelper.getAsJsonObject(rootObj, "timeline", new JsonObject());
		List<CustomInstructionKeyframeData> customInstructions = new ObjectArrayList<>(customInstructionsObj.size());

		for (Map.Entry<String, JsonElement> entry : customInstructionsObj.entrySet()) {
			String instructions;

			if (entry.getValue() instanceof JsonArray array) {
				instructions = GeckoLibResources.GSON.fromJson(array, ObjectArrayList.class).toString();
			}
			else if (entry.getValue() instanceof JsonPrimitive primitive) {
				instructions = primitive.getAsString();
			}
            else {
                instructions = "";
            }

			customInstructions.add(new CustomInstructionKeyframeData(Double.parseDouble(entry.getKey()), instructions));
		}

        customInstructions.sort(Comparator.comparing(CustomInstructionKeyframeData::getTime));

		return customInstructions.toArray(new CustomInstructionKeyframeData[0]);
	}
}
