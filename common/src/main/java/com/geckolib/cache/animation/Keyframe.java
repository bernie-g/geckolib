package com.geckolib.cache.animation;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import com.geckolib.animation.object.EasingType;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.value.Variable;
import com.geckolib.util.MiscUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/// Animation keyframe data
///
/// @param startTime The time (in seconds) the keyframe starts at
/// @param length The length (in seconds) the keyframe lasts for
/// @param startValue The value to start the keyframe's transformation with
/// @param endValue The value to end the keyframe's transformation with
/// @param easingType The `EasingType` to use for transformations
/// @param easingArgs The arguments to provide to the easing calculation
public record Keyframe(double startTime, double length, MathValue startValue, MathValue endValue, EasingType easingType, MathValue[] easingArgs) {
	public Keyframe(double startTime, double length, MathValue startValue, MathValue endValue) {
		this(startTime, length, startValue, endValue, EasingType.LINEAR);
	}

	public Keyframe(double startTime, double length, MathValue startValue, MathValue endValue, EasingType easingType) {
		this(startTime, length, startValue, endValue, easingType, new MathValue[0]);
	}

	public Keyframe(double startTime, double length, MathValue startValue, MathValue endValue, EasingType easingType, List<MathValue> easingArgs) {
		this(startTime, length, startValue, endValue, easingType, easingArgs.toArray(new MathValue[0]));
	}

	/// Extract and collect all [Variable]s used in this keyframe
	public Set<Variable> getUsedVariables() {
		Set<Variable> usedVariables = new ReferenceOpenHashSet<>();

		if (this.startValue.isMutable())
			usedVariables.addAll(this.startValue.getUsedVariables());

		if (this.endValue.isMutable())
			usedVariables.addAll(this.endValue.getUsedVariables());

		for (MathValue easingArg : this.easingArgs) {
			if (easingArg.isMutable())
				usedVariables.addAll(easingArg.getUsedVariables());
		}

		return usedVariables;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.length, this.startValue, this.endValue, this.easingType, Arrays.hashCode(this.easingArgs));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

        Keyframe other = (Keyframe)obj;

        if (!MiscUtil.areFloatsEqual(this.length, other.length))
            return false;

        if (!this.startValue.equals(other.startValue) || !this.endValue.equals(other.endValue))
            return false;

        if (this.easingType != other.easingType)
            return false;

        return Arrays.equals(this.easingArgs, other.easingArgs);
	}
}
