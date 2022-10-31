/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import java.util.List;
import java.util.Objects;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import software.bernie.geckolib3.core.easing.EasingType;

public class KeyFrame<T> {
	private Double length;
	private T startValue;
	private T endValue;
	public EasingType easingType = EasingType.Linear;
	public List<Double> easingArgs = new DoubleArrayList();

	public KeyFrame(Double length, T startValue, T endValue) {
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
	}

	public KeyFrame(Double length, T startValue, T endValue, EasingType easingType) {
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
		this.easingType = easingType;
	}

	public KeyFrame(Double length, T startValue, T endValue, EasingType easingType, List<Double> easingArgs) {
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
		this.easingType = easingType;
		this.easingArgs = easingArgs;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public T getStartValue() {
		return startValue;
	}

	public void setStartValue(T startValue) {
		this.startValue = startValue;
	}

	public T getEndValue() {
		return endValue;
	}

	public void setEndValue(T endValue) {
		this.endValue = endValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(length, startValue, endValue);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof KeyFrame && hashCode() == obj.hashCode();
	}
}
