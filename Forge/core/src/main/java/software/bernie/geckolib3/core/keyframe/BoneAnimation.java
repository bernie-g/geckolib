/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import com.eliotlash.mclib.math.IValue;

/**
 * A record of a deserialized animation for a given bone.<br>
 * Responsible for holding the various {@link Keyframe Keyframes} for the bone's animation transformations
 * @param boneName The name of the bone as listed in the {@code animation.json}
 * @param rotationKeyFrames The deserialized rotation {@code Keyframe} stack
 * @param positionKeyFrames The deserialized position {@code Keyframe} stack
 * @param scaleKeyFrames The deserialized scale {@code Keyframe} stack
 */
public record BoneAnimation(String boneName,
							KeyframeStack<Keyframe<IValue>> rotationKeyFrames,
							KeyframeStack<Keyframe<IValue>> positionKeyFrames,
							KeyframeStack<Keyframe<IValue>> scaleKeyFrames) {
	/**
	 * Get the name of the {@link software.bernie.geckolib3.core.animatable.model.GeoBone} relevant to this animation
	 */
	public String getBoneName() {
		return this.boneName;
	}

	/**
	 * Gets the {@link KeyframeStack} responsible for the rotation {@link Keyframe} transformations
	 */
	public KeyframeStack<Keyframe<IValue>> getRotationKeyframes() {
		return this.rotationKeyFrames;
	}

	/**
	 * Gets the {@link KeyframeStack} responsible for the position {@link Keyframe} transformations
	 */
	public KeyframeStack<Keyframe<IValue>> getPositionKeyframes() {
		return this.rotationKeyFrames;
	}

	/**
	 * Gets the {@link KeyframeStack} responsible for the scale {@link Keyframe} transformations
	 */
	public KeyframeStack<Keyframe<IValue>> getScale() {
		return this.rotationKeyFrames;
	}
}
