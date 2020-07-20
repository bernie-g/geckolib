/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.event;
import software.bernie.geckolib.animation.controller.AnimationController;

public class AnimationTestEvent<T>
{
	private final T entity;
	private final double animationTick;
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float partialTick;
	private final AnimationController controller;
	private final boolean isWalking;
	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *  @param entity          the entity
	 * @param animationTick   The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param limbSwing       the limb swing
	 * @param limbSwingAmount the limb swing amount
	 * @param partialTick     the partial tick
	 * @param controller      the controller
	 * @param isWalking
	 */
	public AnimationTestEvent(T entity, double animationTick, float limbSwing, float limbSwingAmount, float partialTick, AnimationController controller, boolean isWalking)
	{
		this.entity = entity;
		this.animationTick = animationTick;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.partialTick = partialTick;
		this.controller = controller;
		this.isWalking = isWalking;
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
	public float getLimbSwing()
	{
		return limbSwing;
	}
	public float getLimbSwingAmount()
	{
		return limbSwingAmount;
	}
	public float getPartialTick()
	{
		return partialTick;
	}
	public AnimationController getController()
	{
		return controller;
	}
	public boolean isWalking()
	{
		return isWalking;
	}
}
