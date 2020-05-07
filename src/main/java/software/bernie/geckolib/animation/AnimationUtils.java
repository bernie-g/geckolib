package software.bernie.geckolib.animation;

import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.keyframe.KeyFrame;
import software.bernie.geckolib.animation.keyframe.KeyFrameLocation;
import software.bernie.geckolib.animation.keyframe.VectorKeyFrameList;
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
		//GeckoLib.LOGGER.info(currentTick);
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
	public static float LerpRotationKeyFrames(List<KeyFrame<Float>> frames, float ageInTicks, boolean loop, float animationLength)
	{
		float animationLengthTicks = convertSecondsToTicks(animationLength);
		KeyFrameLocation<KeyFrame<Float>> frameLocation = getCurrentKeyFrameLocation(frames,
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
		float value = lerpAnimationDegrees(frameLocation.CurrentAnimationTick, frame.getKeyFrameLength(),
				frame.getStartValue(), frame.getEndValue());
		if(Float.isNaN(value))
		{
			GeckoLib.LOGGER.error("Somehow got a NaN during lerpKeyframes. Blame gecko and cry. Ageinticks: " + ageInTicks + ". Loop:" + loop + ". Animationlength: " + animationLength);
		}
		return value;
	}

	/**
	 * Lerp key frames float.
	 *
	 * @param frames          The frames
	 * @param ageInTicks      The age in ticks
	 * @param loop            The loop
	 * @param animationLength The animation length
	 * @return the float
	 */
	public static float LerpKeyFrames(List<KeyFrame<Float>> frames, float ageInTicks, boolean loop, float animationLength)
	{
		float animationLengthTicks = convertSecondsToTicks(animationLength);
		KeyFrameLocation<KeyFrame<Float>> frameLocation = getCurrentKeyFrameLocation(frames,
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

	public static VectorKeyFrameList<KeyFrame<Float>> applySpeedModifier(VectorKeyFrameList<KeyFrame<Float>> keyframes, float speedModifier)
	{
		for(KeyFrame<Float> keyframe : keyframes.xKeyFrames)
		{
			keyframe.setKeyFrameLength(keyframe.getKeyFrameLength() * speedModifier);
		}
		for(KeyFrame<Float> keyframe : keyframes.yKeyFrames)
		{
			keyframe.setKeyFrameLength(keyframe.getKeyFrameLength() * speedModifier);
		}
		for(KeyFrame<Float> keyframe : keyframes.zKeyFrames)
		{
			keyframe.setKeyFrameLength(keyframe.getKeyFrameLength() * speedModifier);
		}
		return keyframes;
	}


}
