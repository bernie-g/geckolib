package software.bernie.geckolib3.core.easing;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Functional interface defining an easing function.<br>
 * {@code value} is the easing value provided from the keyframe's {@link software.bernie.geckolib3.core.keyframe.KeyFrame#easingArgs}
 * <br><br>
 * For more information on easings, see:<br>
 * <a href="https://easings.net/">Easings.net</a><br>
 * <a href="https://cubic-bezier.com">Cubic-Bezier.com</a><br>
 */
@FunctionalInterface
public interface EasingType {
	final Map<String, EasingType> EASING_TYPES = new ConcurrentHashMap<>(64);

	EasingType LINEAR = register("linear", value -> easeIn(EasingType::linear));
	EasingType STEP = register("step", value -> easeIn(step(value)));
	EasingType EASE_IN_SINE = register("easeinsine", value -> easeIn(EasingType::sine));
	EasingType EASE_OUT_SINE = register("easeoutsine", value -> easeOut(EasingType::sine));
	EasingType EASE_IN_OUT_SINE = register("easeinoutsine", value -> easeInOut(EasingType::sine));
	EasingType EASE_IN_QUAD = register("easeinquad", value -> easeIn(EasingType::quadratic));
	EasingType EASE_OUT_QUAD = register("easeoutquad", value -> easeOut(EasingType::quadratic));
	EasingType EASE_IN_OUT_QUAD = register("easeinoutquad", value -> easeInOut(EasingType::quadratic));
	EasingType EASE_IN_CUBIC = register("easeincubic", value -> easeIn(EasingType::cubic));
	EasingType EASE_OUT_CUBIC = register("easeoutcubic", value -> easeOut(EasingType::cubic));
	EasingType EASE_IN_OUT_CUBIC = register("easeinoutcubic", value -> easeInOut(EasingType::cubic));
	EasingType EASE_IN_QUART = register("easeinquart", value -> easeIn(pow(4)));
	EasingType EASE_OUT_QUART = register("easeoutquart", value -> easeOut(pow(4)));
	EasingType EASE_IN_OUT_QUART = register("easeinoutquart", value -> easeInOut(pow(4)));
	EasingType EASE_IN_QUINT = register("easeinquint", value -> easeIn(pow(4)));
	EasingType EASE_OUT_QUINT = register("easeoutquint", value -> easeOut(pow(5)));
	EasingType EASE_IN_OUT_QUINT = register("easeinoutquint", value -> easeInOut(pow(5)));
	EasingType EASE_IN_EXPO = register("easeinexpo", value -> easeIn(EasingType::exp));
	EasingType EASE_OUT_EXPO = register("easeoutexpo", value -> easeOut(EasingType::exp));
	EasingType EASE_IN_OUT_EXPO = register("easeinoutexpo", value -> easeInOut(EasingType::exp));
	EasingType EASE_IN_CIRC = register("easeincirc", value -> easeIn(EasingType::circle));
	EasingType EASE_OUT_CIRC = register("easeoutcirc", value -> easeOut(EasingType::circle));
	EasingType EASE_IN_OUT_CIRC = register("easeinoutcirc", value -> easeInOut(EasingType::circle));
	EasingType EASE_IN_BACK = register("easeinback", value -> easeIn(back(value)));
	EasingType EASE_OUT_BACK = register("easeoutback", value -> easeOut(back(value)));
	EasingType EASE_IN_OUT_BACK = register("easeinoutback", value -> easeInOut(back(value)));
	EasingType EASE_IN_ELASTIC = register("easeinelastic", value -> easeIn(elastic(value)));
	EasingType EASE_OUT_ELASTIC = register("easeoutelastic", value -> easeOut(elastic(value)));
	EasingType EASE_IN_OUT_ELASTIC = register("easeinoutelastic", value -> easeInOut(elastic(value)));
	EasingType EASE_IN_BOUNCE = register("easeinbounce", value -> easeIn(bounce(value)));
	EasingType EASE_OUT_BOUNCE = register("easeoutbounce", value -> easeOut(bounce(value)));
	EasingType EASE_IN_OUT_BOUNCE = register("easeinoutbounce", value -> easeInOut(bounce(value)));

	Double2DoubleFunction buildTransformer(double value);

	/**
	 * Register an {@code EasingType} with Geckolib for handling animation transitions and value curves
	 * @param name The name of the easing type
	 * @param easingType The {@code EasingType} to associate with the given name
	 * @return The {@code EasingType} you registered
	 */
	static EasingType register(String name, EasingType easingType) {
		EASING_TYPES.putIfAbsent(name.toLowerCase(Locale.ROOT).replaceAll("_", ""), easingType);

		return easingType;
	}


	/**
	 * Get an existing {@code EasingType} from a given string, matching the string to its name.
	 * @param name The name of the easing function
	 * @return The relevant {@code EasingType}, or {@link EasingType#LINEAR} if none match
	 */
	static EasingType getEasingTypeFromString(String name) {
		return EASING_TYPES.getOrDefault(name, EasingType.LINEAR);
	}

	// ---> Easing Transition Type Functions <--- //

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

	// ---> Easing Curve Functions <--- //

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

	/**
	 * An exponential function, equivalent to an exponential curve to the {@code n} root.<br>
	 * <code>f(t) = t^n</code>
	 * @param n The exponent
	 */
	static Double2DoubleFunction pow(double n) {
		return t -> Math.pow(t, n);
	}

	// The MIT license notice below applies to the function step
	/**
	 * The MIT License (MIT)
	 *<br><br>
	 * Copyright (c) 2015 Boris Chumichev
	 *<br><br>
	 * Permission is hereby granted, free of charge, to any person obtaining a copy
	 * of this software and associated documentation files (the "Software"), to deal
	 * in the Software without restriction, including without limitation the rights
	 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	 * copies of the Software, and to permit persons to whom the Software is
	 * furnished to do so, subject to the following conditions:
	 *<br><br>
	 * The above copyright notice and this permission notice shall be included in
	 * all copies or substantial portions of the Software.
	 *<br><br>
	 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	 * SOFTWARE.
	 * <br><br>
	 * Returns a stepped value based on the nearest step to the input value.<br>
	 * The size (grade) of the steps depends on the provided value of {@code n}
	 **/
	static Double2DoubleFunction step(double n) {
		if (n < 2)
			throw new IllegalArgumentException("Steps must be >= 2, got: " + n);

		final int steps = (int)n;

		return t -> {
			double result = 0;

			if (t < 0)
				return result;

			double stepLength = (1 / (double)steps);

			if (t > (result = (steps - 1) * stepLength))
				return result;

			int testIndex;
			int leftBorderIndex = 0;
			int rightBorderIndex = steps - 1;

			while (rightBorderIndex - leftBorderIndex != 1) {
				testIndex = leftBorderIndex + (rightBorderIndex - leftBorderIndex) / 2;

				if (t >= testIndex * stepLength) {
					leftBorderIndex = testIndex;
				}
				else {
					rightBorderIndex = testIndex;
				}
			}

			return leftBorderIndex * stepLength;
		};
	}
}
