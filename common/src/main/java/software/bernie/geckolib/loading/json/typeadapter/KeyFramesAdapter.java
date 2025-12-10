package software.bernie.geckolib.loading.json.typeadapter;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.cache.animation.keyframeevent.CustomInstructionKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.SoundKeyframeData;
import software.bernie.geckolib.loading.json.raw.*;
import software.bernie.geckolib.loading.object.BakedAnimations;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * {@link Gson} {@link JsonDeserializer} for {@link Animation.KeyframeMarkers}
 * <p>
 * Acts as the deserialization interface for {@code Keyframes}
 */
public class KeyFramesAdapter implements JsonDeserializer<Animation.KeyframeMarkers> {
	public static final Gson GEO_GSON = new GsonBuilder().setStrictness(Strictness.LENIENT)
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
			.registerTypeAdapter(Animation.KeyframeMarkers.class, new KeyFramesAdapter())
			.registerTypeAdapter(BakedAnimations.class, new BakedAnimationsAdapter())
			.create();

	@Override
	public Animation.KeyframeMarkers deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
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
				instructions = GEO_GSON.fromJson(array, ObjectArrayList.class).toString();
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
