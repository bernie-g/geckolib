/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */
package software.bernie.geckolib.entity;

import software.bernie.geckolib.manager.EntityAnimationManager;

/**
 * This interface must be applied to any Entity that uses an AnimatedEntityModel
 */
public interface IAnimatedEntity
{
	/**
	 * This method MUST return an Animation Manager, otherwise no animations will be played.
	 *
	 * @return the animation controllers
	 */
	EntityAnimationManager getAnimationManager();
}
