package software.bernie.geckolib.animation;

import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.keyframe.KeyFrame;
import software.bernie.geckolib.animation.keyframe.KeyFrameLocation;
import software.bernie.geckolib.animation.keyframe.RotationKeyFrame;
import software.bernie.geckolib.model.AnimatedModelRenderer;

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
	 * Linearly interpolates between two rotation values at a specified tick.
	 *
	 * @param currentTick            The current tick (usually entity.ticksExisted + partialTicks to make it smoother)
	 * @param animationLengthSeconds The animation's length in seconds
	 * @param animationStartRotation The animation's start rotation
	 * @param animationEndRotation   The animation's end rotation
	 * @return The interpolated value (**In radians because the ModelRenderer#translateRotate takes radians**)
	 */
	public static float lerpAnimationDegrees(float currentTick, float animationLengthSeconds, float animationStartRotation, float animationEndRotation)
	{
		return lerpAnimationFloat(currentTick, animationLengthSeconds, (float) Math.toRadians(animationStartRotation),
				(float) Math.toRadians(animationEndRotation));
	}

	/**
	 * TODO
	 *
	 * @param frames          The frames
	 * @param ageInTicks      The age in ticks
	 * @param loop            The loop
	 * @param animationLength The animation length
	 * @return the float
	 */
	public static float LerpRotationKeyFrames(List<RotationKeyFrame> frames, float ageInTicks, boolean loop, float animationLength)
	{
		float animationLengthTicks = convertSecondsToTicks(animationLength);
		KeyFrameLocation<RotationKeyFrame> frameLocation = getCurrentKeyFrameLocation(frames,
				loop ? ageInTicks % animationLengthTicks : ageInTicks);
		KeyFrame<Float> frame = frameLocation.CurrentFrame;
		return lerpAnimationDegrees(frameLocation.CurrentAnimationTick, frame.getKeyFrameLength(), frame.getStartValue(), frame.getEndValue());
	}

	/**
	 * Lerp key frames float.
	 *
	 * @param <T>             The type parameter
	 * @param frames          The frames
	 * @param ageInTicks      The age in ticks
	 * @param loop            The loop
	 * @param animationLength The animation length
	 * @return the float
	 */
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
				float tick = ageInTicks - convertSecondsToTicks(totalTimeTracker - frame.getKeyFrameLength());
				return new KeyFrameLocation(frame, tick);
			}
		}
		return new KeyFrameLocation(frames.get(frames.size() - 1), ageInTicks - totalTimeTracker);
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

	public static boolean isBonePartOfAnimation(AnimatedModelRenderer modelRenderer, Animation animation)
	{
		return animation.boneAnimations.stream().anyMatch(x -> x.boneName == modelRenderer.getModelRendererName());
	}

}
