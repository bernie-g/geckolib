/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

public class AnimationPoint {
	/**
	 * The current tick in the animation to lerp from
	 */
	public final double currentTick;
	/**
	 * The tick that the current animation should end at
	 */
	public final double animationEndTick;
	/**
	 * The Animation start value.
	 */
	public final double animationStartValue;
	/**
	 * The Animation end value.
	 */
	public final double animationEndValue;

	/**
	 * The current keyframe.
	 */
	public final KeyFrame keyframe;

	// Remove boxed arguments method, leaving this in place just incase an unforeseen issue arises
	/*public AnimationPoint(KeyFrame keyframe, double currentTick, Double animationEndTick,
			Double animationStartValue, double animationEndValue) {
		this.keyframe = keyframe;
		this.currentTick = currentTick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = animationStartValue;
		this.animationEndValue = animationEndValue;
	}*/

	public AnimationPoint(KeyFrame keyframe, double tick, double animationEndTick, double animationStartValue,
			double animationEndValue) {
		this.keyframe = keyframe;
		this.currentTick = tick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = animationStartValue;
		this.animationEndValue = animationEndValue;
	}

	@Override
	public String toString() {
		return "Tick: " + currentTick + " | End Tick: " + animationEndTick + " | Start Value: " + animationStartValue
				+ " | End Value: " + animationEndValue;
	}
}
