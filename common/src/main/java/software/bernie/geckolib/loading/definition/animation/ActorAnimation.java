package software.bernie.geckolib.loading.definition.animation;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.object.LoopType;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.cache.animation.BoneAnimation;
import software.bernie.geckolib.cache.animation.keyframeevent.CustomInstructionKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.KeyFrameData;
import software.bernie.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.SoundKeyframeData;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.object.CompoundException;
import software.bernie.geckolib.util.JsonUtil;

import java.util.Map;
import java.util.function.BiFunction;

/// Container class for a single actor animation, only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// Note that the name of the animation isn't contained here, and is instead held by the [ActorAnimations#animations()] map
///
/// @param animLength The defined length (in seconds) of the animation. Defaults to the time of the last keyframe, but can be overridden manually
/// @param loop The loop type for this animation, or just a true/false for default looping or non-looping
/// @param startDelay The optional delay (in Molang-seconds) before starting this animation. Not used by GeckoLib
/// @param loopDelay The optional delay (in Molang-seconds) between the end of the animation, and when it loops, if a loop is required. Not used by GeckoLib
/// @param animTimeUpdate The optional expression (in Molang-seconds) defining how to evaluate the current animation time. Not used by GeckoLib
/// @param blendWeight The weight value defining this animation's interpolation relationship to another conflicting animation. Not used by GeckoLib
/// @param overridePrevAnimation An optional toggle to force this animation to animate as absolute, and not additive to another conflicting animation. Not used by GeckoLib
/// @param boneAnimations The bone animation map for this animation, defining the actual animation itself
/// @param particleEffects The defined particle effects track keyframe markers for this animation
/// @param soundEffects The defined sound effects track keyframe markers for this animation
/// @param timeline The defined custom instruction track keyframe markers for this animation
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_actor_animation_1.8.0?view=minecraft-bedrock-stable">Bedrock Actor Animation Spec 1.8.0</a>
@ApiStatus.Internal
public record ActorAnimation(@Nullable Float animLength, @Nullable Either<Boolean, String> loop, @Nullable String startDelay, @Nullable String loopDelay, @Nullable String animTimeUpdate,
                             @Nullable String blendWeight, @Nullable Boolean overridePrevAnimation, @Nullable Map<String, ActorBoneAnimation> boneAnimations,
                             @Nullable Map<String, ActorAnimationParticleEffect> particleEffects, @Nullable Map<String, ActorAnimationSoundEffect> soundEffects, @Nullable Map<String, String> timeline) {
    /// Parse an ActorAnimation instance from raw .json input via [Gson]
    public static JsonDeserializer<ActorAnimation> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final Float animLength = JsonUtil.getOptionalFloat(obj, "animation_length");
            final Either<Boolean, String> loop = !obj.has("loop") || !(obj.get("loop") instanceof JsonPrimitive primitive) ? null : primitive.isBoolean() ?
                                                                           Either.left(primitive.getAsBoolean()) :
                                                                           Either.right(primitive.getAsString());
            final String startDelay = GsonHelper.getAsString(obj, "start_delay", null);
            final String loopDelay = GsonHelper.getAsString(obj, "loop_delay", null);
            final String animTimeUpdate = GsonHelper.getAsString(obj, "anim_time_update", null);
            final String blendWeight = GsonHelper.getAsString(obj, "blend_weight", null);
            final Boolean overridePrevAnimation = JsonUtil.getOptionalBoolean(obj, "override_previous_animation");
            final Map<String, ActorBoneAnimation> boneAnimations = JsonUtil.jsonObjToMap(GsonHelper.getAsJsonObject(obj, "bones", null), context, ActorBoneAnimation.class);
            final Map<String, ActorAnimationParticleEffect> particleEffects = JsonUtil.jsonObjToMap(GsonHelper.getAsJsonObject(obj, "particle_effects", null), context, ActorAnimationParticleEffect.class);
            final Map<String, ActorAnimationSoundEffect> soundEffects = JsonUtil.jsonObjToMap(GsonHelper.getAsJsonObject(obj, "sound_effects", null), context, ActorAnimationSoundEffect.class);
            final Map<String, String> timeline = JsonUtil.jsonObjToPrimitiveMap(GsonHelper.getAsJsonObject(obj, "timeline", null), context, JsonElement::getAsString);

            return new ActorAnimation(animLength, loop, startDelay, loopDelay, animTimeUpdate, blendWeight, overridePrevAnimation, boneAnimations, particleEffects, soundEffects, timeline);
        };
    }

    /// Bake this `ActorAnimation` instance into the final [Animation] instance that GeckoLib uses for animating
    public Animation bake(String name, MathParser mathParser) {
        final double length = this.animLength != null ? this.animLength : calculateUnknownAnimationLength();
        final LoopType loopType = this.loop == null ? LoopType.PLAY_ONCE : this.loop.map(val -> val ? LoopType.LOOP : LoopType.PLAY_ONCE, LoopType::fromString);
        final BoneAnimation[] boneAnimations = bakeBoneAnimations(mathParser);
        final Animation.KeyframeMarkers keyframeMarkers = bakeKeyframeMarkers();

        return Animation.create(name, length, loopType, boneAnimations, keyframeMarkers);
    }

    /// Bake the [#boneAnimations] map into an array of [BoneAnimation] instances that GeckoLib can use for animating
    private BoneAnimation[] bakeBoneAnimations(MathParser mathParser) {
        if (this.boneAnimations == null)
            return new BoneAnimation[0];

        final BoneAnimation[] animations = new BoneAnimation[this.boneAnimations.size()];
        int index = 0;

        for (Map.Entry<String, ActorBoneAnimation> entry : this.boneAnimations.entrySet()) {
            animations[index++] = entry.getValue().bake(entry.getKey(), mathParser);
        }

        return animations;
    }

    /// Bake the keyframe markers on this `ActorAnimation` instance into the final `Animation.KeyframeMarkers` instance that GeckoLib uses for animating
    private Animation.KeyframeMarkers bakeKeyframeMarkers() {
        if (this.particleEffects == null && this.soundEffects == null && this.timeline == null)
            return Animation.KeyframeMarkers.EMPTY;

        final SoundKeyframeData[] soundKeyframes = bakeKeyframeMap(this.soundEffects, ActorAnimationSoundEffect::bake);
        final ParticleKeyframeData[] particleKeyframes = bakeKeyframeMap(this.particleEffects, ActorAnimationParticleEffect::bake);
        final CustomInstructionKeyframeData[] timelineKeyframes = bakeKeyframeMap(this.timeline, (instructions, timestamp) -> new CustomInstructionKeyframeData(timestamp, instructions));

        return new Animation.KeyframeMarkers(soundKeyframes, particleKeyframes, timelineKeyframes);
    }

    /// Bake the map of keyframes and their timestamps into their final [KeyFrameData] instance
    @SuppressWarnings("unchecked")
    private <R, T> T[] bakeKeyframeMap(@Nullable Map<String, R> map, BiFunction<R, Double, T> bakery) {
        if (map == null)
            return (T[])new Object[0];

        final T[] array = (T[])new Object[map.size()];
        int index = 0;

        for (Map.Entry<String, R> entry : map.entrySet()) {
            array[index++] = bakery.apply(entry.getValue(), parseTimestamp(entry.getKey()));
        }

        return array;
    }

    /// Calculate the expected length of an animation (in seconds) based on the animation keyframes
    ///
    /// If the animation returns with no length, it is presumed to be infinite and so returns [Double#MAX_VALUE]
    private double calculateUnknownAnimationLength() {
        double length = 0;

        if (this.boneAnimations != null) {
            for (ActorBoneAnimation boneAnimation : this.boneAnimations.values()) {
                length = Math.max(length, boneAnimation.getAnimationLength());
            }
        }

        return length == 0 ? Double.MAX_VALUE : length;
    }

    /// Parse a timestamp value from a raw `String`
    private double parseTimestamp(String timestamp) throws RuntimeException {
        try {
            return Double.parseDouble(timestamp);
        }
        catch (NumberFormatException ex) {
            throw new CompoundException("Invalid timestamp, must be a numerical value: " + timestamp);
        }
    }
}
