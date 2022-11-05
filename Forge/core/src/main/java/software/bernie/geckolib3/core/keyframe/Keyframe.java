/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib3.core.animation.EasingType;

import java.util.List;
import java.util.Objects;

/**
 * Animation keyframe data
 * @param length The length (in ticks) the keyframe lasts for
 * @param startValue The value to start the keyframe's transformation with
 * @param endValue The value to end the keyframe's transformation with
 * @param easingType The {@code EasingType} to use for transformations
 * @param easingArgs The arguments to provide to the easing calculation
 * @param <T>
 */
public record Keyframe<T>(double length, T startValue, T endValue, EasingType easingType, List<T> easingArgs) {
	public Keyframe(double length, T startValue, T endValue) {
		this(length, startValue, endValue, EasingType.LINEAR);
	}

	public Keyframe(double length, T startValue, T endValue, EasingType easingType) {
		this(length, startValue, endValue, easingType, new ObjectArrayList<>(0));
	}

	public Double getLength() {
		return length;
	}

	public T getStartValue() {
		return startValue;
	}

	public T getEndValue() {
		return endValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.length, this.startValue, this.endValue, this.easingType, this.easingArgs);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (getClass() != obj.getClass())
			return false;

		return hashCode() == obj.hashCode();
	}
}
