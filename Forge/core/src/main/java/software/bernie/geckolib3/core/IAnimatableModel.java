package software.bernie.geckolib3.core;

import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;

public interface IAnimatableModel<E>
{
	default double getCurrentTick()
	{
		return (System.nanoTime() / 1000000L / 50.0);
	}

	default void setLivingAnimations(E entity, Integer uniqueID)
	{
		this.setLivingAnimations(entity, uniqueID, null);
	}

	void setLivingAnimations(E entity, Integer uniqueID, AnimationEvent customPredicate);

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
		IBone bone = this.getAnimationProcessor().getBone(boneName);
		if(bone == null)
		{
			throw new RuntimeException("Could not find bone: " + boneName);
		}
		return bone;
	}

	void setMolangQueries(IAnimatable animatable, double currentTick);
}
