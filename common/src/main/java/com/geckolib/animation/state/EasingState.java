package com.geckolib.animation.state;

import org.jspecify.annotations.Nullable;
import com.geckolib.animation.object.EasingType;
import com.geckolib.loading.math.MathValue;

import java.util.Arrays;

/// Container object representing the variables used for performing easing operations
///
/// @param easingType The EasingType to use to interpolate
/// @param easingArgs The easing arguments to use for interpolation
/// @param delta The current delta (position) between the start value and the end value
/// @param fromValue The start value to ease from
/// @param toValue The end value to ease to
public record EasingState(EasingType easingType, MathValue[] easingArgs, double delta, double fromValue, double toValue) {
	/// Calculate the interpolated value for this `EasingState` using the provided [ControllerState]
	public double interpolate(ControllerState controllerState) {
		return this.easingType.apply(this, controllerState);
	}

	/// @return The first easing argument from the [#easingArgs] array for the given [ControllerState], or null if not present
	public @Nullable Double getFirstEasingArg(ControllerState controllerState) {
		return this.easingArgs.length == 0 ? null : this.easingArgs[0].get(controllerState);
	}

	@Override
	public String toString() {
        return "EasingType: " + this.easingType +
               " | EasingArgs: " + Arrays.toString(this.easingArgs) +
               " | Delta: " + this.delta +
               " | From: " + this.fromValue +
               " | To: " + this.toValue;
	}
}
