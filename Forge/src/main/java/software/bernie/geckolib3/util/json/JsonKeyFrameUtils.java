/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.util.json;

import com.eliotlash.mclib.math.IValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.math.NumberUtils;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.ConstantValue;
import software.bernie.geckolib3.core.animation.EasingType;
import software.bernie.geckolib3.core.keyframe.Keyframe;
import software.bernie.geckolib3.core.keyframe.KeyframeStack;
import software.bernie.geckolib3.core.molang.MolangException;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.util.AnimationUtils;

import java.util.List;
import java.util.Map;

/**
 * Helper class to convert json to keyframes
 */
public class JsonKeyFrameUtils {
	private static KeyframeStack<Keyframe<IValue>> convertJson(List<Map.Entry<String, JsonElement>> element,
															   boolean isRotation, MolangParser parser) throws NumberFormatException, MolangException {
		IValue previousXValue = null;
		IValue previousYValue = null;
		IValue previousZValue = null;

		List<Keyframe<IValue>> xKeyframes = new ObjectArrayList<>();
		List<Keyframe<IValue>> yKeyframes = new ObjectArrayList<>();
		List<Keyframe<IValue>> zKeyframes = new ObjectArrayList<>();

		for (int i = 0; i < element.size(); i++) {
			Map.Entry<String, JsonElement> keyframe = element.get(i);
			if(keyframe.getKey().equals("easing") || keyframe.getKey().equals("easingArgs")) continue;
			Map.Entry<String, JsonElement> previousKeyFrame = i == 0 ? null : element.get(i - 1);

			double previousKeyFrameLocation = previousKeyFrame == null ? 0
					: Double.parseDouble(previousKeyFrame.getKey());
			double currentKeyFrameLocation = NumberUtils.isCreatable(keyframe.getKey())
					? Double.parseDouble(keyframe.getKey())
					: 0;
			double animationTimeDifference = currentKeyFrameLocation - previousKeyFrameLocation;

			JsonArray vectorJsonArray = getKeyFrameVector(keyframe.getValue());
			IValue xValue = parseExpression(parser, vectorJsonArray.get(0));
			IValue yValue = parseExpression(parser, vectorJsonArray.get(1));
			IValue zValue = parseExpression(parser, vectorJsonArray.get(2));

			IValue currentXValue = isRotation && xValue instanceof ConstantValue
					? ConstantValue.fromDouble(Math.toRadians(-xValue.get()))
					: xValue;
			IValue currentYValue = isRotation && yValue instanceof ConstantValue
					? ConstantValue.fromDouble(Math.toRadians(-yValue.get()))
					: yValue;
			IValue currentZValue = isRotation && zValue instanceof ConstantValue
					? ConstantValue.fromDouble(Math.toRadians(zValue.get()))
					: zValue;
			Keyframe<IValue> xKeyframe;
			Keyframe<IValue> yKeyframe;
			Keyframe<IValue> zKeyframe;

			if (keyframe.getValue().isJsonObject() && hasEasingType(keyframe.getValue())) {
				EasingType easingType = getEasingType(keyframe.getValue());
				if (hasEasingArgs(keyframe.getValue())) {
					List<IValue> easingArgs = getEasingArgs(keyframe.getValue());
					xKeyframe = new Keyframe(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentXValue : previousXValue, currentXValue, easingType, easingArgs);
					yKeyframe = new Keyframe(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentYValue : previousYValue, currentYValue, easingType, easingArgs);
					zKeyframe = new Keyframe(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentZValue : previousZValue, currentZValue, easingType, easingArgs);
				} else {
					xKeyframe = new Keyframe(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentXValue : previousXValue, currentXValue, easingType);
					yKeyframe = new Keyframe(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentYValue : previousYValue, currentYValue, easingType);
					zKeyframe = new Keyframe(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
							i == 0 ? currentZValue : previousZValue, currentZValue, easingType);

				}
			} else {
				xKeyframe = new Keyframe(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
						i == 0 ? currentXValue : previousXValue, currentXValue);
				yKeyframe = new Keyframe(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
						i == 0 ? currentYValue : previousYValue, currentYValue);
				zKeyframe = new Keyframe(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
						i == 0 ? currentZValue : previousZValue, currentZValue);
			}

			previousXValue = currentXValue;
			previousYValue = currentYValue;
			previousZValue = currentZValue;

			xKeyframes.add(xKeyframe);
			yKeyframes.add(yKeyframe);
			zKeyframes.add(zKeyframe);
		}

		return new KeyframeStack(xKeyframes, yKeyframes, zKeyframes);
	}

	private static JsonArray getKeyFrameVector(JsonElement element) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		} else {
			return element.getAsJsonObject().get("vector").getAsJsonArray();
		}
	}

	private static boolean hasEasingType(JsonElement element) {
		return element.getAsJsonObject().has("easing");
	}

	private static boolean hasEasingArgs(JsonElement element) {
		return element.getAsJsonObject().has("easingArgs");
	}

	private static EasingType getEasingType(JsonElement element) {
		final String easingString = element.getAsJsonObject().get("easing").getAsString();
		try {
			final String uppercaseEasingString = Character.toUpperCase(easingString.charAt(0))
					+ easingString.substring(1);
			return EasingType.valueOf(uppercaseEasingString);
		} catch (Exception e) {
			GeckoLib.LOGGER.fatal("Unknown easing type: {}", easingString);
			throw new RuntimeException(e);
		}
	}

	private static List<IValue> getEasingArgs(JsonElement element) {
		JsonObject asJsonObject = element.getAsJsonObject();
		JsonElement easingArgs = asJsonObject.get("easingArgs");
		JsonArray asJsonArray = easingArgs.getAsJsonArray();
		return JsonDeserializer.convertJsonArrayToList(asJsonArray);
	}

	/**
	 * Convert json to a rotation key frame vector list. This method also converts
	 * degrees to radians.
	 *
	 * @param element The keyframe parent json element
	 * @param parser
	 * @return the vector key frame list
	 * @throws NumberFormatException The number format exception
	 */
	public static KeyframeStack<Keyframe<IValue>> convertJsonToKeyFrames(
			List<Map.Entry<String, JsonElement>> element, MolangParser parser)
			throws NumberFormatException, MolangException {
		return convertJson(element, false, parser);
	}

	/**
	 * Convert json to normal json keyframes
	 *
	 * @param element The keyframe parent json element
	 * @param parser
	 * @return the vector key frame list
	 * @throws NumberFormatException
	 */
	public static KeyframeStack<Keyframe<IValue>> convertJsonToRotationKeyFrames(
			List<Map.Entry<String, JsonElement>> element, MolangParser parser)
			throws NumberFormatException, MolangException {
		KeyframeStack<Keyframe<IValue>> frameList = convertJson(element, true, parser);

		return KeyframeStack.from(frameList);
	}

	public static IValue parseExpression(MolangParser parser, JsonElement element) throws MolangException {
		if (element.getAsJsonPrimitive().isString()) {
			return parser.parseJson(element);
		} else {
			return ConstantValue.fromDouble(element.getAsDouble());
		}
	}

}
