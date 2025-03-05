package software.bernie.geckolib.loading.json.typeadapter;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
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

/**
 * {@link Gson} {@link JsonDeserializer} for {@link BakedAnimations}.<br>
 * Acts as the deserialization interface for {@code BakedAnimations}
 */
public  class BakedAnimationsAdapter implements JsonDeserializer<BakedAnimations> {
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
					GeckoLibConstants.LOGGER.error(compoundEx.withMessage("Unable to parse animation: " + entry.getKey()).getLocalizedMessage());
				}
				else {
					GeckoLibConstants.LOGGER.error("Unable to parse animation: " + entry.getKey());
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
		Animation.Keyframes keyframes = context.deserialize(animationObj, Animation.Keyframes.class);

		if (length == -1)
			length = calculateAnimationLength(boneAnimations);

		return new Animation(name, length, loopType, boneAnimations, keyframes);
	}

	private BoneAnimation[] bakeBoneAnimations(JsonObject bonesObj) throws CompoundException {
		BoneAnimation[] animations = new BoneAnimation[bonesObj.size()];
		int index = 0;

		for (Map.Entry<String, JsonElement> entry : bonesObj.entrySet()) {
			JsonObject entryObj = entry.getValue().getAsJsonObject();
			KeyframeStack<Keyframe<MathValue>> scaleFrames = buildKeyframeStack(getTripletObj(entryObj.get("scale")), false);
			KeyframeStack<Keyframe<MathValue>> positionFrames = buildKeyframeStack(getTripletObj(entryObj.get("position")), false);
			KeyframeStack<Keyframe<MathValue>> rotationFrames = buildKeyframeStack(getTripletObj(entryObj.get("rotation")), true);

			animations[index] = new BoneAnimation(entry.getKey(), rotationFrames, positionFrames, scaleFrames);
			index++;
		}

		return animations;
	}

	private static List<Pair<String, JsonElement>> getTripletObj(JsonElement element) {
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
			return ObjectArrayList.of(Pair.of("0", array));

		if (element instanceof JsonObject obj) {
			List<Pair<String, JsonElement>> list = new ObjectArrayList<>();
			KeyframeType lastKeyframeType = KeyframeType.LINEAR;

			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				if (entry.getValue() instanceof JsonObject entryObj && !entryObj.has("vector")) {
					Pair<Pair<String, JsonElement>, KeyframeType> result = getTripletObjBedrock(entry.getKey(), entryObj, lastKeyframeType);
					list.add(result.getFirst());
					lastKeyframeType = result.getSecond();

					continue;
				}

				list.add(Pair.of(entry.getKey(), entry.getValue()));
			}

			return list;
		}

		throw new JsonParseException("Invalid object type provided to getTripletObj, got: " + element);
	}

	private static Pair<Pair<String, JsonElement>, KeyframeType> getTripletObjBedrock(String timestamp, JsonObject keyframe, KeyframeType lastKeyframesType) {
		JsonArray array;
		JsonObject keyframeValues = new JsonObject();

		switch (lastKeyframesType) {
			case SMOOTH -> keyframeValues.addProperty("easing", "catmullrom");
			case STEP -> keyframeValues.addProperty("easing", "single_step");
		}

		if (keyframe.isJsonArray()) {
			keyframeValues.add("vector", keyframe);
			return Pair.of(Pair.of(NumberUtils.isCreatable(timestamp) ? timestamp : "0", keyframeValues), KeyframeType.LINEAR);
		}

		if (keyframe.has("post")) {
			if (keyframe.has("lerp_mode")) {
				JsonElement post = keyframe.get("post");
				array = post.isJsonArray() ? post.getAsJsonArray() : GsonHelper.getAsJsonArray(post.getAsJsonObject(), "vector");
				if (array != null) {
					keyframeValues.add("vector", array);
					return Pair.of(Pair.of(NumberUtils.isCreatable(timestamp) ? timestamp : "0", keyframeValues), KeyframeType.SMOOTH);
				}
			}
			else if (keyframe.has("pre")) {
				JsonElement pre = keyframe.get("pre");
				array = pre.isJsonArray() ? pre.getAsJsonArray() : GsonHelper.getAsJsonArray(pre.getAsJsonObject(), "vector");
				keyframeValues.add("vector", array);
				keyframeValues.add("post", keyframe.get("post"));
				return Pair.of(Pair.of(NumberUtils.isCreatable(timestamp) ? timestamp : "0", keyframeValues), KeyframeType.STEP);
			}
		}

		throw new JsonParseException("Invalid keyframe data - expected array, found " + keyframe);
	}

	private KeyframeStack<Keyframe<MathValue>> buildKeyframeStack(List<Pair<String, JsonElement>> entries, boolean isForRotation) throws CompoundException {
		if (entries.isEmpty())
			return new KeyframeStack<>();

		List<Keyframe<MathValue>> xFrames = new ObjectArrayList<>();
		List<Keyframe<MathValue>> yFrames = new ObjectArrayList<>();
		List<Keyframe<MathValue>> zFrames = new ObjectArrayList<>();

		MathValue xPrev = null;
		MathValue yPrev = null;
		MathValue zPrev = null;
		Pair<String, JsonElement> prevEntry = null;

		JsonArray bedrockPost = null;

		for (Pair<String, JsonElement> entry : entries) {
			String key = entry.getFirst();
			JsonElement element = entry.getSecond();

			if (key.equals("easing") || key.equals("easingArgs") || key.equals("lerp_mode"))
				continue;

			double prevTime = prevEntry != null ? Double.parseDouble(prevEntry.getFirst()) : 0;
			double curTime = NumberUtils.isCreatable(key) ? Double.parseDouble(entry.getFirst()) : 0;
			double timeDelta = curTime - prevTime;

			JsonArray keyFrameVector = element instanceof JsonArray array ? array : GsonHelper.getAsJsonArray(element.getAsJsonObject(), "vector");
			MathValue rawXValue = MathParser.parseJson(keyFrameVector.get(0));
			MathValue rawYValue = MathParser.parseJson(keyFrameVector.get(1));
			MathValue rawZValue = MathParser.parseJson(keyFrameVector.get(2));
			MathValue xValue = isForRotation && rawXValue instanceof Constant ? new Constant(Math.toRadians(-rawXValue.get())) : rawXValue;
			MathValue yValue = isForRotation && rawYValue instanceof Constant ? new Constant(Math.toRadians(-rawYValue.get())) : rawYValue;
			MathValue zValue = isForRotation && rawZValue instanceof Constant ? new Constant(Math.toRadians(rawZValue.get())) : rawZValue;

			JsonObject entryObj = element instanceof JsonObject obj ? obj : null;
			EasingType easingType = entryObj != null && entryObj.has("easing") ? EasingType.fromJson(entryObj.get("easing")) : EasingType.LINEAR;
			List<MathValue> easingArgs = entryObj != null && entryObj.has("easingArgs") ?
					JsonUtil.jsonArrayToList(GsonHelper.getAsJsonArray(entryObj, "easingArgs"), ele -> new Constant(ele.getAsDouble())) :
					new ObjectArrayList<>();
			boolean seperateArgs = easingType == EasingType.SINGLE_STEP || easingType == EasingType.CATMULLROM;

			xFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? xValue : xPrev, xValue, easingType, seperateArgs ? new ObjectArrayList<>() : easingArgs));
			yFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? yValue : yPrev, yValue, easingType, seperateArgs ? new ObjectArrayList<>() : easingArgs));
			zFrames.add(new Keyframe<>(timeDelta * 20, prevEntry == null ? zValue : zPrev, zValue, easingType, seperateArgs ? new ObjectArrayList<>() : easingArgs));

			xPrev = xValue;
			yPrev = yValue;
			zPrev = zValue;
			prevEntry = entry;

			if (easingType == EasingType.SINGLE_STEP && bedrockPost != null) {
				rawXValue = MathParser.parseJson(bedrockPost.get(0));
				rawYValue = MathParser.parseJson(bedrockPost.get(1));
				rawZValue = MathParser.parseJson(bedrockPost.get(2));
				xValue = isForRotation && rawXValue instanceof Constant ? new Constant(Math.toRadians(-rawXValue.get())) : rawXValue;
				yValue = isForRotation && rawYValue instanceof Constant ? new Constant(Math.toRadians(-rawYValue.get())) : rawYValue;
				zValue = isForRotation && rawZValue instanceof Constant ? new Constant(Math.toRadians(rawZValue.get())) : rawZValue;
				xFrames.getLast().easingArgs().add(xValue);
				yFrames.getLast().easingArgs().add(yValue);
				zFrames.getLast().easingArgs().add(zValue);
			}
			if (entryObj != null && entryObj.has("post")) {
				bedrockPost = entryObj.getAsJsonArray("post");
			}
		}

		applyCatmullRomEasing(xFrames);
		applyCatmullRomEasing(yFrames);
		applyCatmullRomEasing(zFrames);

		return new KeyframeStack<>(xFrames, yFrames, zFrames);
	}

	private void applyCatmullRomEasing(List<Keyframe<MathValue>> frames) {
		for (int i=0; i < frames.size(); i++) {
			Keyframe<MathValue> frame = frames.get(i);
			if (frame.easingType() == EasingType.CATMULLROM) {
				frame.easingArgs().add(i-2 >= 0 ? frames.get(i-2).endValue() : frames.get(0).endValue());
				frame.easingArgs().add(i+1 < frames.size()-1 ? frames.get(i+1).endValue() : frames.get(frames.size()-1).endValue());
			}
		}
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

	public enum KeyframeType {
		SMOOTH,
		STEP,
		LINEAR
	}
}
