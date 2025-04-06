package software.bernie.geckolib.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animation.keyframe.BoneAnimation;
import software.bernie.geckolib.animation.keyframe.event.data.CustomInstructionKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.ParticleKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.SoundKeyframeData;
import software.bernie.geckolib.loading.math.value.Variable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A compiled animation instance for use by the {@link AnimationController}
 * <p>
 * Modifications or extensions of a compiled Animation are not supported, and therefore an instance of <code>Animation</code> is considered final and immutable
 */
public record Animation(String name, double length, LoopType loopType, BoneAnimation[] boneAnimations, List<Variable> usedVariables, KeyframeMarkers keyframeMarkers) {
	/**
	 * Create a new Animation instance from the given values, automatically compiling the {@link #usedVariables} list
	 *
	 * @param name The name of the animation
	 * @param length The length (in seconds) of the animation
	 * @param loopType The type of looping this animation should have once finished
	 * @param boneAnimations The keyframe stacks for each bone in this animation
	 * @param keyframeMarkers Any custom keyframe instructions this animation contains
	 */
	public static Animation create(String name, double length, LoopType loopType, BoneAnimation[] boneAnimations, KeyframeMarkers keyframeMarkers) {
		Set<Variable> usedVariables = new ReferenceArraySet<>();

		for (BoneAnimation boneAnimation : boneAnimations) {
			usedVariables.addAll(boneAnimation.getUsedVariables());
		}

		return new Animation(name, length, loopType, boneAnimations, new ReferenceArrayList<>(usedVariables), keyframeMarkers);
	}

	public record KeyframeMarkers(SoundKeyframeData[] sounds, ParticleKeyframeData[] particles, CustomInstructionKeyframeData[] customInstructions) {}

	@ApiStatus.Internal
	public static Animation generateWaitAnimation(double length) {
		return new Animation(RawAnimation.Stage.WAIT, length, LoopType.PLAY_ONCE, new BoneAnimation[0], new ReferenceArrayList<>(0),
				new KeyframeMarkers(new SoundKeyframeData[0], new ParticleKeyframeData[0], new CustomInstructionKeyframeData[0]));
	}

	/**
	 * Loop type functional interface to define post-play handling for a given animation
	 * <p>
	 * Custom loop types are supported by extending this class and providing the extended class instance as the loop type for the animation
	 */
	@FunctionalInterface
	public interface LoopType {
		Map<String, LoopType> LOOP_TYPES = new ConcurrentHashMap<>(4);

		LoopType DEFAULT = (animationState, controller, currentAnimation) -> currentAnimation.loopType().shouldPlayAgain(animationState, controller, currentAnimation);
		LoopType PLAY_ONCE = register("play_once", register("false", (animationState, controller, currentAnimation) -> false));
		LoopType HOLD_ON_LAST_FRAME = register("hold_on_last_frame", (animationState, controller, currentAnimation) -> {
			controller.setAnimationState(AnimationController.State.PAUSED);

			return true;
		});
		LoopType LOOP = register("loop", register("true", (animationState, controller, currentAnimation) -> true));

		/**
		 * Override in a custom instance to dynamically decide whether an animation should repeat or stop
		 *
		 * @param animationState The {@link AnimationState} for the current render pass
		 * @param controller The {@link AnimationController} playing the current animation
		 * @param currentAnimation The current animation that just played
		 * @return Whether the animation should play again, or stop
		 */
		boolean shouldPlayAgain(AnimationState<?> animationState, AnimationController<? extends GeoAnimatable> controller, Animation currentAnimation);

		/**
		 * Retrieve a LoopType instance based on a {@link JsonElement}
		 * <p>
		 * Returns either {@link LoopType#PLAY_ONCE} or {@link LoopType#LOOP} based on a boolean or string element type,
		 * or any other registered loop type with a matching type string
		 *
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
}
