package software.bernie.geckolib.loading.json.typeadapter;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.keyframe.event.data.CustomInstructionKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.KeyFrameData;
import software.bernie.geckolib.animation.keyframe.event.data.ParticleKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.SoundKeyframeData;
import software.bernie.geckolib.loading.json.raw.*;
import software.bernie.geckolib.loading.object.BakedAnimations;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

/**
 * {@link Gson} {@link JsonDeserializer} for {@link Animation.Keyframes}
 * <p>
 * Acts as the deserialization interface for {@code Keyframes}
 */
public class KeyFramesAdapter implements JsonDeserializer<Animation.Keyframes> {
	public static final Gson GEO_GSON = new GsonBuilder().setLenient()
			.registerTypeAdapter(Bone.class, Bone.deserializer())
			.registerTypeAdapter(Cube.class, Cube.deserializer())
			.registerTypeAdapter(FaceUV.class, FaceUV.deserializer())
			.registerTypeAdapter(LocatorClass.class, LocatorClass.deserializer())
			.registerTypeAdapter(LocatorValue.class, LocatorValue.deserializer())
			.registerTypeAdapter(MinecraftGeometry.class, MinecraftGeometry.deserializer())
			.registerTypeAdapter(Model.class, Model.deserializer())
			.registerTypeAdapter(ModelProperties.class, ModelProperties.deserializer())
			.registerTypeAdapter(PolyMesh.class, PolyMesh.deserializer())
			.registerTypeAdapter(PolysUnion.class, PolysUnion.deserializer())
			.registerTypeAdapter(TextureMesh.class, TextureMesh.deserializer())
			.registerTypeAdapter(UVFaces.class, UVFaces.deserializer())
			.registerTypeAdapter(UVUnion.class, UVUnion.deserializer())
			.registerTypeAdapter(Animation.Keyframes.class, new KeyFramesAdapter())
			.registerTypeAdapter(BakedAnimations.class, new BakedAnimationsAdapter())
			.create();

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
			final String effect = GsonHelper.getAsString(obj, "effect", "");

			return effect.isEmpty() ? null : new SoundKeyframeData(Double.parseDouble(timestamp) * 20d, effect);
		});
	}

	private static ParticleKeyframeData[] buildParticleFrameData(JsonObject rootObj) {
		return extractKeyframes(GsonHelper.getAsJsonObject(rootObj, "particle_effects", new JsonObject()), ParticleKeyframeData[]::new, (timestamp, obj) -> {
			final String effect = GsonHelper.getAsString(obj, "effect", "");
			final String locator = GsonHelper.getAsString(obj, "locator", "");
			final String script = GsonHelper.getAsString(obj, "pre_effect_script", "");

			return new ParticleKeyframeData(Double.parseDouble(timestamp) * 20d, effect, locator, script);
		});
	}

	private static CustomInstructionKeyframeData[] buildCustomFrameData(JsonObject rootObj) {
		final JsonObject customInstructionsObj = GsonHelper.getAsJsonObject(rootObj, "timeline", new JsonObject());
		final CustomInstructionKeyframeData[] customInstructions = new CustomInstructionKeyframeData[customInstructionsObj.size()];
		int index = 0;

		for (Map.Entry<String, JsonElement> entry : customInstructionsObj.entrySet()) {
			String instructions = "";

			if (entry.getValue() instanceof JsonArray array) {
				instructions = GEO_GSON.fromJson(array, ObjectArrayList.class).toString();
			}
			else if (entry.getValue() instanceof JsonPrimitive primitive) {
				instructions = primitive.getAsString();
			}

			customInstructions[index] = new CustomInstructionKeyframeData(Double.parseDouble(entry.getKey()) * 20d, instructions);
			index++;
		}

		return customInstructions;
	}

	private static <T extends KeyFrameData> T[] extractKeyframes(JsonObject section, IntFunction<T[]> arrayFactory, BiFunction<String, JsonObject, @Nullable T> keyframeFactory) {
		final List<T> keyframes = new ArrayList<>(section.size());

		for (Map.Entry<String, JsonElement> entry : section.entrySet()) {
			if (entry.getValue() instanceof JsonArray array) {
				for (JsonElement element : array) {
					if (element instanceof JsonObject object) {
						final T keyframe = keyframeFactory.apply(entry.getKey(), object);

						if (keyframe != null)
							keyframes.add(keyframe);
					}
				}
			}
			else if (entry.getValue() instanceof JsonObject object) {
				final T keyframe = keyframeFactory.apply(entry.getKey(), object);

				if (keyframe != null)
					keyframes.add(keyframe);
			}
		}

		return keyframes.toArray(arrayFactory);
	}
}
