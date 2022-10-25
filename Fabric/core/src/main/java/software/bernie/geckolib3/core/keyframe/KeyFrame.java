/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import com.eliotlash.mclib.math.IValue;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import software.bernie.geckolib3.core.easing.EasingType;

import java.util.List;
import java.util.Objects;

public class KeyFrame {
	private double length;
	private IValue startValue;
	private IValue endValue;
	public EasingType easingType = EasingType.Linear;
	public DoubleList easingArgs = new DoubleArrayList();

	public KeyFrame(double length, IValue startValue, IValue endValue) {
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
	}

	public KeyFrame(double length, IValue startValue, IValue endValue, EasingType easingType) {
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
		this.easingType = easingType;
	}

	public KeyFrame(double length, IValue startValue, IValue endValue, EasingType easingType, List<IValue> easingArgs) {
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
		this.easingType = easingType;

		for (IValue easing : easingArgs) {
			this.easingArgs.add(easing.get());
		}
	}

	public double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public IValue getStartValue() {
		return startValue;
	}

	public void setStartValue(IValue startValue) {
		this.startValue = startValue;
	}

	public IValue getEndValue() {
		return endValue;
	}

	public void setEndValue(IValue endValue) {
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
