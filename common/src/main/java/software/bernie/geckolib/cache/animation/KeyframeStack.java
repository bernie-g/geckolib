package software.bernie.geckolib.cache.animation;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import software.bernie.geckolib.loading.math.value.Variable;

import java.util.List;
import java.util.Set;

/**
 * Stores a triplet of {@link Keyframe Keyframes} in an ordered stack
 */
public record KeyframeStack(Keyframe[] xKeyframes, Keyframe[] yKeyframes, Keyframe[] zKeyframes) {
	public static final KeyframeStack EMPTY = new KeyframeStack(new Keyframe[0], new Keyframe[0], new Keyframe[0]);

    public KeyframeStack(List<Keyframe> xKeyframes, List<Keyframe> yKeyframes, List<Keyframe> zKeyframes) {
        this(xKeyframes.toArray(new Keyframe[0]), yKeyframes.toArray(new Keyframe[0]), zKeyframes.toArray(new Keyframe[0]));
    }

	/**
	 * Extract and collect all {@link Variable}s used in this keyframe stack
	 */
	public Set<Variable> getUsedVariables() {
		Set<Variable> usedVariables = new ReferenceOpenHashSet<>();

		for (Keyframe keyframe : this.xKeyframes) {
			usedVariables.addAll(keyframe.getUsedVariables());
		}

		for (Keyframe keyframe : this.yKeyframes) {
			usedVariables.addAll(keyframe.getUsedVariables());
		}

		for (Keyframe keyframe : this.zKeyframes) {
			usedVariables.addAll(keyframe.getUsedVariables());
		}

		return usedVariables;
	}

	public double getTotalKeyframeTime() {
        double time = 0;

        if (this.xKeyframes.length > 0) {
            Keyframe xKeyframe = this.xKeyframes[this.xKeyframes.length - 1];

            time = Math.max(time, xKeyframe.startTime() + xKeyframe.length());
        }

        if (this.yKeyframes.length > 0) {
            Keyframe yKeyframe = this.yKeyframes[this.yKeyframes.length - 1];

            time = Math.max(time, yKeyframe.startTime() + yKeyframe.length());
        }

        if (this.zKeyframes.length > 0) {
            Keyframe zKeyframe = this.zKeyframes[this.zKeyframes.length - 1];

            time = Math.max(time, zKeyframe.startTime() + zKeyframe.length());
        }

        return time;
	}
}
