package software.bernie.geckolib.animation.state;

import software.bernie.geckolib.animation.object.EasingType;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.Arrays;

/// Container object representing the variables used for performing easing operations
///
/// @param easingType The EasingType to use to interpolate
/// @param easingArgs The easing arguments to use for interpolation
/// @param delta The current delta (position) between the start value and the end value
/// @param fromValue The start value to ease from
/// @param toValue The end value to ease to
public record EasingState(EasingType easingType, MathValue[] easingArgs, double delta, double fromValue, double toValue) {
	@Override
	public String toString() {
        return "EasingType: " + this.easingType +
               " | EasingArgs: " + Arrays.toString(this.easingArgs) +
               " | Delta: " + this.delta +
               " | From: " + this.fromValue +
               " | To: " + this.toValue;
	}
}
