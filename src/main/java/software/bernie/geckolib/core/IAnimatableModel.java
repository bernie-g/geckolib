package software.bernie.geckolib.core;

import software.bernie.geckolib.core.builder.Animation;
import software.bernie.geckolib.core.processor.AnimationProcessor;
import software.bernie.geckolib.core.processor.IBone;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;

import javax.annotation.Nullable;

public interface IAnimatableModel<E>
{
	default float getCurrentTick()
	{
		return (System.nanoTime() / 1000000L / 50f);
	}

	default void setLivingAnimations(E entity)
	{
		this.setLivingAnimations(entity, null);
	}

	void setLivingAnimations(E entity, @Nullable AnimationTestPredicate customPredicate);

	AnimationProcessor getAnimationProcessor();

	Animation getAnimation(String name, IAnimatable animatable);

	/**
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	default IBone getBone(String boneName)
	{
		return this.getAnimationProcessor().getBone(boneName);
	}
}
