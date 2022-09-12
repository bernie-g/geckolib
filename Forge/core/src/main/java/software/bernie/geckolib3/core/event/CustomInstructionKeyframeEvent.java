/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.event;

import software.bernie.geckolib3.core.controller.AnimationController;

public class CustomInstructionKeyframeEvent<T> extends KeyframeEvent<T>
{
	public final String instructions;

	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *
	 * @param entity        the entity
	 * @param animationTick The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param instructions  A list of all the custom instructions. In blockbench, each line in the custom instruction box is a separate instruction.
	 * @param controller    the controller
	 */
	public CustomInstructionKeyframeEvent(T entity, double animationTick, String instructions, AnimationController controller)
	{
		super(entity, animationTick, controller);
		this.instructions = instructions;
	}
}
