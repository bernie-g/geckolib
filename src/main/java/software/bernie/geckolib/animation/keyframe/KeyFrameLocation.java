package software.bernie.geckolib.animation.keyframe;

public class KeyFrameLocation<T extends KeyFrame>
{
	public T CurrentFrame;

	/*
	This is the combined total time of all the previous keyframes
	 */
	public double CurrentAnimationTick;

	public KeyFrameLocation(T currentFrame, double currentAnimationTick)
	{
		CurrentFrame = currentFrame;
		CurrentAnimationTick = currentAnimationTick;
	}
}
