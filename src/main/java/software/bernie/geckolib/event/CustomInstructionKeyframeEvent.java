/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.event;

import software.bernie.geckolib.animation.controller.AnimationController;

import java.util.List;

public class CustomInstructionKeyframeEvent<T> extends AnimationEvent<T>
{
	public final List<String> instructions;

	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *
	 * @param entity        the entity
	 * @param animationTick The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param instructions  A list of all the custom instructions. In blockbench, each line in the custom instruction box is a separate instruction.
	 * @param controller    the controller
	 */
	public CustomInstructionKeyframeEvent(T entity, double animationTick, List<String> instructions, AnimationController controller)
	{
		super(entity, animationTick, controller);
		this.instructions = instructions;
	}
}
