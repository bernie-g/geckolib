package software.bernie.geckolib.animation.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import software.bernie.geckolib.loading.math.value.Variable;

import java.util.List;
import java.util.Set;

/**
 * Stores a triplet of {@link Keyframe Keyframes} in an ordered stack
 */
public record KeyframeStack<T extends Keyframe<?>>(List<T> xKeyframes, List<T> yKeyframes, List<T> zKeyframes) {
	public KeyframeStack() {
		this(new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>());
	}

	/**
	 * Extract and collect all {@link Variable}s used in this keyframe stack
	 */
	public Set<Variable> getUsedVariables() {
		Set<Variable> usedVariables = new ReferenceOpenHashSet<>();

		for (T keyframe : this.xKeyframes) {
			usedVariables.addAll(keyframe.getUsedVariables());
		}

		for (T keyframe : this.yKeyframes) {
			usedVariables.addAll(keyframe.getUsedVariables());
		}

		for (T keyframe : this.zKeyframes) {
			usedVariables.addAll(keyframe.getUsedVariables());
		}

		return usedVariables;
	}

	public double getLastKeyframeTime() {
		double xTime = 0;
		double yTime = 0;
		double zTime = 0;

		for (T frame : xKeyframes()) {
			xTime += frame.length();
		}

		for (T frame : yKeyframes()) {
			yTime += frame.length();
		}

		for (T frame : zKeyframes()) {
			zTime += frame.length();
		}

		return Math.max(xTime, Math.max(yTime, zTime));
	}
}
