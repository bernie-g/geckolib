package software.bernie.geckolib.animation;

import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.keyframe.KeyFrame;
import software.bernie.geckolib.animation.keyframe.KeyFrameLocation;
import software.bernie.geckolib.animation.keyframe.RotationKeyFrame;

import java.util.List;

public class AnimationUtils
{
	public static float convertTicksToSeconds(float ticks)
	{
		return ticks / 20;
	}

	public static float convertSecondsToTicks(float seconds)
	{
		return seconds * 20;
	}

	public static float lerpAnimationFloat(float currentTick, float animationLengthSeconds, float animationStartRotation, float animationEndRotation)
	{
		float seconds = convertTicksToSeconds(currentTick);
		if (seconds > animationLengthSeconds)
		{
			return animationEndRotation;
		}
		return MathHelper.lerp(seconds / animationLengthSeconds, animationStartRotation,
				animationEndRotation) * animationLengthSeconds / animationLengthSeconds;
	}

	public static float lerpAnimationDegrees(float currentTick, float animationLengthSeconds, float animationStartRotation, float animationEndRotation)
	{
		return lerpAnimationFloat(currentTick, animationLengthSeconds, (float) Math.toRadians(animationStartRotation),
				(float) Math.toRadians(animationEndRotation));
	}

	public static float LerpRotationKeyFrames(List<RotationKeyFrame> frames, float ageInTicks, boolean loop, float animationLength)
	{
		float animationLengthTicks = convertSecondsToTicks(animationLength);
		KeyFrameLocation<RotationKeyFrame> frameLocation = getCurrentKeyFrameLocation(frames,
				loop ? ageInTicks % animationLengthTicks : ageInTicks);
		KeyFrame<Float> frame = frameLocation.CurrentFrame;
		return lerpAnimationDegrees(frameLocation.CurrentAnimationTick, frame.getKeyFrameLength(), frame.getStartValue(), frame.getEndValue());
	}

	public static <T extends KeyFrame<Float>> float LerpKeyFrames(List<T> frames, float ageInTicks, boolean loop, float animationLength)
	{
		float animationLengthTicks = convertSecondsToTicks(animationLength);
		KeyFrameLocation<T> frameLocation = getCurrentKeyFrameLocation(frames,
				loop ? ageInTicks % animationLengthTicks : ageInTicks);
		T frame = frameLocation.CurrentFrame;
		return lerpAnimationFloat(frameLocation.CurrentAnimationTick, frame.getKeyFrameLength(), frame.getStartValue(), frame.getEndValue());
	}

	/*
	Returns the current keyframe object, plus how long the previous keyframes have taken (aka elapsed animation time)
	 */
	private static <T extends KeyFrame<?>> KeyFrameLocation<T> getCurrentKeyFrameLocation(List<T> frames, float ageInTicks)
	{
		float seconds = convertTicksToSeconds(ageInTicks);
		float totalTimeTracker = 0;
		for (int i = 0; i < frames.size(); i++)
		{
			KeyFrame frame = frames.get(i);
			totalTimeTracker += frame.getKeyFrameLength();
			if (totalTimeTracker > seconds)
			{
				float tick = ageInTicks - ((totalTimeTracker - frame.getKeyFrameLength()) * 20);
				if(tick == 6)
				{
					int sdf = 9;
				}
				return new KeyFrameLocation(frame, tick);
			}
		}
		return new KeyFrameLocation(frames.get(frames.size() - 1), ageInTicks - totalTimeTracker);
	}

	public static <T extends KeyFrame> float getAnimationLengthInTicks(List<T> frames)
	{
		return (float) frames.stream().mapToDouble(x -> convertSecondsToTicks(x.getKeyFrameLength())).sum();
	}

	public static <X, T extends KeyFrame<X>> X getLastKeyFrameEndValue(List<T> frames)
	{
		return frames.get(frames.size() - 1).getEndValue();
	}

	public static <X, T extends KeyFrame<X>> X getSecondKeyFrameStartValue(List<T> frames)
	{
		return frames.get(1).getStartValue();
	}
}
