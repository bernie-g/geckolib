/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.model;

import java.util.HashMap;

/**
 * Each entity should have exactly <bold>ONE</bold> AnimationControllerCollection and can add as many animation controllers to the collection as desired.
 */
public class AnimationControllerCollection extends HashMap<String, AnimationController>
{
	protected BoneSnapshotCollection boneSnapshotCollection;

	/**
	 * Instantiates a new Animation controller collection.
	 */
	public AnimationControllerCollection()
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
}
