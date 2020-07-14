/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.manager;

import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.snapshot.BoneSnapshotCollection;

import java.util.HashMap;

/**
 * Each entity should have exactly <bold>ONE</bold> EntityAnimationManager and can add as many animation controllers to the collection as desired.
 */
public class EntityAnimationManager extends HashMap<String, AnimationController>
{
	private BoneSnapshotCollection boneSnapshotCollection;
	public float tick;
	public boolean isFirstTick = true;
	private double speedModifier = 1;


	/**
	 * Instantiates a new Animation controller collection.
	 */
	public EntityAnimationManager()
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
	public AnimationController addAnimationController(AnimationController value)
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


	public void setAnimationSpeed(double speed)
	{
		this.speedModifier = speed;
	}

	public double getCurrentAnimationSpeed()
	{
		return this.speedModifier;
	}
}
