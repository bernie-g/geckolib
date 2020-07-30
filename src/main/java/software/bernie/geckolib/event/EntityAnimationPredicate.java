/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.event;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.entity.IAnimatable;

public class EntityAnimationPredicate<T extends IAnimatable> extends AnimationTestPredicate
{
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float partialTick;
	private final boolean isWalking;
	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *  @param entity          the entity
	 * @param animationTick   The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param limbSwing       the limb swing
	 * @param limbSwingAmount the limb swing amount
	 * @param partialTick     the partial tick
	 * @param isWalking
	 */
	public EntityAnimationPredicate(T entity, double animationTick, float limbSwing, float limbSwingAmount, float partialTick, boolean isWalking)
	{
		super(entity, animationTick);
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.partialTick = partialTick;
		this.isWalking = isWalking;
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

	public boolean isWalking()
	{
		return isWalking;
	}
}
