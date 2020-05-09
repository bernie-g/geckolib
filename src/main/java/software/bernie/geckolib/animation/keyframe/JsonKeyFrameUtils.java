package software.bernie.geckolib.animation.keyframe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonKeyFrameUtils
{
	public static VectorKeyFrameList<KeyFrame<Float>> convertJsonToKeyFrames(List<Map.Entry<String, JsonElement>> element, boolean isRotation) throws NumberFormatException
	{
		Float previousXValue = null;
		Float previousYValue = null;
		Float previousZValue = null;

		List<KeyFrame<Float>> xKeyFrames = new ArrayList();
		List<KeyFrame<Float>> yKeyFrames = new ArrayList();
		List<KeyFrame<Float>> zKeyFrames = new ArrayList();

		for(int i = 0; i < element.size(); i++)
		{
			Map.Entry<String, JsonElement> keyframe = element.get(i);
			Map.Entry<String, JsonElement> previousKeyFrame = i == 0 ? null : element.get(i - 1);

			float previousKeyFrameLocation = previousKeyFrame == null ? 0 : Float.parseFloat(previousKeyFrame.getKey());
			float currentKeyFrameLocation = Float.parseFloat(keyframe.getKey());
			float animationTimeDifference = currentKeyFrameLocation - previousKeyFrameLocation;

			JsonArray vectorJsonArray = keyframe.getValue().getAsJsonArray();
			float float1 = vectorJsonArray.get(0).getAsFloat();
			float float2 = vectorJsonArray.get(1).getAsFloat();
			float float3 = vectorJsonArray.get(2).getAsFloat();

			float currentXValue = isRotation ? (float) Math.toRadians(float1) : float1;
			float currentYValue = isRotation ? (float) Math.toRadians(float2) : float2;
			float currentZValue = isRotation ? (float) Math.toRadians(float3) : float3;

			KeyFrame<Float> xKeyFrame = new KeyFrame(animationTimeDifference, i == 0 ? currentXValue : previousXValue, currentXValue);
			KeyFrame<Float> yKeyFrame = new KeyFrame(animationTimeDifference, i == 0 ? currentYValue : previousYValue, currentYValue);
			KeyFrame<Float> zKeyFrame = new KeyFrame(animationTimeDifference, i == 0 ? currentZValue : previousZValue, currentZValue);

			previousXValue = currentXValue;
			previousYValue = currentYValue;
			previousZValue = currentZValue;


			xKeyFrames.add(xKeyFrame);
			yKeyFrames.add(yKeyFrame);
			zKeyFrames.add(zKeyFrame);
		}

		return new VectorKeyFrameList<>(xKeyFrames, yKeyFrames, zKeyFrames);
	}
	public static VectorKeyFrameList<KeyFrame<Float>> convertJsonToKeyFrames(List<Map.Entry<String, JsonElement>> element) throws NumberFormatException
	{
		return convertJsonToKeyFrames(element, false);
	}


	public static VectorKeyFrameList<KeyFrame<Float>> convertJsonToRotationKeyFrames(List<Map.Entry<String, JsonElement>> element) throws NumberFormatException
	{
		VectorKeyFrameList<KeyFrame<Float>> frameList = convertJsonToKeyFrames(element, true);
		return new VectorKeyFrameList(frameList.xKeyFrames, frameList.yKeyFrames,
				frameList.zKeyFrames);
	}
	public static VectorKeyFrameList<KeyFrame<Float>> convertJsonToScaleKeyFrames(List<Map.Entry<String, JsonElement>> element) throws NumberFormatException
	{
		VectorKeyFrameList<KeyFrame<Float>> frameList = convertJsonToKeyFrames(element);
		return new VectorKeyFrameList(frameList.xKeyFrames, frameList.yKeyFrames,
				frameList.zKeyFrames);
	}
	public static VectorKeyFrameList<KeyFrame<Float>> convertJsonToPositionKeyFrames(List<Map.Entry<String, JsonElement>> element) throws NumberFormatException
	{
		VectorKeyFrameList<KeyFrame<Float>> frameList = convertJsonToKeyFrames(element);
		return new VectorKeyFrameList(frameList.xKeyFrames, frameList.yKeyFrames,
				frameList.zKeyFrames);
	}

}
