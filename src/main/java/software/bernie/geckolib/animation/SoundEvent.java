/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation;

import net.minecraft.entity.Entity;
import software.bernie.geckolib.animation.model.AnimationController;

public class SoundEvent<T extends Entity> extends AnimationEvent<T>
{
	public final String sound;

	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *
	 * @param entity        the entity
	 * @param animationTick The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param sound        The name of the sound to play
	 * @param controller    the controller
	 */
	public SoundEvent(T entity, double animationTick, String sound, AnimationController controller)
	{
		super(entity, animationTick, controller);
		this.sound = sound;
	}
}
