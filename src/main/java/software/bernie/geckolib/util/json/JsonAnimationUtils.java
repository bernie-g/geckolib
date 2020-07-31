/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.util.json;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.util.JsonException;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.keyframe.BoneAnimation;
import software.bernie.geckolib.animation.keyframe.EventKeyFrame;
import software.bernie.geckolib.animation.keyframe.ParticleEventKeyFrame;
import software.bernie.geckolib.animation.keyframe.VectorKeyFrameList;
import software.bernie.geckolib.util.AnimationUtils;

import java.util.*;

/**
 * Helper for parsing the bedrock json animation format and finding certain elements
 */
public class JsonAnimationUtils
{
	/**
	 * Gets the "animations" object as a set of maps consisting of the name of the animation and the inner json of the animation.
	 *
	 * @param json The root json object
	 * @return The set of map entries where the string is the name of the animation and the JsonElement is the actual animation
	 */
	public static Set<Map.Entry<String, JsonElement>> getAnimations(JsonObject json)
	{
		return getObjectListAsArray(json.getAsJsonObject("animations"));
	}

	/**
	 * Gets the "bones" object from an animation json object.
	 *
	 * @param json The animation json
	 * @return The set of map entries where the string is the name of the group name in blockbench and the JsonElement is the object, which has all the position/rotation/scale keyframes
	 */
	public static List<Map.Entry<String, JsonElement>> getBones(JsonObject json)
	{
		JsonObject bones = json.getAsJsonObject("bones");
		return bones == null ? new ArrayList<>() : new ArrayList<>(getObjectListAsArray(bones));
	}



	/**
	 * Gets rotation key frames.
	 *
	 * @param json The "bones" json object
	 * @return The set of map entries where the string is the keyframe time (not sure why the format stores the times as a string) and the JsonElement is the object, which has all the rotation keyframes.
	 */
	public static Set<Map.Entry<String, JsonElement>> getRotationKeyFrames(JsonObject json)
	{
		JsonElement rotationObject = json.get("rotation");
		if(rotationObject.isJsonArray())
		{
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0.001",
					rotationObject.getAsJsonArray()));
		}
		if(rotationObject.isJsonPrimitive())
		{
			double value = rotationObject.getAsDouble();
			Gson gson = new Gson();
			JsonElement jsonElement = gson.toJsonTree(Arrays.asList(value, value, value));
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0.001", jsonElement));
		}
		return getObjectListAsArray(rotationObject.getAsJsonObject());
	}

	/**
	 * Gets position key frames.
	 * @param json The "bones" json object
	 * @return The set of map entries where the string is the keyframe time (not sure why the format stores the times as a string) and the JsonElement is the object, which has all the position keyframes.
	 */
	public static Set<Map.Entry<String, JsonElement>> getPositionKeyFrames(JsonObject json)
	{
		JsonElement positionObject = json.get("position");
		if(positionObject.isJsonArray())
		{
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0.001",
					positionObject.getAsJsonArray()));
		}
		if(positionObject.isJsonPrimitive())
		{
			double value = positionObject.getAsDouble();
			Gson gson = new Gson();
			JsonElement jsonElement = gson.toJsonTree(Arrays.asList(value, value, value));
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0.001", jsonElement));
		}
		return getObjectListAsArray(positionObject.getAsJsonObject());
	}

	/**
	 * Gets scale key frames.
	 *
	 * @param json The "bones" json object
	 * @return The set of map entries where the string is the keyframe time (not sure why the format stores the times as a string) and the JsonElement is the object, which has all the scale keyframes.
	 */
	public static Set<Map.Entry<String, JsonElement>> getScaleKeyFrames(JsonObject json)
	{
		JsonElement scaleObject = json.get("scale");
		if(scaleObject.isJsonArray())
		{
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0.001",
					scaleObject.getAsJsonArray()));
		}
		if(scaleObject.isJsonPrimitive())
		{
			double value = scaleObject.getAsDouble();
			Gson gson = new Gson();
			JsonElement jsonElement = gson.toJsonTree(Arrays.asList(value, value, value));
			return ImmutableSet.of(new AbstractMap.SimpleEntry("0.001", jsonElement));
		}
		return getObjectListAsArray(scaleObject.getAsJsonObject());	}

	/**
	 * Gets sound effect frames.
	 *
	 * @param json The animation json
	 * @return The set of map entries where the string is the keyframe time (not sure why the format stores the times as a string) and the JsonElement is the object, which has all the sound effect keyframes.
	 */
	public static ArrayList<Map.Entry<String, JsonElement>> getSoundEffectFrames(JsonObject json)
	{
		JsonObject sound_effects = json.getAsJsonObject("sound_effects");
		return sound_effects == null ? new ArrayList<>() : new ArrayList<>(getObjectListAsArray(sound_effects));
	}

	/**
	 * Gets particle effect frames.
	 *
	 * @param json The animation json
	 * @return The set of map entries where the string is the keyframe time (not sure why the format stores the times as a string) and the JsonElement is the object, which has all the particle effect keyframes.
	 */
	public static ArrayList<Map.Entry<String, JsonElement>> getParticleEffectFrames(JsonObject json)
	{
		JsonObject particle_effects = json.getAsJsonObject("particle_effects");
		return particle_effects == null ? new ArrayList<>() : new ArrayList<>(getObjectListAsArray(particle_effects));
	}

	/**
	 * Gets custom instruction key frames.
	 *
	 * @param json The animation json
	 * @return The set of map entries where the string is the keyframe time (not sure why the format stores the times as a string) and the JsonElement is the object, which has all the custom instruction keyframes.
	 */
	public static  ArrayList<Map.Entry<String, JsonElement>> getCustomInstructionKeyFrames(JsonObject json)
	{
		JsonObject custom_instructions = json.getAsJsonObject("timeline");
		return custom_instructions == null ? new ArrayList<>() : new ArrayList<>(getObjectListAsArray(custom_instructions));
	}


	private static JsonElement getObjectByKey(Set<Map.Entry<String, JsonElement>> json, String key) throws JsonException
	{
		return json.stream().filter(x -> x.getKey().equals(key)).findFirst().orElseThrow(
				() -> new JsonException("Could not find key: " + key)).getValue();
	}


	/**
	 * Gets an animation by name.
	 *
	 * @param animationFile the animation file
	 * @param animationName the animation name
	 * @return the animation
	 * @throws JsonException the json exception
	 */
	public static Map.Entry<String, JsonElement> getAnimation(JsonObject animationFile, String animationName) throws JsonException
	{
		return new AbstractMap.SimpleEntry(animationName, getObjectByKey(getAnimations(animationFile), animationName));
	}


	/**
	 * The animation format bedrock/blockbench uses is bizarre, and exports arrays of objects as plain parameters in a parent object, so this method undos it
	 *
	 * @param json The json to convert (pass in the parent object or the list of objects)
	 * @return The set of map entries where the string is the object key and the JsonElement is the actual object
	 */
	public static Set<Map.Entry<String, JsonElement>> getObjectListAsArray(JsonObject json)
	{
		return json.entrySet();
	}


	/**
	 * This is the central method that parses an animation and converts it to an Animation object with all the correct keyframe times and extra metadata.
	 *
	 * @param element The animation json
	 * @return The newly constructed Animation object
	 * @throws ClassCastException    Throws this exception if the JSON is formatted incorrectly
	 * @throws IllegalStateException Throws this exception if the JSON is formatted incorrectly
	 */
	public static Animation deserializeJsonToAnimation(Map.Entry<String, JsonElement> element) throws ClassCastException, IllegalStateException
	{
		Animation animation = new Animation();
		JsonObject animationJsonObject = element.getValue().getAsJsonObject();

		// Set some metadata about the animation
 		animation.animationName = element.getKey();
		JsonElement animation_length = animationJsonObject.get("animation_length");
		animation.animationLength = AnimationUtils.convertSecondsToTicks(animation_length == null ? 0.01D : animation_length.getAsDouble());
		animation.boneAnimations = new ArrayList();
		JsonElement loop = animationJsonObject.get("loop");
		animation.loop = loop != null && loop.getAsBoolean();

		//Handle parsing sound effect keyframes
		ArrayList<Map.Entry<String, JsonElement>> soundEffectFrames = getSoundEffectFrames(animationJsonObject);
		if(soundEffectFrames != null)
		{
			for(Map.Entry<String, JsonElement> keyFrame : soundEffectFrames)
			{
				animation.soundKeyFrames.add(new EventKeyFrame(Double.parseDouble(keyFrame.getKey()) * 20, keyFrame.getValue().getAsJsonObject().get("effect").getAsString()));
			}
		}

		//Handle parsing particle effect keyframes
		ArrayList<Map.Entry<String, JsonElement>> particleKeyFrames = getParticleEffectFrames(animationJsonObject);
		if(particleKeyFrames != null)
		{
			for(Map.Entry<String, JsonElement> keyFrame : particleKeyFrames)
			{
				JsonObject object = keyFrame.getValue().getAsJsonObject();
				JsonElement effect = object.get("effect");
				JsonElement locator = object.get("locator");
				JsonElement pre_effect_script = object.get("pre_effect_script");
				animation.particleKeyFrames.add(new ParticleEventKeyFrame(Double.parseDouble(keyFrame.getKey()) * 20, effect == null ? "" : effect.getAsString(), locator == null ? "" : locator.getAsString(), pre_effect_script == null ? "" : pre_effect_script.getAsString()));
			}
		}

		//Handle parsing custom instruction keyframes
		ArrayList<Map.Entry<String, JsonElement>> customInstructionKeyFrames = getCustomInstructionKeyFrames(animationJsonObject);
		if(customInstructionKeyFrames != null)
		{
			for(Map.Entry<String, JsonElement> keyFrame : customInstructionKeyFrames)
			{
				animation.customInstructionKeyframes.add(new EventKeyFrame(Double.parseDouble(keyFrame.getKey()) * 20, convertJsonArrayToList(keyFrame.getValue().getAsJsonArray())));
			}
		}


		// The list of all bones being used in this animation, where String is the name of the bone/group, and the JsonElement is the
		List<Map.Entry<String, JsonElement>> bones = getBones(animationJsonObject);
		for (Map.Entry<String, JsonElement> bone : bones)
		{
			BoneAnimation boneAnimation = new BoneAnimation();
			boneAnimation.boneName = bone.getKey();

			JsonObject boneJsonObj = bone.getValue().getAsJsonObject();
			try
			{
				Set<Map.Entry<String, JsonElement>> scaleKeyFramesJson = getScaleKeyFrames(boneJsonObj);
				boneAnimation.scaleKeyFrames = JsonKeyFrameUtils.convertJsonToKeyFrames(
						new ArrayList<>(scaleKeyFramesJson));
			}
			catch(Exception e)
			{
				// No scale key frames found
				boneAnimation.scaleKeyFrames = new VectorKeyFrameList<>();
			}

			try
			{
				Set<Map.Entry<String, JsonElement>> positionKeyFramesJson = getPositionKeyFrames(boneJsonObj);
				boneAnimation.positionKeyFrames = JsonKeyFrameUtils.convertJsonToKeyFrames(
						new ArrayList<>(positionKeyFramesJson));
			}
			catch(Exception e)
			{
				// No position key frames found
				boneAnimation.positionKeyFrames = new VectorKeyFrameList<>();
			}

			try
			{
				Set<Map.Entry<String, JsonElement>> rotationKeyFramesJson = getRotationKeyFrames(boneJsonObj);
				boneAnimation.rotationKeyFrames = JsonKeyFrameUtils.convertJsonToRotationKeyFrames(
						new ArrayList<>(rotationKeyFramesJson));
			}
			catch(Exception e)
			{
				// No rotation key frames found
				boneAnimation.rotationKeyFrames = new VectorKeyFrameList<>();
			}

			animation.boneAnimations.add(boneAnimation);
		}
		return animation;
	}

	static List<Double> convertJsonArrayToList(JsonArray array)
	{
		return new Gson().fromJson(array, ArrayList.class);
	}
}
