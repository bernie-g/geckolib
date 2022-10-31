package software.bernie.geckolib3.core.easing;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;

/**
 * Functional interface defining an easing function.<br>
 * For more information on easings, see:<br>
 * <a href="https://easings.net/">Easings.net</a><br>
 * <a href="https://cubic-bezier.com">Cubic-Bezier.com</a><br>
 */
@FunctionalInterface
public interface EasingType {
	EasingType LINEAR = value -> value;
	EasingType STEP = in(step)
	EasingType EASE_IN_SINE = value -> 0;
	EasingType EASE_OUT_SINE = value -> 0;
	EasingType EASE_IN_OUT_SINE = value -> 0;
	EasingType EASE_IN_QUAD = value -> 0;
	EasingType EASE_OUT_QUAD = value -> 0;
	EasingType EASE_IN_OUT_QUAD = value -> 0;
	EasingType EASE_IN_CUBIC = value -> 0;
	EasingType EASE_OUT_CUBIC = value -> 0;
	EasingType EASE_IN_OUT_CUBIC = value -> 0;
	EasingType EASE_IN_QUART = value -> 0;
	EasingType EASE_OUT_QUART = value -> 0;
	EasingType EASE_IN_OUT_QUART = value -> 0;
	EasingType EASE_IN_QUINT = value -> 0;
	EasingType EASE_OUT_QUINT = value -> 0;
	EasingType EASE_IN_OUT_QUINT = value -> 0;
	EasingType EASE_IN_EXPO = value -> 0;
	EasingType EASE_OUT_EXPO = value -> 0;
	EasingType EASE_IN_OUT_EXPO = value -> 0;
	EasingType EASE_IN_CIRC = value -> 0;
	EasingType EASE_OUT_CIRC = value -> 0;
	EasingType EASE_IN_OUT_CIRC = value -> 0;
	EasingType EASE_IN_BACK = value -> 0;
	EasingType EASE_OUT_BACK = value -> 0;
	EasingType EASE_IN_OUT_BACK = value -> 0;
	EasingType EASE_IN_ELASTIC = easeIn(EasingType::elastic)::apply;
	EasingType EASE_OUT_ELASTIC = easeOut(EasingType::elastic)::apply;
	EasingType EASE_IN_OUT_ELASTIC = easeInOut(EasingType::elastic)::apply;
	EasingType EASE_IN_BOUNCE = value -> 0;
	EasingType EASE_OUT_BOUNCE = value -> 0;
	EasingType EASE_IN_OUT_BOUNCE = value -> 0;
	
	double doTransformation(double value);

	// ---> Easing functions <--- //

	/**
	 * Returns an easing function running linearly. Functionally equivalent to no easing
	 */
	static Double2DoubleFunction linear(Double2DoubleFunction function) {
		return function;
	}

	/**
	 * Returns an easing function running forward in time
	 */
	static Double2DoubleFunction easeIn(Double2DoubleFunction function) {
		return function;
	}

	/**
	 * Returns an easing function running backwards in time
	 */
	static Double2DoubleFunction easeOut(Double2DoubleFunction function) {
		return time -> 1 - function.apply(1 - time);
	}

	/**
	 * Returns an easing function that runs equally both forwards and backwards in time based on the halfway point, generating a symmetrical curve.<br>
	 */
	static Double2DoubleFunction easeInOut(Double2DoubleFunction function) {
		return time -> {
			if (time < 0.5d)
				return function.apply(time * 2d) / 2d;

			return 1 - function.apply((1 - time) * 2d) / 2d;
		};
	}

	// ---> Stepping Functions <--- //

	/**
	 * Returns a stepping function that returns 1 for any input value greater than 0, or otherwise returning 0
	 */
	static Double2DoubleFunction stepPositive(Double2DoubleFunction function) {
		return n -> n > 0 ? 1 : 0;
	}

	/**
	 * Returns a stepping function that returns 1 for any input value greater than or equal to 0, or otherwise returning 0
	 */
	static Double2DoubleFunction stepNonNegative(Double2DoubleFunction function) {
		return n -> n >= 0 ? 1 : 0;
	}

	// ---> Mathematical Functions <--- //

	/**
	 * A linear function, equivalent to a null-operation.<br>
	 * {@code f(n) = n}
	 */
	static double linear(double n) {
		return n;
	}

	/**
	 * A quadratic function, equivalent to the square (<i>n</i>^2) of elapsed time.<br>
	 * {@code f(n) = n^2}<br>
	 * <a href="http://easings.net/#easeInQuad">Easings.net#easeInQuad</a>
	 */
	static double quadratic(double n) {
		return n * n;
	}

	/**
	 * A cubic function, equivalent to cube (<i>n</i>^3) of elapsed time.<br>
	 * {@code f(n) = n^3}<br>
	 * <a href="http://easings.net/#easeInCubic">Easings.net#easeInCubic</a>
	 */
	static double cubic(double n) {
		return n * n * n;
	}

	/**
	 * A sinusoidal function, equivalent to a sine curve output.<br>
	 * {@code f(n) = 1 - cos(n * π / 2)}<br>
	 * <a href="http://easings.net/#easeInSine">Easings.net#easeInSine</a>
	 */
	static double sine(double n) {
		return 1 - Math.cos(n * Math.PI / 2f);
	}

	/**
	 * A circular function, equivalent to a normally symmetrical curve.<br>
	 * {@code f(n) = 1 - sqrt(1 - n^2)}<br>
	 * <a href="http://easings.net/#easeInCirc">Easings.net#easeInCirc</a>
	 */
	static double circle(double n) {
		return 1 - Math.sqrt(1 - n * n);
	}

	/**
	 * An exponential function, equivalent to an exponential curve.<br>
	 * {@code f(n) = 2^(10 * (n - 1))}<br>
	 * <a href="http://easings.net/#easeInExpo">Easings.net#easeInExpo</a>
	 */
	static double exp(double n) {
		return Math.pow(2, 10 * (n - 1));
	}

	/**
	 * An elastic function, equivalent to an oscillating curve.<br>
	 * <i>n</i> defines the elasticity of the output.<br>
	 * {@code f(t) = 1 - (cos(t * π) / 2))^3 * cos(t * n * π)}<br>
	 * <a href="http://easings.net/#easeInElastic">Easings.net#easeInElastic</a>
	 */
	static Double2DoubleFunction elastic(double n) {
		return t -> 1 - Math.pow(Math.cos(t * Math.PI / 2f), 3) * Math.cos(t * n * Math.PI);
	}

	/**
	 * A bouncing function, equivalent to a bouncing ball curve.<br>
	 * <i>n</i> defines the bounciness of the output.<br>
	 * Thanks to <b>Waterded#6455</b> for making the bounce adjustable, and <b>GiantLuigi4#6616</b> for additional cleanup.<br>
	 * <a href="http://easings.net/#easeInBounce">Easings.net#easeInBounce</a>
	 */
	static Double2DoubleFunction bounce(double n) {
		Double2DoubleFunction one = x -> 121f / 16f * x * x;
		Double2DoubleFunction two = x -> 121f / 4f * n * Math.pow(x - 6f / 11f, 2) + 1 - n;
		Double2DoubleFunction three = x -> 121 * n * n * Math.pow(x - 9f / 11f, 2) + 1 - n * n;
		Double2DoubleFunction four = x -> 484 * n * n * n * Math.pow(x - 10.5f / 11f, 2) + 1 - n * n * n;

		return t -> Math.min(Math.min(one.apply(t), two.apply(t)), Math.min(three.apply(t), four.apply(t)));
	}

	/**
	 * A negative elastic function, equivalent to inverting briefly before increasing.<br>
	 * <code>f(t) = t^2 * ((n * 1.70158 + 1) * t * n * 1.70158)</code><br>
	 * <a href="https://easings.net/#easeInBack">Easings.net#easeInBack</a>
	 */
	static Double2DoubleFunction back(double n) {
		return t -> t * t * ((n * 1.70158d + 1) * t * n * 1.70158d);
	}

	static Double2DoubleFunction step(double n) {
		if (n < 2)
			throw new IllegalArgumentException("Steps must be >= 2, got: " + n);

		return t -> {
			double result = 0;

			if (t < 0)
				return result;

			if (t > (result = (n - 1) / n))
				return result;


		};

		double[] intervals = stepRange(n);

		return t -> intervals[findIntervalBorderIndex(t, intervals, false)];
	}

	private static double[] stepRange(double steps) {
		double[] stepArray = new double[(int)Math.ceil(steps)];

		for (int i = 0; i < steps; i++) {
			stepArray[i] = i / steps;
		}

		return stepArray;
	}

}
