/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.EventKeyFrame;
import software.bernie.geckolib3.core.keyframe.ParticleEventKeyFrame;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A compiled animation instance for use by the {@link AnimationController}<br>
 * Modifications or extensions of a compiled Animation are not supported, and therefore an instance of <code>Animation</code> is considered final and immutable.
 */
public record Animation(String name, double length, LoopType loopType, BoneAnimation[] boneAnimations, KeyFrames keyFrames) {
	public record KeyFrames(EventKeyFrame<String>[] sounds, ParticleEventKeyFrame[] particles, EventKeyFrame<String>[] customInstructions) {}

	/**
	 * Loop type functional interface to define post-play handling for a given animation. <br>
	 * Custom loop types are supported by extending this class and providing the extended class instance as the loop type for the animation
	 */
	@FunctionalInterface
	public interface LoopType {
		final Map<String, LoopType> LOOP_TYPES = new ConcurrentHashMap<>(4);

		LoopType PLAY_ONCE = (animatable, controller, currentAnimation) -> false;
		LoopType LOOP = (animatable, controller, currentAnimation) -> true;

		/**
		 * Override in a custom instance to dynamically decide whether an animation should repeat or stop
		 * @param animatable The animating object relevant to this method call
		 * @param controller The {@link AnimationController} playing the current animation
		 * @param currentAnimation The current animation that just played
		 * @return Whether the animation should play again, or stop
		 */
		boolean shouldPlayAgain(GeoAnimatable animatable, AnimationController<? extends GeoAnimatable> controller, Animation currentAnimation);

		/**
		 * Retrieve a LoopType instance based on a {@link JsonElement}.
		 * Returns either {@link LoopType#PLAY_ONCE} or {@link LoopType#LOOP} based on a boolean or string element type,
		 * or any other registered loop type with a matching type string.
		 * @param json The <code>loop</code> {@link JsonElement} to attempt to parse
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

		static LoopType fromString(String name) {
			if (LOOP_TYPES.isEmpty()) {
				LOOP_TYPES.put("false", PLAY_ONCE);
				LOOP_TYPES.put("play_once", PLAY_ONCE);
				LOOP_TYPES.put("loop", LOOP);
				LOOP_TYPES.put("true", LOOP);
			}

			return LOOP_TYPES.getOrDefault(name.toLowerCase(Locale.ROOT), PLAY_ONCE);
		}

		static void addCustom(String name, LoopType loopType) {
			LOOP_TYPES.put(name, loopType);
		}
	}
}
