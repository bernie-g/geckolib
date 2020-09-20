/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.core.manager;

import software.bernie.geckolib.core.controller.BaseAnimationController;
import software.bernie.geckolib.core.snapshot.BoneSnapshotCollection;

import java.util.HashMap;

/**
 * Each entity should have exactly <b>ONE</b> EntityAnimationManager and can add as many animation controllers to the collection as desired.
 */
public class AnimationManager extends HashMap<String, BaseAnimationController>
{
	private BoneSnapshotCollection boneSnapshotCollection;
	public float tick;
	public boolean isFirstTick = true;
	private double speedModifier = 1;
	private double resetTickLength = 30;
	public Float startTick;

	/**
	 * Instantiates a new Animation controller collection.
	 */
	public AnimationManager()
	{
		super();
		boneSnapshotCollection = new BoneSnapshotCollection();
	}

	/**
	 * This method is how you register animation controllers, without this, your AnimationPredicate method will never be called
	 *
	 * @param value The value
	 * @return the animation controller
	 */
	public BaseAnimationController addAnimationController(BaseAnimationController value)
	{
		return this.put(value.getName(), value);
	}

	public BoneSnapshotCollection getBoneSnapshotCollection()
	{
		return boneSnapshotCollection;
	}

	public void setBoneSnapshotCollection(BoneSnapshotCollection boneSnapshotCollection)
	{
		this.boneSnapshotCollection = boneSnapshotCollection;
	}

	public void clearSnapshotCache()
	{
		this.boneSnapshotCollection = new BoneSnapshotCollection();
	}

	/**
	 * Sets the speed multipler of how fast the animation goes. This cannot be negative, and the default value is 1.
	 */
	public void setAnimationSpeed(double speed)
	{
		this.speedModifier = speed < 0 ? 0 : speed;
	}

	public double getCurrentAnimationSpeed()
	{
		return this.speedModifier;
	}

	public double getResetSpeed()
	{
		return resetTickLength;
	}

	/**
	 * This is how long it takes for any bones that don't have an animation to revert back to their original position
	 *
	 * @param resetTickLength The amount of ticks it takes to reset. Cannot be negative.
	 */
	public void setResetSpeedInTicks(double resetTickLength)
	{
		this.resetTickLength = resetTickLength < 0 ? 0 : resetTickLength;
	}
}
