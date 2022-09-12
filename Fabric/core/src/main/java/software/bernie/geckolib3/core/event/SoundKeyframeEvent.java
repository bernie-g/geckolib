/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.event;

import software.bernie.geckolib3.core.controller.AnimationController;

public class SoundKeyframeEvent<T> extends KeyframeEvent<T> {
	public final String sound;

	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *
	 * @param entity        the entity
	 * @param animationTick The amount of ticks that have passed in either the
	 *                      current transition or animation, depending on the
	 *                      controller's AnimationState.
	 * @param sound         The name of the sound to play
	 * @param controller    the controller
	 */
	public SoundKeyframeEvent(T entity, double animationTick, String sound, AnimationController controller) {
		super(entity, animationTick, controller);
		this.sound = sound;
	}
}
