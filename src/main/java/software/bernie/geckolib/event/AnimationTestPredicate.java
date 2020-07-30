package software.bernie.geckolib.event;

import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.entity.IAnimatable;

import javax.annotation.Nullable;

public class AnimationTestPredicate<T extends IAnimatable>
{
	private final T entity;
	private final double animationTick;

	@Nullable
	protected AnimationController controller;

	public AnimationTestPredicate(T entity, double animationTick)
	{
		this.animationTick = animationTick;
		this.entity = entity;
	}

	/**
	 * Gets the amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 *
	 * @return the animation tick
	 */
	public double getAnimationTick()
	{
		return animationTick;
	}

	public T getEntity()
	{
		return entity;
	}

	public AnimationController getController()
	{
		return controller;
	}

	public void setController(@Nullable AnimationController controller)
	{
		this.controller = controller;
	}
}
