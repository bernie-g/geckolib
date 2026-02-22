package com.geckolib.loading.definition.animation;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animation.object.EasingType;
import com.geckolib.animation.state.AnimationPoint;
import com.geckolib.cache.animation.Keyframe;
import com.geckolib.loading.definition.animation.object.KeyframeTriplet;
import com.geckolib.loading.math.MathParser;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.misc.ToRadFunction;
import com.geckolib.loading.math.value.Negative;
import com.geckolib.util.JsonUtil;

import java.util.List;

/// Container class for a single actor animation's bone animation keyframe,
/// only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// Note that the timestamp for this keyframe is not contained here, and is instead held by the [ActorBoneAnimationEntry#keyframes()] map, if present
///
/// @param values The actual value for this keyframe. Can technically be null if only [#preKeyframe] or [#postKeyframe] are present
/// @param interpolationType An optional interpolation/easing type declaration
/// @param easingArgs An optional array of values to be used by the interpolation type for further customization
/// @param preKeyframe An optional keyframe to insert directly prior to this keyframe, usually for interpolation purposes
/// @param postKeyframe An optional keyframe to insert directly after this keyframe, usually for interpolation purposes
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_actor_animation_1.8.0?view=minecraft-bedrock-stable">Bedrock Actor Animation Spec 1.8.0</a>
@ApiStatus.Internal
public record ActorBoneAnimationKeyframe(@Nullable ActorBoneAnimationKeyframeValues values, @Nullable String interpolationType, DoubleOrString @Nullable [] easingArgs,
                                         @Nullable ActorBoneAnimationKeyframe preKeyframe, @Nullable ActorBoneAnimationKeyframe postKeyframe) {
    /// Parse an ActorBoneAnimationKeyframe instance from raw .json input via [Gson]
    public static JsonDeserializer<ActorBoneAnimationKeyframe> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            if (json.isJsonObject()) {
                final JsonObject obj = json.getAsJsonObject();
                final ActorBoneAnimationKeyframeValues values = GsonHelper.getAsObject(obj, "vector", null, context, ActorBoneAnimationKeyframeValues.class);
                final String interpolationType = obj.has("lerp_mode") ? obj.get("lerp_mode").getAsString() : obj.has("easing") ? obj.get("easing").getAsString() : null;
                final DoubleOrString[] easingArgs = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "easingArgs", null), context, DoubleOrString.class);
                final ActorBoneAnimationKeyframe preKeyframe = GsonHelper.getAsObject(obj, "pre", null, context, ActorBoneAnimationKeyframe.class);
                final ActorBoneAnimationKeyframe postKeyframe = GsonHelper.getAsObject(obj, "post", null, context, ActorBoneAnimationKeyframe.class);

                return new ActorBoneAnimationKeyframe(values, interpolationType, easingArgs, preKeyframe, postKeyframe);
            }

            if (json.isJsonPrimitive() || json.isJsonArray())
                return new ActorBoneAnimationKeyframe(context.deserialize(json, ActorBoneAnimationKeyframeValues.class), null, null, null, null);

            throw new JsonParseException("ActorBoneAnimationKeyframe has invalid format, expected either String, Array or JsonObject: " + json);
        };
    }

    /// Bake this `ActorBoneAnimationKeyframe` instance into the final [Keyframe] instance array that GeckoLib uses for animating,
    /// held in a [KeyframeTriplet] container for easy returning
    ///
    /// The returned array will be 0+ elements in length, depending on whether any pre or post keyframes are present
    public KeyframeTriplet[] bake(double timestamp, AnimationPoint.Transform transformType, boolean forceLinearInterpolation, @Nullable KeyframeTriplet lastTriplet, MathParser mathParser) {
        if (this.values == null && this.preKeyframe == null && this.postKeyframe == null) {
            GeckoLibConstants.LOGGER.warn("ActorBoneAnimationKeyframe has no values, pre, or post keyframes!");

            return new KeyframeTriplet[0];
        }

        final List<KeyframeTriplet> triplets = new ObjectArrayList<>(3);

        if (this.preKeyframe != null) {
            KeyframeTriplet[] preKeyframeTriplets = this.preKeyframe.bake(timestamp - Mth.EPSILON, transformType, forceLinearInterpolation, lastTriplet, mathParser);

            if (preKeyframeTriplets.length > 0) {
                triplets.addAll(ObjectArrayList.of(preKeyframeTriplets));
                lastTriplet = triplets.getLast();
            }
        }

        if (this.values != null) {
            triplets.add(bakeTriplet(timestamp, this.values, transformType, forceLinearInterpolation, lastTriplet, mathParser));
            lastTriplet = triplets.getLast();
        }

        if (this.postKeyframe != null) {
            KeyframeTriplet[] postKeyframeTriplets = this.postKeyframe.bake(timestamp + Mth.EPSILON, transformType, forceLinearInterpolation, lastTriplet, mathParser);

            if (postKeyframeTriplets.length > 0)
                triplets.addAll(ObjectArrayList.of(postKeyframeTriplets));
        }

        return triplets.toArray(new KeyframeTriplet[0]);
    }

    /// Bake the core [KeyframeTriplet] this instance represents
    private KeyframeTriplet bakeTriplet(double timestamp, ActorBoneAnimationKeyframeValues values, AnimationPoint.Transform transformType, boolean forceLinearInterpolation,
                                        @Nullable KeyframeTriplet lastTriplet, MathParser mathParser) {
        final EasingType easingType = this.interpolationType == null || forceLinearInterpolation ? EasingType.LINEAR : EasingType.fromString(this.interpolationType);
        final MathValue[] easingArgs = this.easingArgs == null ? new MathValue[0] : new MathValue[this.easingArgs.length];
        final double prevTime = lastTriplet == null ? 0 : lastTriplet.x().startTime();
        final double keyframeLength = timestamp - prevTime;
        MathValue xValue = mathParser.compileDoubleOrString(values.xValue());
        MathValue yValue = mathParser.compileDoubleOrString(values.yValue());
        MathValue zValue = mathParser.compileDoubleOrString(values.zValue());

        if (transformType == AnimationPoint.Transform.ROTATION) {
            xValue = mathParser.wrap(xValue, ToRadFunction::new, Negative::new);
            yValue = mathParser.wrap(yValue, ToRadFunction::new, Negative::new);
            zValue = mathParser.wrap(zValue, ToRadFunction::new);
        }

        for (int i = 0; i < easingArgs.length; i++) {
            easingArgs[i] = mathParser.compileDoubleOrString(this.easingArgs[i]);
        }

        return new KeyframeTriplet(new Keyframe(timestamp, keyframeLength, lastTriplet != null ? lastTriplet.x().endValue() : xValue, xValue, easingType, easingArgs),
                                   new Keyframe(timestamp, keyframeLength, lastTriplet != null ? lastTriplet.y().endValue() : yValue, yValue, easingType, easingArgs),
                                   new Keyframe(timestamp, keyframeLength, lastTriplet != null ? lastTriplet.z().endValue() : zValue, zValue, easingType, easingArgs));
    }
}
