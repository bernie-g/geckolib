/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.keyframe;

/**
 * This class stores a location in an animation, and returns the keyframe that should be executed.
 *
 */
public class KeyFrameLocation<T extends KeyFrame>
{
	/**
	 * The curent frame.
	 */
	public T CurrentFrame;

	/**
	 * This is the combined total time of all the previous keyframes
	 */
	public double CurrentAnimationTick;

	/**
	 * Instantiates a new Key frame location.
	 *
	 * @param currentFrame         the current frame
	 * @param currentAnimationTick the current animation tick
	 */
	public KeyFrameLocation(T currentFrame, double currentAnimationTick)
	{
		CurrentFrame = currentFrame;
		CurrentAnimationTick = currentAnimationTick;
	}
}
