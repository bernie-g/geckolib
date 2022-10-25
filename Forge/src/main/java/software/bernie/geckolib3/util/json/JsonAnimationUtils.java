/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.util.json;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eliotlash.mclib.math.IValue;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.ChainedJsonException;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.EventKeyFrame;
import software.bernie.geckolib3.core.keyframe.ParticleEventKeyFrame;
import software.bernie.geckolib3.core.keyframe.VectorKeyFrameList;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.util.AnimationUtils;

/**
 * Helper for parsing the bedrock json animation format and finding certain
 * elements
 */
public class JsonAnimationUtils {
	private static Gson GSON = null;

	/**
	 * Gets the "animations" object as a set of maps consisting of the name of the
	 * animation and the inner json of the animation.
	 *
	 * @param json The root json object
	 * @return The set of map entries where the string is the name of the animation
	 *         and the JsonElement is the actual animation
	 */
	public static Set<Map.Entry<String, JsonElement>> getAnimations(JsonObject json) {
		return json.getAsJsonObject("animations").entrySet();
	}

	/**
	 * Gets the "bones" object from an animation json object.
	 *
	 * @param json The animation json
	 * @return The set of map entries where the string is the name of the group name
	 *         in blockbench and the JsonElement is the object, which has all the
	 *         position/rotation/scale keyframes
	 */
	public static List<Map.Entry<String, JsonElement>> getBones(JsonObject json) {
		JsonObject bones = json.getAsJsonObject("bones");
		return bones == null ? List.of() : new ArrayList<>(bones.entrySet());
	}

	/**
	 * Gets rotation key frames.
	 *
	 * @param json The "bones" json object
	 * @return The set of map entries where the string is the keyframe time (not
	 *         sure why the format stores the times as a string) and the JsonElement
	 *         is the object, which has all the rotation keyframes.
	 */
	public static Set<Map.Entry<String, JsonElement>> getRotationKeyFrames(JsonObject json) {
		JsonElement rotationObject = json.get("rotation");
		if (rotationObject.isJsonArray()) {
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0", rotationObject.getAsJsonArray()));
		}
		if (rotationObject.isJsonPrimitive()) {
			JsonPrimitive primitive = rotationObject.getAsJsonPrimitive();
			JsonElement jsonElement = getGson().toJsonTree(Arrays.asList(primitive, primitive, primitive));
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0", jsonElement));
		}
		return rotationObject.getAsJsonObject().entrySet();
	}

	/**
	 * Gets position key frames.
	 *
	 * @param json The "bones" json object
	 * @return The set of map entries where the string is the keyframe time (not
	 *         sure why the format stores the times as a string) and the JsonElement
	 *         is the object, which has all the position keyframes.
	 */
	public static Set<Map.Entry<String, JsonElement>> getPositionKeyFrames(JsonObject json) {
		JsonElement positionObject = json.get("position");
		if (positionObject.isJsonArray()) {
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0", positionObject.getAsJsonArray()));
		}
		if (positionObject.isJsonPrimitive()) {
			JsonPrimitive primitive = positionObject.getAsJsonPrimitive();
			JsonElement jsonElement = getGson().toJsonTree(Arrays.asList(primitive, primitive, primitive));
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0", jsonElement));
		}
		return positionObject.getAsJsonObject().entrySet();
	}

	/**
	 * Gets scale key frames.
	 *
	 * @param json The "bones" json object
	 * @return The set of map entries where the string is the keyframe time (not
	 *         sure why the format stores the times as a string) and the JsonElement
	 *         is the object, which has all the scale keyframes.
	 */
	public static Set<Map.Entry<String, JsonElement>> getScaleKeyFrames(JsonObject json) {
		JsonElement scaleObject = json.get("scale");
		if (scaleObject.isJsonArray()) {
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0", scaleObject.getAsJsonArray()));
		}
		if (scaleObject.isJsonPrimitive()) {
			JsonPrimitive primitive = scaleObject.getAsJsonPrimitive();
			JsonElement jsonElement = getGson().toJsonTree(Arrays.asList(primitive, primitive, primitive));
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0", jsonElement));
		}
		return getObjectListAsArray(scaleObject.getAsJsonObject());
	}

	/**
	 * Gets sound effect frames.
	 *
	 * @param json The animation json
	 * @return The set of map entries where the string is the keyframe time (not
	 *         sure why the format stores the times as a string) and the JsonElement
	 *         is the object, which has all the sound effect keyframes.
	 */
	public static List<Map.Entry<String, JsonElement>> getSoundEffectFrames(JsonObject json) {
		JsonObject soundEffects = json.getAsJsonObject("sound_effects");
		return soundEffects == null ? List.of() : new ObjectArrayList<>(soundEffects.entrySet());
	}

	/**
	 * Gets particle effect frames.
	 *
	 * @param json The animation json
	 * @return The set of map entries where the string is the keyframe time (not
	 *         sure why the format stores the times as a string) and the JsonElement
	 *         is the object, which has all the particle effect keyframes.
	 */
	public static List<Map.Entry<String, JsonElement>> getParticleEffectFrames(JsonObject json) {
		JsonObject particleEffects = json.getAsJsonObject("particle_effects");
		return particleEffects == null ? List.of() : new ObjectArrayList<>(particleEffects.entrySet());
	}

	/**
	 * Gets custom instruction key frames.
	 *
	 * @param json The animation json
	 * @return The set of map entries where the string is the keyframe time (not
	 *         sure why the format stores the times as a string) and the JsonElement
	 *         is the object, which has all the custom instruction keyframes.
	 */
	public static List<Map.Entry<String, JsonElement>> getCustomInstructionKeyFrames(JsonObject json) {
		JsonObject customInstructions = json.getAsJsonObject("timeline");
		return customInstructions == null ? List.of() : new ArrayList<>(customInstructions.entrySet());
	}

	private static JsonElement getObjectByKey(Set<Map.Entry<String, JsonElement>> json, String key)
			throws ChainedJsonException {
		for (Map.Entry<String, JsonElement> entry : json) {
			if (entry.getKey().equals(key))
				return entry.getValue();
		}

		throw new ChainedJsonException("Could not find key: " + key);
	}

	/**
	 * Gets an animation by name.
	 *
	 * @param animationFile the animation file
	 * @param animationName the animation name
	 * @return the animation
	 * @throws ChainedJsonException the json exception
	 */
	public static Map.Entry<String, JsonElement> getAnimation(JsonObject animationFile, String animationName)
			throws ChainedJsonException {
		return new AbstractMap.SimpleEntry(animationName, getObjectByKey(getAnimations(animationFile), animationName));
	}

	/**
	 * The animation format bedrock/blockbench uses is bizarre, and exports arrays
	 * of objects as plain parameters in a parent object, so this method undoes it
	 *
	 * @param json The json to convert (pass in the parent object or the list of
	 *             objects)
	 * @return The set of map entries where the string is the object key and the
	 *         JsonElement is the actual object
	 */
	public static Set<Map.Entry<String, JsonElement>> getObjectListAsArray(JsonObject json) {
		return json.entrySet();
	}

	/**
	 * This is the central method that parses an animation and converts it to an
	 * Animation object with all the correct keyframe times and extra metadata.
	 *
	 * @param element The animation json
	 * @param parser
	 * @return The newly constructed Animation object
	 * @throws ClassCastException    Throws this exception if the JSON is formatted
	 *                               incorrectly
	 * @throws IllegalStateException Throws this exception if the JSON is formatted
	 *                               incorrectly
	 */
	public static Animation deserializeJsonToAnimation(Map.Entry<String, JsonElement> element, MolangParser parser)
			throws ClassCastException, IllegalStateException {
		Animation animation = new Animation();
		JsonObject animationJsonObject = element.getValue().getAsJsonObject();

		// Set some metadata about the animation
		animation.animationName = element.getKey();
		JsonElement animationLength = animationJsonObject.get("animation_length");
		animation.animationLength = animationLength == null ? -1
				: AnimationUtils.convertSecondsToTicks(animationLength.getAsDouble());
		animation.boneAnimations = new ObjectArrayList<>();
		animation.loop = ILoopType.fromJson(animationJsonObject.get("loop"));

		// Handle parsing sound effect keyframes
		for (Map.Entry<String, JsonElement> keyFrame : getSoundEffectFrames(animationJsonObject)) {
			animation.soundKeyFrames.add(new EventKeyFrame<>(Double.parseDouble(keyFrame.getKey()) * 20,
					keyFrame.getValue().getAsJsonObject().get("effect").getAsString()));
		}

		// Handle parsing particle effect keyframes
		for (Map.Entry<String, JsonElement> keyFrame : getParticleEffectFrames(animationJsonObject)) {
			JsonObject object = keyFrame.getValue().getAsJsonObject();
			JsonElement effect = object.get("effect");
			JsonElement locator = object.get("locator");
			JsonElement pre_effect_script = object.get("pre_effect_script");
			animation.particleKeyFrames.add(new ParticleEventKeyFrame(Double.parseDouble(keyFrame.getKey()) * 20,
					effect == null ? "" : effect.getAsString(), locator == null ? "" : locator.getAsString(),
					pre_effect_script == null ? "" : pre_effect_script.getAsString()));
		}

		// Handle parsing custom instruction keyframes
		for (Map.Entry<String, JsonElement> keyFrame : getCustomInstructionKeyFrames(animationJsonObject)) {
			animation.customInstructionKeyframes.add(new EventKeyFrame(Double.parseDouble(keyFrame.getKey()) * 20,
					keyFrame.getValue() instanceof JsonArray
							? convertJsonArrayToList(keyFrame.getValue().getAsJsonArray()).toString()
							: keyFrame.getValue().getAsString()));
		}

		// The list of all bones being used in this animation, where String is the name
		// of the bone/group, and the JsonElement is the
		for (Map.Entry<String, JsonElement> bone : getBones(animationJsonObject)) {
			BoneAnimation boneAnimation = new BoneAnimation(bone.getKey());
			JsonObject boneJsonObj = bone.getValue().getAsJsonObject();
			try {
				boneAnimation.scaleKeyFrames = JsonKeyFrameUtils
						.convertJsonToKeyFrames(new ObjectArrayList<>(getScaleKeyFrames(boneJsonObj)), parser);
			} catch (Exception e) {
				// No scale key frames found
				boneAnimation.scaleKeyFrames = new VectorKeyFrameList<>();
			}

			try {
				boneAnimation.positionKeyFrames = JsonKeyFrameUtils
						.convertJsonToKeyFrames(new ObjectArrayList<>(getPositionKeyFrames(boneJsonObj)), parser);
			} catch (Exception e) {
				// No position key frames found
				boneAnimation.positionKeyFrames = new VectorKeyFrameList<>();
			}

			try {
				boneAnimation.rotationKeyFrames = JsonKeyFrameUtils
						.convertJsonToRotationKeyFrames(new ObjectArrayList<>(getRotationKeyFrames(boneJsonObj)), parser);
			} catch (Exception e) {
				// No rotation key frames found
				boneAnimation.rotationKeyFrames = new VectorKeyFrameList<>();
			}

			animation.boneAnimations.add(boneAnimation);
		}
		if (animation.animationLength == -1) {
			animation.animationLength = calculateLength(animation.boneAnimations);
		}

		return animation;
	}

	private static double calculateLength(List<BoneAnimation> boneAnimations) {
		double longestLength = 0;
		for (BoneAnimation animation : boneAnimations) {
			double xKeyframeTime = animation.rotationKeyFrames.getLastKeyframeTime();
			double yKeyframeTime = animation.positionKeyFrames.getLastKeyframeTime();
			double zKeyframeTime = animation.scaleKeyFrames.getLastKeyframeTime();
			longestLength = maxAll(longestLength, xKeyframeTime, yKeyframeTime, zKeyframeTime);
		}
		return longestLength == 0 ? Double.MAX_VALUE : longestLength;
	}

	static List<IValue> convertJsonArrayToList(JsonArray array) {
		return getGson().fromJson(array, ArrayList.class);
	}

	private static Gson getGson() {
		if (GSON == null)
			GSON = new Gson();

		return GSON;
	}

	public static double maxAll(double... values) {
		double max = 0;
		for (double value : values) {
			max = Math.max(value, max);
		}
		return max;
	}
}
