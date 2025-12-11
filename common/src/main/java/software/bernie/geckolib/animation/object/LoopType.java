package software.bernie.geckolib.animation.object;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.state.AnimationPoint;
import software.bernie.geckolib.animation.state.AnimationTimeline;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loop type functional interface to define post-play handling for a given {@link Animation}
 * <p>
 * Custom loop types are supported by extending this class and providing the extended class instance as the loop type for the animation
 */
@FunctionalInterface
public interface LoopType {
    Map<String, LoopType> LOOP_TYPES = new ConcurrentHashMap<>(4);

    LoopType DEFAULT = (animatable, animationPoint, timelineStage, renderState, controller) ->
            animationPoint.animation().loopType().shouldKeepPlaying(animatable, animationPoint, timelineStage, renderState, controller);
    LoopType PLAY_ONCE = register("play_once", register("false", (animatable, animationPoint, timelineStage, renderState, controller) ->
            false));
    LoopType HOLD_ON_LAST_FRAME = register("hold_on_last_frame", (animatable, animationPoint, timelineStage, renderState, controller) -> {
        controller.setTimelineTime(timelineStage.endTime());

        return true;
    });
    LoopType LOOP = register("loop", register("true", (animatable, animationPoint, timelineStage, renderState, controller) -> {
        controller.setTimelineTime(timelineStage.startTime() + (controller.getCurrentTimelineTime() - timelineStage.endTime()));

        return true;
    }));

    /**
     * Override in a custom instance to dynamically decide whether an animation should repeat or stop.
     * <p>
     * Timeline changes related to replaying the animation must be made prior to returning
     *
     * @param animatable The animatable running the animation
     * @param animationPoint The current {@link AnimationPoint} for the animation
     * @param timelineStage The current {@link AnimationTimeline.Stage} for the animation being checked
     * @param renderState The GeoRenderState for the current render pass
     * @param controller The {@link AnimationController} playing the current animation
     *
     * @return Whether the animation should play again or stop
     */
    boolean shouldKeepPlaying(GeoAnimatable animatable, AnimationPoint animationPoint, AnimationTimeline.Stage timelineStage,
                              GeoRenderState renderState, AnimationController<? extends GeoAnimatable> controller);

    /**
     * Retrieve a LoopType instance based on a {@link JsonElement}
     * <p>
     * Returns either {@link LoopType#PLAY_ONCE} or {@link LoopType#LOOP} based on a boolean or string element type,
     * or any other registered loop type with a matching type string
     *
     * @param json The {@code loop} {@link JsonElement} to attempt to parse
     * @return A usable LoopType instance
     */
    static LoopType fromJson(JsonElement json) {
        if (json == null || !json.isJsonPrimitive())
            return PLAY_ONCE;

        JsonPrimitive primitive = json.getAsJsonPrimitive();

        if (primitive.isBoolean())
            return primitive.getAsBoolean() ? LOOP : PLAY_ONCE;

        if (primitive.isString())
            return fromString(primitive.getAsString());

        return PLAY_ONCE;
    }

    /**
     * Get the registered name for this LoopType
     *
     * @throws IllegalStateException if this LoopType has not been registered
     */
    default String getId() throws IllegalStateException {
        for (String loopType : LOOP_TYPES.keySet()) {
            if (LOOP_TYPES.get(loopType) == this)
                return loopType;
        }

        throw new IllegalStateException("LoopType has not been registered before being used!");
    }

    /**
     * Get a registered LoopType by name, or {@link LoopType#PLAY_ONCE} if none match
     */
    static LoopType fromString(String name) {
        return LOOP_TYPES.getOrDefault(name, PLAY_ONCE);
    }

    /**
     * Register a LoopType with Geckolib for handling loop functionality of animations
     * <p>
     * <b><u>MUST be called during mod construct</u></b>
     * <p>
     * It is recommended you don't call this directly, and instead call it via {@code GeckoLibUtil#addCustomLoopType}
     *
     * @param name The name of the loop type
     * @param loopType The loop type to register
     * @return The registered {@code LoopType}
     */
    static LoopType register(String name, LoopType loopType) {
        LOOP_TYPES.put(name, loopType);

        return loopType;
    }
}