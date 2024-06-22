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
					getTripletObj(entryObj.get("scale")), false);
			KeyframeStack<Keyframe<IValue>> positionFrames = buildKeyframeStack(
					getTripletObj(entryObj.get("position")), false);
			KeyframeStack<Keyframe<IValue>> rotationFrames = buildKeyframeStack(
					getTripletObj(entryObj.get("rotation")), true);

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

			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				if (entry.getValue() instanceof JsonObject entryObj && !entryObj.has("vector")) {
					list.add(getTripletObjBedrock(entry.getKey(), entryObj));

					continue;
				}

				list.add(Pair.of(entry.getKey(), entry.getValue()));
			}

			return list;
		}

		throw new JsonParseException("Invalid object type provided to getTripletObj, got: " + element);
	}

	private static Pair<String, JsonElement> getTripletObjBedrock(String timestamp, JsonObject keyframe) {
		JsonArray keyframeValues = null;

		if (keyframe.has("pre")) {
			JsonElement pre = keyframe.get("pre");
			keyframeValues = pre.isJsonArray() ? pre.getAsJsonArray() : GsonHelper.getAsJsonArray(pre.getAsJsonObject(), "vector");
		}
		else if (keyframe.has("post")) {
			JsonElement post = keyframe.get("post");
			keyframeValues = post.isJsonArray() ? post.getAsJsonArray() : GsonHelper.getAsJsonArray(post.getAsJsonObject(), "vector");
		}

		if (keyframeValues != null)
			return Pair.of(NumberUtils.isCreatable(timestamp) ? timestamp : "0", keyframeValues);

		throw new JsonParseException("Invalid keyframe data - expected array, found " + keyframe);
	}

	private KeyframeStack<Keyframe<IValue>> buildKeyframeStack(List<Pair<String, JsonElement>> entries, boolean isForRotation) throws MolangException {
		if (entries.isEmpty())
			return new KeyframeStack<>();

		List<Keyframe<IValue>> xFrames = new ObjectArrayList<>();
		List<Keyframe<IValue>> yFrames = new ObjectArrayList<>();
		List<Keyframe<IValue>> zFrames = new ObjectArrayList<>();

		IValue xPrev = null;
		IValue yPrev = null;
		IValue zPrev = null;
		Pair<String, JsonElement> prevEntry = null;

		for (Pair<String, JsonElement> entry : entries) {
			String key = entry.getFirst();
			JsonElement element = entry.getSecond();

			if (key.equals("easing") || key.equals("easingArgs") || key.equals("lerp_mode"))
				continue;

			double prevTime = prevEntry != null ? Double.parseDouble(prevEntry.getFirst()) : 0;
			double curTime = NumberUtils.isCreatable(key) ? Double.parseDouble(entry.getFirst()) : 0;
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

		return new KeyframeStack<>(xFrames, yFrames, zFrames);
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
}
