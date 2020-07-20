/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.keyframe;

import javax.annotation.Nullable;

public class AnimationPoint
{
	/**
	 * The current tick in the animation to lerp from
	 */
	public final Double currentTick;
	/**
	 * The tick that the current animation should end at
	 */
	public final Double animationEndTick;
	/**
	 * The Animation start value.
	 */
	public final Float animationStartValue;
	/**
	 * The Animation end value.
	 */
	public final Float animationEndValue;

	/**
	 * The current keyframe.
	 */
	@Nullable
	public final KeyFrame<Double> keyframe;

	public AnimationPoint(@Nullable KeyFrame<Double> keyframe, Double currentTick, Double animationEndTick, Double animationStartValue, Double animationEndValue)
	{
		this.keyframe = keyframe;
		this.currentTick = currentTick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = animationStartValue.floatValue();
		this.animationEndValue = animationEndValue.floatValue();
	}

	public AnimationPoint(@Nullable KeyFrame<Double> keyframe, Double currentTick, Double animationEndTick, Float animationStartValue, Float animationEndValue)
	{
		this.currentTick = currentTick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = animationStartValue;
		this.animationEndValue = animationEndValue;
		this.keyframe = keyframe;
	}

	public AnimationPoint(@Nullable KeyFrame<Double> keyframe, Double currentTick, Double animationEndTick, Float animationStartValue, Double animationEndValue)
	{
		this.keyframe = keyframe;
		this.currentTick = currentTick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = animationStartValue;
		this.animationEndValue = animationEndValue.floatValue();
	}

	@Override
	public String toString()
	{
		return "Tick: " + currentTick + " | End Tick: " + animationEndTick + " | Start Value: " + animationStartValue + " | End Value: " + animationEndValue;
	}
}
