package software.bernie.geckolib.cache.animation;

import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.LoopType;
import software.bernie.geckolib.cache.animation.keyframeevent.CustomInstructionKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.SoundKeyframeData;
import software.bernie.geckolib.loading.math.value.Variable;

import java.util.Set;

/// A compiled animation instance for use by the [AnimationController]
///
/// Modifications or extensions of a compiled Animation are not supported, and therefore an instance of `Animation` is considered final and immutable
@SuppressWarnings("ClassCanBeRecord")
public class Animation {
	protected final String name;
	protected final double length;
	protected final LoopType loopType;
	protected final BoneAnimation[] boneAnimations;
	protected final Set<Variable> usedVariables;
	protected final KeyframeMarkers keyframeMarkers;

	public Animation(String name, double length, LoopType loopType, BoneAnimation[] boneAnimations, Set<Variable> usedVariables, KeyframeMarkers keyframeMarkers) {
		this.name = name;
		this.length = length;
		this.loopType = loopType;
		this.boneAnimations = boneAnimations;
		this.usedVariables = usedVariables;
		this.keyframeMarkers = keyframeMarkers;
	}

	/// @return The name of the animation, as defined in the animation .json
	public String name() {
		return this.name;
	}

	/// @return The length of the animation (in seconds)
	public double length() {
		return this.length;
	}

	/// @return The default loop type of this animation
	public LoopType loopType() {
		return this.loopType;
	}

	/// @return The keyframes of this animation, organized by bone groups
	public BoneAnimation[] boneAnimations() {
		return this.boneAnimations;
	}

	/// @return The [Variable]s used by this animation
	public Set<Variable> usedVariables() {
		return this.usedVariables;
	}

	/// @return The keyframe instruction markers for this animation, as defined in the animation .json
	public KeyframeMarkers keyframeMarkers() {
		return this.keyframeMarkers;
	}

	/// Create a new Animation instance from the given values, automatically compiling the [#usedVariables] list
	///
	/// @param name The name of the animation
	/// @param length The length (in seconds) of the animation
	/// @param loopType The type of looping this animation should have once finished
	/// @param boneAnimations The keyframe stacks for each bone in this animation
	/// @param keyframeMarkers Any custom keyframe instructions this animation contains
	public static Animation create(String name, double length, LoopType loopType, BoneAnimation[] boneAnimations, KeyframeMarkers keyframeMarkers) {
		Set<Variable> usedVariables = new ReferenceOpenHashSet<>();

		for (BoneAnimation boneAnimation : boneAnimations) {
			usedVariables.addAll(boneAnimation.getUsedVariables());
		}

		return new Animation(name, length, loopType, boneAnimations, new ReferenceArraySet<>(usedVariables), keyframeMarkers);
	}

	/// Container record for the various keyframe marker types for an animation
	public record KeyframeMarkers(SoundKeyframeData[] sounds, ParticleKeyframeData[] particles, CustomInstructionKeyframeData[] customInstructions) {
		public static final KeyframeMarkers EMPTY = new KeyframeMarkers(new SoundKeyframeData[0], new ParticleKeyframeData[0], new CustomInstructionKeyframeData[0]);

        /// @return Whether there are any keyframe markers of any type
        public boolean isEmpty() {
            return this.sounds.length == 0 && this.particles.length == 0 && this.customInstructions.length == 0;
        }
    }

	@ApiStatus.Internal
	public static Animation generateWaitAnimation(double length) {
		return new Animation(RawAnimation.Stage.WAIT, length, LoopType.PLAY_ONCE, new BoneAnimation[0], ReferenceOpenHashSet.of(),
                             new KeyframeMarkers(new SoundKeyframeData[0], new ParticleKeyframeData[0], new CustomInstructionKeyframeData[0]));
	}
}
