package software.bernie.geckolib.loading.json.typeadapter;

import com.eliotlash.mclib.math.Constant;
import com.eliotlash.mclib.math.IValue;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.math.NumberUtils;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.EasingType;
import software.bernie.geckolib.core.keyframe.BoneAnimation;
import software.bernie.geckolib.core.keyframe.Keyframe;
import software.bernie.geckolib.core.keyframe.KeyframeStack;
import software.bernie.geckolib.core.molang.MolangException;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.core.molang.expressions.MolangValue;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * {@link com.google.gson.Gson} {@link JsonDeserializer} for {@link software.bernie.geckolib.loading.object.BakedAnimations}.<br>
 * Acts as the deserialization interface for {@code BakedAnimations}
 */
public class BakedAnimationsAdapter implements JsonDeserializer<BakedAnimations> {
	@Override
	public BakedAnimations deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		Map<String, Animation> animations = new Object2ObjectOpenHashMap<>(obj.size());

		for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
			try {
				animations.put(entry.getKey(), bakeAnimation(entry.getKey(), entry.getValue().getAsJsonObject(), context));
			}
			catch (MolangException ex) {
				GeckoLib.LOGGER.error("Unable to parse animation: " + entry.getKey());
				ex.printStackTrace();
			}
		}

		return new BakedAnimations(animations);
	}

	private Animation bakeAnimation(String name, JsonObject animationObj, JsonDeserializationContext context) throws MolangException {
		double length = animationObj.has("animation_length") ? GsonHelper.getAsDouble(animationObj, "animation_length") * 20d : -1;
		Animation.LoopType loopType = Animation.LoopType.fromJson(animationObj.get("loop"));
		BoneAnimation[] boneAnimations = bakeBoneAnimations(GsonHelper.getAsJsonObject(animationObj, "bones", new JsonObject()));
		Animation.Keyframes keyframes = context.deserialize(animationObj, Animation.Keyframes.class);

		if (length == -1)
			length = calculateAnimationLength(boneAnimations);

		return new Animation(name, length, loopType, boneAnimations, keyframes);
	}

	private BoneAnimation[] bakeBoneAnimations(JsonObject bonesObj) throws MolangException {
		BoneAnimation[] animations = new BoneAnimation[bonesObj.size()];
		int index = 0;

		for (Map.Entry<String, JsonElement> entry : bonesObj.entrySet()) {
			JsonObject entryObj = entry.getValue().getAsJsonObject();
			KeyframeStack<Keyframe<IValue>> scaleFrames = buildKeyframeStack(
					getKeyframes(entryObj.get("scale")), false);
			KeyframeStack<Keyframe<IValue>> positionFrames = buildKeyframeStack(
					getKeyframes(entryObj.get("position")), false);
			KeyframeStack<Keyframe<IValue>> rotationFrames = buildKeyframeStack(
					getKeyframes(entryObj.get("rotation")), true);

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

	private static void addBedrockKeyframes(double timestamp, JsonObject keyframe, List<DoubleObjectPair<JsonElement>> keyframes) {
		boolean addedFrame = false;

		if (keyframe.has("pre")) {
			JsonElement pre = keyframe.get("pre");
			addedFrame = true;

			keyframes.add(DoubleObjectPair.of(timestamp == 0 ? timestamp : timestamp - 0.001d, pre.isJsonArray() ? pre.getAsJsonArray() : GsonHelper.getAsJsonArray(pre.getAsJsonObject(), "vector")));
		}

		if (keyframe.has("post")) {
			JsonElement post = keyframe.get("post");
			JsonArray values = post.isJsonArray() ? post.getAsJsonArray() : GsonHelper.getAsJsonArray(post.getAsJsonObject(), "vector");

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

	private KeyframeStack<Keyframe<IValue>> buildKeyframeStack(List<DoubleObjectPair<JsonElement>> entries, boolean isForRotation) throws MolangException {
		if (entries.isEmpty())
			return new KeyframeStack<>();

		List<Keyframe<IValue>> xFrames = new ObjectArrayList<>();
		List<Keyframe<IValue>> yFrames = new ObjectArrayList<>();
		List<Keyframe<IValue>> zFrames = new ObjectArrayList<>();

		IValue xPrev = null;
		IValue yPrev = null;
		IValue zPrev = null;
		DoubleObjectPair<JsonElement> prevEntry = null;

		for (DoubleObjectPair<JsonElement> entry : entries) {
			JsonElement element = entry.right();

			double prevTime = prevEntry != null ? prevEntry.leftDouble() : 0;
			double curTime = entry.leftDouble();
			double timeDelta = curTime - prevTime;

			JsonArray keyFrameVector = element instanceof JsonArray array ? array : GsonHelper.getAsJsonArray(element.getAsJsonObject(), "vector");
			MolangValue rawXValue = MolangParser.parseJson(keyFrameVector.get(0));
			MolangValue rawYValue = MolangParser.parseJson(keyFrameVector.get(1));
			MolangValue rawZValue = MolangParser.parseJson(keyFrameVector.get(2));
			IValue xValue = isForRotation && rawXValue.isConstant() ? new Constant(Math.toRadians(-rawXValue.get())) : rawXValue;
			IValue yValue = isForRotation && rawYValue.isConstant() ? new Constant(Math.toRadians(-rawYValue.get())) : rawYValue;
			IValue zValue = isForRotation && rawZValue.isConstant() ? new Constant(Math.toRadians(rawZValue.get())) : rawZValue;

			JsonObject entryObj = element instanceof JsonObject obj ? obj : null;
			EasingType easingType = entryObj != null && entryObj.has("easing") ? EasingType.fromJson(entryObj.get("easing")) : EasingType.LINEAR;
			List<IValue> easingArgs = entryObj != null && entryObj.has("easingArgs") ?
									  JsonUtil.jsonArrayToList(GsonHelper.getAsJsonArray(entryObj, "easingArgs"), ele -> new Constant(ele.getAsDouble())) :
									  new ObjectArrayList<>();

			xFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? xValue : xPrev, xValue, easingType, easingArgs));
			yFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? yValue : yPrev, yValue, easingType, easingArgs));
			zFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? zValue : zPrev, zValue, easingType, easingArgs));

			xPrev = xValue;
			yPrev = yValue;
			zPrev = zValue;
			prevEntry = entry;
		}

		return new KeyframeStack<>(addSplineArgs(xFrames), addSplineArgs(yFrames), addSplineArgs(zFrames));
	}

	private List<Keyframe<IValue>> addSplineArgs(List<Keyframe<IValue>> frames) {
		if (frames.size() == 1) {
			Keyframe<IValue> frame = frames.get(0);

			if (frame.easingType() != EasingType.LINEAR) {
				frames.set(0, new Keyframe<>(frame.length(), frame.startValue(), frame.endValue()));

				return frames;
			}
		}

		for (int i = 0; i < frames.size(); i++) {
			Keyframe<IValue> frame = frames.get(i);

			if (frame.easingType() == EasingType.CATMULLROM) {
				frames.set(i, new Keyframe<>(frame.length(), frame.startValue(), frame.endValue(), frame.easingType(), ObjectArrayList.of(
						i == 0 ? frame.startValue() : frames.get(i - 1).endValue(),
						i + 1 >= frames.size() ? frame.endValue() : frames.get(i + 1).endValue()
				)));
			}
		}

		return frames;
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
