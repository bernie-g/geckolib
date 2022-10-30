package software.bernie.geckolib3.core;

import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;

public interface IAnimatableModel<E> {
	default double getCurrentTick() {
		return System.nanoTime() / 1000000L / 50d;
	}

	default void setCustomAnimations(E animatable, int instanceId) {
		setCustomAnimations(animatable, instanceId, null);
	}

	// TODO 1.20+ Remove default keyword
	default void setCustomAnimations(E animatable, int instanceId, AnimationEvent animationEvent) {}

	AnimationProcessor getAnimationProcessor();

	Animation getAnimation(String name, IAnimatable animatable);

	/**
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	default IBone getBone(String boneName) {
		IBone bone = getAnimationProcessor().getBone(boneName);

		if (bone == null)
			throw new RuntimeException("Could not find bone: " + boneName);

		return bone;
	}

	void setMolangQueries(IAnimatable animatable, double seekTime);

	/**
	 * Use {@link IAnimatableModel#setCustomAnimations(Object, int)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	default void setLivingAnimations(E animatable, Integer instanceId) {
		setCustomAnimations(animatable, instanceId);
	}

	/**
	 * Use {@link IAnimatableModel#setCustomAnimations(Object, int, AnimationEvent)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	default void setLivingAnimations(E animatable, Integer instanceId, AnimationEvent animationEvent) {
		setCustomAnimations(animatable, instanceId, animationEvent);
	}
}
