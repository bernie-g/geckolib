package software.bernie.geckolib.animation.keyframe;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.value.Variable;

import java.util.Set;

/**
 * A record of a deserialized animation for a given bone
 * <p>
 * Responsible for holding the various {@link Keyframe Keyframes} for the bone's animation transformations
 *
 * @param boneName The name of the bone as listed in the {@code animation.json}
 * @param rotationKeyFrames The deserialized rotation {@code Keyframe} stack
 * @param positionKeyFrames The deserialized position {@code Keyframe} stack
 * @param scaleKeyFrames The deserialized scale {@code Keyframe} stack
 */
public record BoneAnimation(String boneName,
							KeyframeStack<Keyframe<MathValue>> rotationKeyFrames,
							KeyframeStack<Keyframe<MathValue>> positionKeyFrames,
							KeyframeStack<Keyframe<MathValue>> scaleKeyFrames) {
	/**
	 * Extract and collect all {@link Variable}s used in this bone animation
	 */
	public Set<Variable> getUsedVariables() {
		Set<Variable> usedVariables = new ReferenceOpenHashSet<>();

		usedVariables.addAll(this.rotationKeyFrames.getUsedVariables());
		usedVariables.addAll(this.positionKeyFrames.getUsedVariables());
		usedVariables.addAll(this.scaleKeyFrames.getUsedVariables());

		return usedVariables;
	}
}
