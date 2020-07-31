/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.keyframe.KeyFrame;
import software.bernie.geckolib.animation.keyframe.VectorKeyFrameList;
import software.bernie.geckolib.easing.EasingType;
import software.bernie.geckolib.util.AnimationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper class to convert json to keyframes
 */
public class JsonKeyFrameUtils
{
	private static VectorKeyFrameList<KeyFrame<Double>> convertJson(List<Map.Entry<String, JsonElement>> element, boolean isRotation) throws NumberFormatException
	{
		Double previousXValue = null;
		Double previousYValue = null;
		Double previousZValue = null;

		List<KeyFrame<Double>> xKeyFrames = new ArrayList();
		List<KeyFrame<Double>> yKeyFrames = new ArrayList();
		List<KeyFrame<Double>> zKeyFrames = new ArrayList();

		for (int i = 0; i < element.size(); i++)
		{
			Map.Entry<String, JsonElement> keyframe = element.get(i);
			Map.Entry<String, JsonElement> previousKeyFrame = i == 0 ? null : element.get(i - 1);

			double previousKeyFrameLocation = previousKeyFrame == null ? 0 : Double.parseDouble(
					previousKeyFrame.getKey());
			double currentKeyFrameLocation = Double.parseDouble(keyframe.getKey());
			double animationTimeDifference = currentKeyFrameLocation - previousKeyFrameLocation;

			JsonArray vectorJsonArray = getKeyFrameVector(keyframe.getValue());
			float double1 = vectorJsonArray.get(0).getAsFloat();
			float double2 = vectorJsonArray.get(1).getAsFloat();
			float double3 = vectorJsonArray.get(2).getAsFloat();

			double currentXValue = isRotation ? (float) Math.toRadians(double1) : double1;
			double currentYValue = isRotation ? (float) Math.toRadians(double2) : double2;
			double currentZValue = isRotation ? (float) Math.toRadians(double3) : double3;
			KeyFrame<Double> xKeyFrame;
			KeyFrame<Double> yKeyFrame;
			KeyFrame<Double> zKeyFrame;

			if (keyframe.getValue().isJsonObject() && hasEasingType(keyframe.getValue()))
			{
				EasingType easingType = getEasingType(keyframe.getValue());
				if (hasEasingArgs(keyframe.getValue()))
				{
					List<Double> easingArgs = getEasingArgs(keyframe.getValue());
					xKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentXValue : previousXValue, currentXValue, easingType, easingArgs);
					yKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentYValue : previousYValue, currentYValue, easingType, easingArgs);
					zKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentZValue : previousZValue, currentZValue, easingType, easingArgs);
				}
				else
				{
					xKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentXValue : previousXValue, currentXValue, easingType);
					yKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentYValue : previousYValue, currentYValue, easingType);
					zKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentZValue : previousZValue, currentZValue, easingType);

				}
			}
			else
			{
				xKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
						i == 0 ? currentXValue : previousXValue, currentXValue);
				yKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
						i == 0 ? currentYValue : previousYValue, currentYValue);
				zKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
						i == 0 ? currentZValue : previousZValue, currentZValue);
			}

			previousXValue = currentXValue;
			previousYValue = currentYValue;
			previousZValue = currentZValue;

			xKeyFrames.add(xKeyFrame);
			yKeyFrames.add(yKeyFrame);
			zKeyFrames.add(zKeyFrame);
		}

		return new VectorKeyFrameList<>(xKeyFrames, yKeyFrames, zKeyFrames);
	}

	private static JsonArray getKeyFrameVector(JsonElement element)
	{
		if (element.isJsonArray())
		{
			return element.getAsJsonArray();
		}
		else
		{
			return element.getAsJsonObject().get("vector").getAsJsonArray();
		}
	}


	private static boolean hasEasingType(JsonElement element)
	{
		return element.getAsJsonObject().has("easing");
	}

	private static boolean hasEasingArgs(JsonElement element)
	{
		return element.getAsJsonObject().has("easingArgs");
	}

	private static EasingType getEasingType(JsonElement element)
	{
		final String easingString = element.getAsJsonObject().get("easing").getAsString();
		try
		{
			final String uppercaseEasingString = Character.toUpperCase(easingString.charAt(0)) + easingString.substring(1);
			EasingType easing = EasingType.valueOf(uppercaseEasingString);
			return easing;
		}
		catch(Exception e)
		{
			GeckoLib.LOGGER.fatal("Unknown easing type: " + easingString);
			throw new RuntimeException(e);
		}
	}

	private static List<Double> getEasingArgs(JsonElement element)
	{
		JsonObject asJsonObject = element.getAsJsonObject();
		JsonElement easingArgs = asJsonObject.get("easingArgs");
		JsonArray asJsonArray = easingArgs.getAsJsonArray();
		return JsonAnimationUtils.convertJsonArrayToList(asJsonArray);
	}

	/**
	 * Convert json to a rotation key frame vector list. This method also converts degrees to radians.
	 *
	 * @param element The keyframe parent json element
	 * @return the vector key frame list
	 * @throws NumberFormatException The number format exception
	 */
	public static VectorKeyFrameList<KeyFrame<Double>> convertJsonToKeyFrames(List<Map.Entry<String, JsonElement>> element) throws NumberFormatException
	{
		return convertJson(element, false);
	}

	/**
	 * Convert json to normal json keyframes
	 *
	 * @param element The keyframe parent json element
	 * @return the vector key frame list
	 * @throws NumberFormatException
	 */
	public static VectorKeyFrameList<KeyFrame<Double>> convertJsonToRotationKeyFrames(List<Map.Entry<String, JsonElement>> element) throws NumberFormatException
	{
		VectorKeyFrameList<KeyFrame<Double>> frameList = convertJson(element, true);
		return new VectorKeyFrameList(frameList.xKeyFrames, frameList.yKeyFrames,
				frameList.zKeyFrames);
	}
}
