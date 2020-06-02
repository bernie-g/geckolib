/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import software.bernie.geckolib.animation.AnimationUtils;
import software.bernie.geckolib.animation.keyframe.KeyFrame;
import software.bernie.geckolib.animation.keyframe.VectorKeyFrameList;
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

		for(int i = 0; i < element.size(); i++)
		{
			Map.Entry<String, JsonElement> keyframe = element.get(i);
			Map.Entry<String, JsonElement> previousKeyFrame = i == 0 ? null : element.get(i - 1);

			double previousKeyFrameLocation = previousKeyFrame == null ? 0 : Double.parseDouble(previousKeyFrame.getKey());
			double currentKeyFrameLocation = Double.parseDouble(keyframe.getKey());
			double animationTimeDifference = currentKeyFrameLocation - previousKeyFrameLocation;

			JsonArray vectorJsonArray = keyframe.getValue().getAsJsonArray();
			float double1 = vectorJsonArray.get(0).getAsFloat();
			float double2 = vectorJsonArray.get(1).getAsFloat();
			float double3 = vectorJsonArray.get(2).getAsFloat();

			double currentXValue = isRotation ? (float) Math.toRadians(double1) : double1;
			double currentYValue = isRotation ? (float) Math.toRadians(double2) : double2;
			double currentZValue = isRotation ? (float) Math.toRadians(double3) : double3;

			KeyFrame<Double> xKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference), i == 0 ? currentXValue : previousXValue, currentXValue);
			KeyFrame<Double> yKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference), i == 0 ? currentYValue : previousYValue, currentYValue);
			KeyFrame<Double> zKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference), i == 0 ? currentZValue : previousZValue, currentZValue);

			previousXValue = currentXValue;
			previousYValue = currentYValue;
			previousZValue = currentZValue;

			xKeyFrames.add(xKeyFrame);
			yKeyFrames.add(yKeyFrame);
			zKeyFrames.add(zKeyFrame);
		}

		return new VectorKeyFrameList<>(xKeyFrames, yKeyFrames, zKeyFrames);
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
