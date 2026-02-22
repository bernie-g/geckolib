package com.geckolib.cache.animation;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import com.geckolib.loading.math.value.Variable;

import java.util.Set;

/// A record of a deserialized animation for a given bone
///
/// Responsible for holding the various [Keyframes][Keyframe] for the bone's animation transformations
///
/// @param boneName The name of the bone as listed in the `animation.json`
/// @param rotationKeyFrames The deserialized rotation `Keyframe` stack
/// @param positionKeyFrames The deserialized position `Keyframe` stack
/// @param scaleKeyFrames The deserialized scale `Keyframe` stack
public record BoneAnimation(String boneName, KeyframeStack rotationKeyFrames, KeyframeStack positionKeyFrames, KeyframeStack scaleKeyFrames) {
	/// Extract and collect all [Variable]s used in this bone animation
	public Set<Variable> getUsedVariables() {
		Set<Variable> usedVariables = new ReferenceOpenHashSet<>();

		usedVariables.addAll(this.rotationKeyFrames.getUsedVariables());
		usedVariables.addAll(this.positionKeyFrames.getUsedVariables());
		usedVariables.addAll(this.scaleKeyFrames.getUsedVariables());

		return usedVariables;
	}
}
