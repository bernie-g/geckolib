package com.geckolib.animation.object;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.animation.state.EasingState;
import com.geckolib.cache.animation.Keyframe;
import com.geckolib.loading.math.MathParser;
import com.geckolib.loading.math.MathValue;
import com.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Functional interface defining an easing function
///
/// `value` is the easing value provided from the keyframe's [Keyframe#easingArgs()]
///
/// @see <a href="https://easings.net/">Easings.net</a>
/// @see <a href="https://cubic-bezier.com">Cubic-Bezier.com</a>
@FunctionalInterface
public interface EasingType {
	Map<String, EasingType> EASING_TYPES = new ConcurrentHashMap<>(64);

	EasingType LINEAR				= register("linear", registerSimple("none", EasingType::linear));
	EasingType STEP					= register("step", EasingType::step);
	EasingType EASE_IN_SINE			= registerSimple("easeinsine", EasingType::sine);
	EasingType EASE_OUT_SINE		= registerSimple("easeoutsine", easeOut(EasingType::sine));
	EasingType EASE_IN_OUT_SINE		= registerSimple("easeinoutsine", easeInOut(EasingType::sine));
	EasingType EASE_IN_QUAD			= registerSimple("easeinquad", EasingType::quadratic);
	EasingType EASE_OUT_QUAD		= registerSimple("easeoutquad", easeOut(EasingType::quadratic));
	EasingType EASE_IN_OUT_QUAD		= registerSimple("easeinoutquad", easeInOut(EasingType::quadratic));
	EasingType EASE_IN_CUBIC		= registerSimple("easeincubic", EasingType::cubic);
	EasingType EASE_OUT_CUBIC		= registerSimple("easeoutcubic", easeOut(EasingType::cubic));
	EasingType EASE_IN_OUT_CUBIC	= registerSimple("easeinoutcubic", easeInOut(EasingType::cubic));
	EasingType EASE_IN_QUART		= registerSimple("easeinquart", pow(4));
	EasingType EASE_OUT_QUART		= registerSimple("easeoutquart", easeOut(pow(4)));
	EasingType EASE_IN_OUT_QUART	= registerSimple("easeinoutquart", easeInOut(pow(4)));
	EasingType EASE_IN_QUINT		= registerSimple("easeinquint", pow(4));
	EasingType EASE_OUT_QUINT		= registerSimple("easeoutquint", easeOut(pow(5)));
	EasingType EASE_IN_OUT_QUINT	= registerSimple("easeinoutquint", easeInOut(pow(5)));
	EasingType EASE_IN_EXPO			= registerSimple("easeinexpo", EasingType::exp);
	EasingType EASE_OUT_EXPO		= registerSimple("easeoutexpo", easeOut(EasingType::exp));
	EasingType EASE_IN_OUT_EXPO		= registerSimple("easeinoutexpo", easeInOut(EasingType::exp));
	EasingType EASE_IN_CIRC			= registerSimple("easeincirc", EasingType::circle);
	EasingType EASE_OUT_CIRC		= registerSimple("easeoutcirc", easeOut(EasingType::circle));
	EasingType EASE_IN_OUT_CIRC		= registerSimple("easeinoutcirc", easeInOut(EasingType::circle));
	EasingType EASE_IN_BACK			= register("easeinback", EasingType::back);
	EasingType EASE_OUT_BACK		= register("easeoutback", arg -> easeOut(back(arg)));
	EasingType EASE_IN_OUT_BACK		= register("easeinoutback", arg -> easeInOut(back(arg)));
	EasingType EASE_IN_ELASTIC		= register("easeinelastic", EasingType::elastic);
	EasingType EASE_OUT_ELASTIC		= register("easeoutelastic", arg -> easeOut(elastic(arg)));
	EasingType EASE_IN_OUT_ELASTIC	= register("easeinoutelastic", arg -> easeInOut(elastic(arg)));
	EasingType EASE_IN_BOUNCE		= register("easeinbounce", EasingType::bounce);
	EasingType EASE_OUT_BOUNCE		= register("easeoutbounce", arg -> easeOut(bounce(arg)));
	EasingType EASE_IN_OUT_BOUNCE	= register("easeinoutbounce", arg -> easeInOut(bounce(arg)));
	CatmullRomEasing CATMULLROM		= register("catmullrom", new CatmullRomEasing());

	/// Construct an interpolation function for this `EasingType`, optionally using an additional easing argument, provided from the animation JSON
	Double2DoubleFunction buildTransformer(@Nullable Double easingArg);

	/// Calculate the interpolated value for the provided [EasingState] and [ControllerState]
	default double apply(EasingState easingState, ControllerState controllerState) {
		if (easingState.delta() >= 1)
			return easingState.toValue();

		return Mth.lerp(buildTransformer(easingState.getFirstEasingArg(controllerState)).apply(easingState.delta()), easingState.fromValue(), easingState.toValue());
	}

	/// Modify the baked [Keyframe] array if necessary to apply keyframe changes for this `EasingType`
	default void modifyKeyframes(Keyframe[] keyframes, int currentFrameIndex, MathParser mathParser) {}

	/// Register an `EasingType` with GeckoLib for handling animation transitions and value curves
	///
	/// **<u>MUST be called during mod construct</u>**
	///
	/// It is recommended you don't call this directly and instead call it via [GeckoLibUtil#addCustomEasingType(String, EasingType)]
	///
	/// @param name The name of the easing type
	/// @param easingType The `EasingType` to associate with the given name
	/// @return The `EasingType` you registered
	static <T extends EasingType> T register(String name, T easingType) {
		EASING_TYPES.putIfAbsent(name, easingType);

		return easingType;
	}

	/// Register a zero-parameter `EasingType` with GeckoLib for handling animation transitions and value curves
	///
	/// **<u>MUST be called during mod construct</u>**
	///
	/// It is recommended you don't call this directly and instead call it via [GeckoLibUtil#addCustomSimpleEasingType(String, Double2DoubleFunction)]
	///
	/// @param name The name of the easing type
	/// @param function The interpolation function for this easing type
	/// @return The `EasingType` you registered
	static EasingType registerSimple(String name, Double2DoubleFunction function) {
		return register(name, (EasingType)_ -> function);
	}

	/// Get an existing `EasingType` from a given string, matching the string to its name
	///
	/// @param name The name of the easing function
	/// @return The relevant `EasingType`, or [EasingType#LINEAR] if none match
	static EasingType fromString(String name) {
		return EASING_TYPES.getOrDefault(name, EasingType.LINEAR);
	}

	//<editor-fold defaultstate="collapsed" desc="<Internal Implementation>">

	/// Returns an easing function running linearly. Functionally equivalent to no easing
	static Double2DoubleFunction linear(Double2DoubleFunction function) {
		return function;
	}

	/// Performs an approximation of Catmull-Rom interpolation, used to get smooth interpolated motion between keyframes
	///
	/// Given that by necessity, this only accepts a single argument, making this only technically a spline interpolation for n=1
	///
	/// <a href="https://pub.dev/documentation/latlong2/latest/spline/CatmullRom-class.html">CatmullRom#position</a>
	static double catmullRom(double n) {
		return 0.5d * (2d * (n + 1d) + 2d
					   + (2d * n - 5d * (n + 1d) + 4d * (n + 2d) - (n + 3d))
					   + (3d * (n + 1d) - n - 3d * (n + 2d) + (n + 3d)));
	}

	/// Returns an easing function running backwards in time
	static Double2DoubleFunction easeOut(Double2DoubleFunction function) {
		return time -> 1 - function.apply(1 - time);
	}

	/// Returns an easing function that runs equally both forwards and backwards in time based on the halfway point, generating a symmetrical curve
	static Double2DoubleFunction easeInOut(Double2DoubleFunction function) {
		return time -> {
			if (time < 0.5d)
				return function.apply(time * 2d) / 2d;

			return 1 - function.apply((1 - time) * 2d) / 2d;
		};
	}

	// ---> Stepping Functions <--- //

	/// Returns a stepping function that returns 1 for any input value greater than 0, or otherwise returning 0
	static Double2DoubleFunction stepPositive(Double2DoubleFunction function) {
		return n -> n > 0 ? 1 : 0;
	}

	/// Returns a stepping function that returns 1 for any input value greater than or equal to 0, or otherwise returning 0
	static Double2DoubleFunction stepNonNegative(Double2DoubleFunction function) {
		return n -> n >= 0 ? 1 : 0;
	}

	// ---> Mathematical Functions <--- //

	/// A linear function, equivalent to a null-operation
	///
	/// `f(n) = n`
	static double linear(double n) {
		return n;
	}

	/// A quadratic function, equivalent to the square (_n_^2) of elapsed time
	///
	/// `f(n) = n^2`
	///
	/// <a href="http://easings.net/#easeInQuad">Easings.net#easeInQuad</a>
	static double quadratic(double n) {
		return n * n;
	}

	/// A cubic function, equivalent to cube (_n_^3) of elapsed time
	///
	/// `f(n) = n^3`
	///
	/// <a href="http://easings.net/#easeInCubic">Easings.net#easeInCubic</a>
	static double cubic(double n) {
		return n * n * n;
	}

	/// A sinusoidal function, equivalent to a sine curve output
	///
	/// `f(n) = 1 - cos(n * π / 2)`
	///
	/// <a href="http://easings.net/#easeInSine">Easings.net#easeInSine</a>
	static double sine(double n) {
		return 1 - Math.cos(n * Math.PI / 2f);
	}

	/// A circular function, equivalent to a normally symmetrical curve
	///
	/// `f(n) = 1 - sqrt(1 - n^2)`
	///
	/// <a href="http://easings.net/#easeInCirc">Easings.net#easeInCirc</a>
	static double circle(double n) {
		return 1 - Math.sqrt(1 - n * n);
	}

	/// An exponential function, equivalent to an exponential curve
	///
	/// `f(n) = 2^(10 * (n - 1))`
	///
	/// <a href="http://easings.net/#easeInExpo">Easings.net#easeInExpo</a>
	static double exp(double n) {
		return Math.pow(2, 10 * (n - 1));
	}

	// ---> Easing Curve Functions <--- //

	/// An elastic function, equivalent to an oscillating curve
	///
	/// _n_ defines the elasticity of the output
	///
	/// `f(t) = 1 - (cos(t * π) / 2))^3 * cos(t * n * π)`
	///
	/// <a href="http://easings.net/#easeInElastic">Easings.net#easeInElastic</a>
	static Double2DoubleFunction elastic(@Nullable Double n) {
		double n2 = n == null ? 1 : n;

		return t -> 1 - Math.pow(Math.cos(t * Math.PI / 2f), 3) * Math.cos(t * n2 * Math.PI);
	}

	/// A bouncing function, equivalent to a bouncing ball curve
	///
	/// _n_ defines the bounciness of the output
	///
	/// Thanks to **Waterded#6455** for making the bounce adjustable, and **GiantLuigi4#6616** for additional cleanup
	///
	/// <a href="http://easings.net/#easeInBounce">Easings.net#easeInBounce</a>
	static Double2DoubleFunction bounce(@Nullable Double n) {
		final double n2 = n == null ? 0.5d : n;

		Double2DoubleFunction one = x -> 121f / 16f * x * x;
		Double2DoubleFunction two = x -> 121f / 4f * n2 * Math.pow(x - 6f / 11f, 2) + 1 - n2;
		Double2DoubleFunction three = x -> 121 * n2 * n2 * Math.pow(x - 9f / 11f, 2) + 1 - n2 * n2;
		Double2DoubleFunction four = x -> 484 * n2 * n2 * n2 * Math.pow(x - 10.5f / 11f, 2) + 1 - n2 * n2 * n2;

		return t -> Math.min(Math.min(one.apply(t), two.apply(t)), Math.min(three.apply(t), four.apply(t)));
	}

	/// A negative elastic function, equivalent to inverting briefly before increasing
	///
	/// `f(t) = t^2 * ((n * 1.70158 + 1) * t - n * 1.70158)`
	///
	/// <a href="https://easings.net/#easeInBack">Easings.net#easeInBack</a>
	static Double2DoubleFunction back(@Nullable Double n) {
		final double n2 = n == null ? 1.70158d : n * 1.70158d;

		return t -> t * t * ((n2 + 1) * t - n2);
	}

	/// An exponential function, equivalent to an exponential curve to the `n` root
	///
	/// `f(t) = t^n`
	///
	/// @param n The exponent
	static Double2DoubleFunction pow(double n) {
		return t -> Math.pow(t, n);
	}

	// The MIT licence notice below applies to the function step
	/// The MIT License (MIT)
	///
	///
	/// Copyright (c) 2015 Boris Chumichev
	///
	///
	/// Permission is hereby granted, free of charge, to any person obtaining a copy
	/// of this software and associated documentation files (the "Software"), to deal
	/// in the Software without restriction, including without limitation the rights
	/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	/// copies of the Software, and to permit persons to whom the Software is
	/// furnished to do so, subject to the following conditions:
	///
	///
	/// The above copyright notice and this permission notice shall be included in
	/// all copies or substantial portions of the Software.
	///
	///
	/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	/// SOFTWARE.
	///
	///
	/// Returns a stepped value based on the nearest step to the input value.
	/// The size (grade) of the steps depends on the provided value of `n`
	static Double2DoubleFunction step(@Nullable Double n) {
		double n2 = n == null ? 2 : n;

		if (n2 < 2)
			throw new IllegalArgumentException("Steps must be >= 2, got: " + n2);

		final int steps = (int)n2;

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

	/// Custom EasingType implementation required for special-handling of spline-based interpolation
	class CatmullRomEasing implements EasingType {
		/// Generates a value from a given Catmull-Rom spline range with Centripetal parameterization (alpha=0.5)
		///
		/// Per standard implementation, this generates a spline curve over control points p1-p2, with p0 and p3
		/// acting as curve anchors.
		/// We then apply the delta to determine the point on the generated spline to return.
		///
		/// Functionally equivalent to [Mth#catmullrom(float, float, float, float, float)]
		///
		/// @see <a href="https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline">Wikipedia</a>
		public static double getPointOnSpline(double delta, double p0, double p1, double p2, double p3) {
			return 0.5d * (2d * p1 + (p2 - p0) * delta +
						   (2d * p0 - 5d * p1 + 4d * p2 - p3) * delta * delta +
						   (3d * p1 - p0 - 3d * p2 + p3) * delta * delta * delta);
		}

		/// Modify the baked [Keyframe] array if necessary to apply keyframe changes for this `EasingType`
		@Override
		public void modifyKeyframes(Keyframe[] keyframes, int currentFrameIndex, MathParser mathParser) {
			final Keyframe currentFrame = keyframes[currentFrameIndex];

			keyframes[currentFrameIndex] = new Keyframe(currentFrame.startTime(), currentFrame.length(), currentFrame.startValue(), currentFrame.endValue(), currentFrame.easingType(),
														ObjectArrayList.of(
																currentFrameIndex == 0 ? currentFrame.startValue() : keyframes[currentFrameIndex - 1].endValue(),
																currentFrameIndex + 1 >= keyframes.length ? currentFrame.endValue() : keyframes[currentFrameIndex + 1].endValue()));
		}

		/// Construct an interpolation function for this `EasingType`, optionally using an additional easing argument, provided from the animation JSON
		@Override
		public Double2DoubleFunction buildTransformer(@Nullable Double easingArg) {
			return easeInOut(EasingType::catmullRom);
		}

		/// Calculate the interpolated value for the provided [EasingState] and [ControllerState]
		@Override
		public double apply(EasingState easingState, ControllerState controllerState) {
			if (easingState.delta() >= 1)
				return easingState.toValue();

			final MathValue[] easingArgs = easingState.easingArgs();

			if (easingArgs.length < 2)
				return Mth.lerp(buildTransformer(easingState.getFirstEasingArg(controllerState)).apply(easingState.delta()), easingState.fromValue(), easingState.toValue());

			return getPointOnSpline(easingState.delta(), easingArgs[0].get(controllerState), easingState.fromValue(), easingState.toValue(), easingArgs[1].get(controllerState));
		}
	}
	//</editor-fold>
}