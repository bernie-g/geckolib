/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.event;
import software.bernie.geckolib.animation.controller.AnimationController;

public class AnimationEvent<T>
{
	private final T entity;
	private final double animationTick;
	private final AnimationController controller;
	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *
	 * @param entity          the entity
	 * @param animationTick   The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param controller      the controller
	 */
	public AnimationEvent(T entity, double animationTick, AnimationController controller)
	{
		this.entity = entity;
		this.animationTick = animationTick;
		this.controller = controller;
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
}
