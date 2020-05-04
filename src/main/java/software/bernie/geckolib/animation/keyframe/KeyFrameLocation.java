package software.bernie.geckolib.animation.keyframe;

public class KeyFrameLocation<T extends KeyFrame>
{
	public T CurrentFrame;

	/*
	This is the combined total time of all the previous keyframes
	 */
	public float CurrentAnimationTick;

	public KeyFrameLocation(T currentFrame, float currentAnimationTick)
	{
		CurrentFrame = currentFrame;
		CurrentAnimationTick = currentAnimationTick;
	}
}
