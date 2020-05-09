package software.bernie.geckolib.animation;

import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.keyframe.KeyFrame;
import software.bernie.geckolib.animation.keyframe.KeyFrameLocation;
import software.bernie.geckolib.animation.keyframe.VectorKeyFrameList;
import software.bernie.geckolib.model.AnimatedModelRenderer;
import software.bernie.geckolib.model.BoneSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Animation utils.
 */
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

	/**
	 * This is the actual function smoothly interpolate (lerp) between keyframes
	 *
	 * @param currentTick            The current tick (usually entity.ticksExisted + partialTicks to make it smoother)
	 * @param animationLengthSeconds The animation's length in seconds
	 * @param animationStartValue The animation's start value
	 * @param animationEndValue   The animation's end value
	 * @return The interpolated value
	 */
	public static float lerpAnimationFloat(float currentTick, float animationLengthSeconds, float animationStartValue, float animationEndValue)
	{
		float seconds = convertTicksToSeconds(currentTick);
		if (seconds > animationLengthSeconds)
		{
			return animationEndValue;
		}
		return MathHelper.lerp(seconds / animationLengthSeconds, animationStartValue,
				animationEndValue) * animationLengthSeconds / animationLengthSeconds;
	}

	/**
	 * Lerp key frames float.
	 *
	 * @param framesIn          The frames
	 * @param ageInTicks      The age in ticks
	 * @param loop            The loop
	 * @param animationLength The animation length
	 * @return the float
	 */
	public static float LerpKeyFrames(List<KeyFrame<Float>> framesIn, float ageInTicks, boolean loop, float animationLength, float speedFactor)
	{
		float animationLengthTicks = convertSecondsToTicks(animationLength);
		KeyFrameLocation<KeyFrame<Float>> frameLocation = getCurrentKeyFrameLocation(framesIn,
				loop ? ageInTicks % animationLengthTicks : ageInTicks);
		KeyFrame<Float> frame = frameLocation.CurrentFrame;
		if(Float.isNaN(animationLength))
		{
			return frame.getEndValue();
		}
		if(frameLocation.CurrentAnimationTick == -1)
		{
			return frame.getEndValue();
		}
		return lerpAnimationFloat(frameLocation.CurrentAnimationTick, frame.getKeyFrameLength(), frame.getStartValue(), frame.getEndValue());
	}

	/*
	Returns the current keyframe object, plus how long the previous keyframes have taken (aka elapsed animation time)
	 */
	private static KeyFrameLocation<KeyFrame<Float>> getCurrentKeyFrameLocation(List<KeyFrame<Float>> frames, float ageInTicks)
	{
		float seconds = convertTicksToSeconds(ageInTicks);
		float totalTimeTracker = 0;
		for (int i = 0; i < frames.size(); i++)
		{
			KeyFrame frame = frames.get(i);
			totalTimeTracker += frame.getKeyFrameLength();
			if (totalTimeTracker >= seconds)
			{
				float tick = ageInTicks - convertSecondsToTicks(totalTimeTracker - frame.getKeyFrameLength());
				return new KeyFrameLocation(frame, tick);
			}
		}
		return new KeyFrameLocation(frames.get(frames.size() - 1), -1);
	}

	/**
	 * Sums the length of the animation
	 *
	 * @param <T>    The keyframe type
	 * @param frames The list of keyframes
	 * @return The animation length in ticks
	 */
	public static <T extends KeyFrame> float getAnimationLengthInTicks(List<T> frames)
	{
		return (float) frames.stream().mapToDouble(x -> convertSecondsToTicks(x.getKeyFrameLength())).sum();
	}

	public static List<KeyFrame<Float>> applySpeedModifier(List<KeyFrame<Float>> keyframes, float speedModifier)
	{
		for(KeyFrame<Float> keyframe : keyframes)
		{
			keyframe.setKeyFrameLength(keyframe.getKeyFrameLength() * speedModifier);
		}

		return keyframes;
	}


}
