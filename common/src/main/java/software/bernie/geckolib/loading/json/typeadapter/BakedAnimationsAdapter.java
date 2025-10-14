package software.bernie.geckolib.loading.json.typeadapter;

import com.google.gson.*;
import it.unimi.dsi.fastutil.doubles.DoubleObjectPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.math.NumberUtils;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.animation.keyframe.BoneAnimation;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.animation.keyframe.KeyframeStack;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.value.Constant;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.object.CompoundException;
import software.bernie.geckolib.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link Gson} {@link JsonDeserializer} for {@link BakedAnimations}.<br>
 * Acts as the deserialization interface for {@code BakedAnimations}
 */
public class BakedAnimationsAdapter implements JsonDeserializer<BakedAnimations> {
	public static ConcurrentMap<Double, Constant> COMPRESSION_CACHE = null;

	@Override
	public BakedAnimations deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws RuntimeException {
		JsonObject obj = json.getAsJsonObject();
		Map<String, Animation> animations = new Object2ObjectOpenHashMap<>(obj.size());

		for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
			try {
				animations.put(entry.getKey(), bakeAnimation(entry.getKey(), entry.getValue().getAsJsonObject(), context));
			}
			catch (Exception ex) {
				if (ex instanceof CompoundException compoundEx) {
					compoundEx.withMessage("Unable to parse animation: " + entry.getKey());
				}
				else {
                    GeckoLibConstants.LOGGER.error("Unable to parse animation: {}", entry.getKey());
				}

				ex.printStackTrace();
			}
		}

		return new BakedAnimations(animations);
	}

	private Animation bakeAnimation(String name, JsonObject animationObj, JsonDeserializationContext context) throws CompoundException {
		double length = animationObj.has("animation_length") ? GsonHelper.getAsDouble(animationObj, "animation_length") * 20d : -1;
		Animation.LoopType loopType = Animation.LoopType.fromJson(animationObj.get("loop"));
		BoneAnimation[] boneAnimations = bakeBoneAnimations(GsonHelper.getAsJsonObject(animationObj, "bones", new JsonObject()));
		Animation.KeyframeMarkers keyframes = context.deserialize(animationObj, Animation.KeyframeMarkers.class);

		if (length == -1)
			length = calculateAnimationLength(boneAnimations);

		return Animation.create(name, length, loopType, boneAnimations, keyframes);
	}

	private BoneAnimation[] bakeBoneAnimations(JsonObject bonesObj) throws CompoundException {
		BoneAnimation[] animations = new BoneAnimation[bonesObj.size()];
		int index = 0;

		for (Map.Entry<String, JsonElement> entry : bonesObj.entrySet()) {
			JsonObject entryObj = entry.getValue().getAsJsonObject();
			KeyframeStack<Keyframe<MathValue>> scaleFrames = buildKeyframeStack(getKeyframes(entryObj.get("scale")), false);
			KeyframeStack<Keyframe<MathValue>> positionFrames = buildKeyframeStack(getKeyframes(entryObj.get("position")), false);
			KeyframeStack<Keyframe<MathValue>> rotationFrames = buildKeyframeStack(getKeyframes(entryObj.get("rotation")), true);

			animations[index] = new BoneAnimation(entry.getKey(), rotationFrames, positionFrames, scaleFrames);
			index++;
		}

		return animations;
	}

	private static List<DoubleObjectPair<JsonElement>> getKeyframes(JsonElement element) {
		if (element == null)
			return List.of();

		if (element instanceof JsonPrimitive primitive) {
			JsonArray array = new JsonArray(3);

			array.add(primitive);
			array.add(primitive);
			array.add(primitive);

			element = array;
		}

		if (element instanceof JsonArray array)
			return ObjectArrayList.of(DoubleObjectPair.of(0, array));

		if (element instanceof JsonObject obj) {
			if (obj.has("vector"))
				return ObjectArrayList.of(DoubleObjectPair.of(0, obj));

			List<DoubleObjectPair<JsonElement>> list = new ObjectArrayList<>();

			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				double timestamp = readTimestamp(entry.getKey());

				if (timestamp == 0 && !list.isEmpty())
					throw new JsonParseException("Invalid keyframe data - multiple starting keyframes?" + entry.getKey());

				if (entry.getValue() instanceof JsonObject entryObj && !entryObj.has("vector")) {
					addBedrockKeyframes(timestamp, entryObj, list);

					continue;
				}

				list.add(DoubleObjectPair.of(timestamp, entry.getValue()));
			}

			return list;
		}

		throw new JsonParseException("Invalid object type provided to getTripletObj, got: " + element);
	}

    // Blockbench is just getting silly now
    private static JsonArray extractBedrockKeyframe(JsonElement keyframe) {
        if (keyframe.isJsonArray())
            return keyframe.getAsJsonArray();

        if (!keyframe.isJsonObject())
            throw new JsonParseException("Invalid keyframe data - expected array or object, found " + keyframe);

        JsonObject keyframeObj = keyframe.getAsJsonObject();

        if (keyframeObj.has("vector"))
            return keyframeObj.get("vector").getAsJsonArray();

        if (keyframeObj.has("pre"))
            return keyframeObj.get("pre").getAsJsonArray();

        return keyframeObj.get("post").getAsJsonArray();
    }

	private static void addBedrockKeyframes(double timestamp, JsonObject keyframe, List<DoubleObjectPair<JsonElement>> keyframes) {
		boolean addedFrame = false;

		if (keyframe.has("pre")) {
			addedFrame = true;

			JsonArray value = extractBedrockKeyframe(keyframe.get("pre"));
			JsonObject result = null;
			if (keyframe.has("easing")) {
				result = new JsonObject();
				result.add("vector", value);
				result.add("easing", keyframe.get("easing"));
				if (keyframe.has("easingArgs")) result.add("easingArgs", keyframe.get("easingArgs"));
			}

			keyframes.add(DoubleObjectPair.of(timestamp == 0 ? timestamp : timestamp - 0.001d, result != null ? result : value));
		}

		if (keyframe.has("post")) {
            JsonArray values = extractBedrockKeyframe(keyframe.get("post"));

			if (keyframe.has("lerp_mode")) {
				JsonObject keyframeObj = new JsonObject();

				keyframeObj.add("vector", values);
				keyframeObj.add("easing", keyframe.get("lerp_mode"));

				keyframes.add(DoubleObjectPair.of(timestamp, keyframeObj));
			}
			else {
				keyframes.add(DoubleObjectPair.of(timestamp, values));
			}

			return;
		}

		if (!addedFrame)
			throw new JsonParseException("Invalid keyframe data - expected array, found " + keyframe);
	}

	private KeyframeStack<Keyframe<MathValue>> buildKeyframeStack(List<DoubleObjectPair<JsonElement>> entries, boolean isForRotation) throws CompoundException {
		if (entries.isEmpty())
			return new KeyframeStack<>();

		List<Keyframe<MathValue>> xFrames = new ObjectArrayList<>();
		List<Keyframe<MathValue>> yFrames = new ObjectArrayList<>();
		List<Keyframe<MathValue>> zFrames = new ObjectArrayList<>();

		MathValue xPrev = null;
		MathValue yPrev = null;
		MathValue zPrev = null;
		DoubleObjectPair<JsonElement> prevEntry = null;

		for (DoubleObjectPair<JsonElement> entry : entries) {
			JsonElement element = entry.right();

			double prevTime = prevEntry != null ? prevEntry.leftDouble() : 0;
			double curTime = entry.leftDouble();
			double timeDelta = curTime - prevTime;

			JsonArray keyFrameVector = element instanceof JsonArray array ? array : GsonHelper.getAsJsonArray(element.getAsJsonObject(), "vector");
			MathValue rawXValue = MathParser.parseJson(keyFrameVector.get(0));
			MathValue rawYValue = MathParser.parseJson(keyFrameVector.get(1));
			MathValue rawZValue = MathParser.parseJson(keyFrameVector.get(2));
			MathValue xValue = compressMathValue(isForRotation && rawXValue instanceof Constant ? new Constant(Math.toRadians(-rawXValue.get(null))) : rawXValue);
			MathValue yValue = compressMathValue(isForRotation && rawYValue instanceof Constant ? new Constant(Math.toRadians(-rawYValue.get(null))) : rawYValue);
			MathValue zValue = compressMathValue(isForRotation && rawZValue instanceof Constant ? new Constant(Math.toRadians(rawZValue.get(null))) : rawZValue);

			JsonObject entryObj = element instanceof JsonObject obj ? obj : null;
			EasingType easingType = entryObj != null && entryObj.has("easing") ? EasingType.fromJson(entryObj.get("easing")) : EasingType.LINEAR;
			List<MathValue> easingArgs = entryObj != null && entryObj.has("easingArgs") ?
										 JsonUtil.jsonArrayToList(GsonHelper.getAsJsonArray(entryObj, "easingArgs"), ele -> new Constant(ele.getAsDouble())) :
										 new ObjectArrayList<>();

			List<MathValue> leftValues = entryObj != null && entryObj.has("left") ? JsonUtil.jsonArrayToList(GsonHelper.getAsJsonArray(entryObj, "left"), ele -> new Constant(ele.getAsDouble())) : ObjectArrayList.of(new Constant(0), new Constant(0), new Constant(0));
			List<MathValue> rightValues = entryObj != null && entryObj.has("right") ? JsonUtil.jsonArrayToList(GsonHelper.getAsJsonArray(entryObj, "right"), ele -> new Constant(ele.getAsDouble())) : ObjectArrayList.of(new Constant(0), new Constant(0), new Constant(0));
			List<MathValue> leftTimes = entryObj != null && entryObj.has("left_time") ? JsonUtil.jsonArrayToList(GsonHelper.getAsJsonArray(entryObj, "left_time"), ele -> new Constant(ele.getAsDouble())) : ObjectArrayList.of(new Constant(-0.1), new Constant(-0.1), new Constant(-0.1));
			List<MathValue> rightTimes = entryObj != null && entryObj.has("right_time") ? JsonUtil.jsonArrayToList(GsonHelper.getAsJsonArray(entryObj, "right_time"), ele -> new Constant(ele.getAsDouble())) : ObjectArrayList.of(new Constant(0.1), new Constant(0.1), new Constant(0.1));

			xFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? xValue : xPrev, xValue, easingType, easingType == EasingType.BEZIER ? ObjectArrayList.of(leftValues.get(0), leftTimes.get(0), rightValues.get(0), rightTimes.get(0)) : easingArgs));
			yFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? yValue : yPrev, yValue, easingType, easingType == EasingType.BEZIER ? ObjectArrayList.of(leftValues.get(1), leftTimes.get(1), rightValues.get(1), rightTimes.get(1)) : easingArgs));
			zFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? zValue : zPrev, zValue, easingType, easingType == EasingType.BEZIER ? ObjectArrayList.of(leftValues.get(2), leftTimes.get(2), rightValues.get(2), rightTimes.get(2)) : easingArgs));

			xPrev = xValue;
			yPrev = yValue;
			zPrev = zValue;
			prevEntry = entry;
		}

		return new KeyframeStack<>(addArgsForKeyframes(xFrames), addArgsForKeyframes(yFrames), addArgsForKeyframes(zFrames));
	}

	private List<Keyframe<MathValue>> addArgsForKeyframes(List<Keyframe<MathValue>> frames) {
		if (frames.size() == 1) {
			Keyframe<MathValue> frame = frames.getFirst();

			if (frame.easingType() != EasingType.LINEAR) {
				frames.set(0, new Keyframe<>(frame.length(), frame.startValue(), frame.endValue()));

				return frames;
			}
		}

		for (int i = 0; i < frames.size(); i++) {
			Keyframe<MathValue> frame = frames.get(i);

			if (frame.easingType() == EasingType.CATMULLROM) {
				frames.set(i, new Keyframe<>(frame.length(), frame.startValue(), frame.endValue(), frame.easingType(), ObjectArrayList.of(
						i == 0 ? frame.startValue() : frames.get(i - 1).endValue(),
						i + 1 >= frames.size() ? frame.endValue() : frames.get(i + 1).endValue()
				)));
			}
			else if (frame.easingType() == EasingType.BEZIER) {
				MathValue rightValue = frame.easingArgs().get(2);
				MathValue rightTime = frame.easingArgs().get(3);
				frame.easingArgs().remove(2);
				frame.easingArgs().remove(2);
				if (frames.size() > i + 1) {
					Keyframe<MathValue> nextKeyframe = frames.get(i + 1);
					if (nextKeyframe.easingType() == EasingType.BEZIER) {
						nextKeyframe.easingArgs().add(rightValue);
						nextKeyframe.easingArgs().add(rightTime);
					}
					else frames.set(i + 1, new Keyframe<>(nextKeyframe.length(), nextKeyframe.startValue(), nextKeyframe.endValue(), EasingType.BEZIER_AFTER, ObjectArrayList.of(rightValue, rightTime)));
				}
			}
		}

		return frames;
	}

	private MathValue compressMathValue(MathValue input) {
		if (COMPRESSION_CACHE == null || input.isMutable())
			return input;

		return COMPRESSION_CACHE.computeIfAbsent(input.get(null), Constant::new);
	}

	private static double calculateAnimationLength(BoneAnimation[] boneAnimations) {
		double length = 0;

		for (BoneAnimation animation : boneAnimations) {
			length = Math.max(length, animation.rotationKeyFrames().getLastKeyframeTime());
			length = Math.max(length, animation.positionKeyFrames().getLastKeyframeTime());
			length = Math.max(length, animation.scaleKeyFrames().getLastKeyframeTime());
		}

		return length == 0 ? Double.MAX_VALUE : length;
	}

	private static double readTimestamp(String timestamp) {
		return NumberUtils.isCreatable(timestamp) ? Double.parseDouble(timestamp) : 0;
	}
}