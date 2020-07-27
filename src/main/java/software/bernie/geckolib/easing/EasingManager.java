package software.bernie.geckolib.easing;

import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.util.Memoizer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.DoubleStream;

public class EasingManager
{
	static class EasingFunctionArgs {
		public final EasingType easingType;
		@Nullable
		public final Double arg0;

		public EasingFunctionArgs(EasingType easingType, @Nullable Double arg0) {
			this.easingType = easingType;
			this.arg0 = arg0;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			EasingFunctionArgs that = (EasingFunctionArgs) o;
			return easingType == that.easingType &&
					Objects.equals(arg0, that.arg0);
		}

		@Override
		public int hashCode() {
			return Objects.hash(easingType, arg0);
		}
	}

	public static double ease(double number, EasingType easingType, @Nullable List<Double> easingArgs)
	{
		Double firstArg = easingArgs == null || easingArgs.size() < 1 ? null : easingArgs.get(0);
		return getEasingFunction.apply(new EasingFunctionArgs(easingType, firstArg)).apply(number);
	}

	// Memoize easing functions so we don't need to create new ones from HOFs every frame
	static Function<Double, Double> quart = poly(4);
	static Function<Double, Double> quint = poly(5);
	static Function<EasingFunctionArgs, Function<Double, Double>> getEasingFunction =
			Memoizer.memoize(EasingManager::getEasingFuncImpl);

	// Don't call this, use getEasingFunction instead as that function is the memoized version
	static Function<Double, Double> getEasingFuncImpl(EasingFunctionArgs args) {
		switch (args.easingType)
		{
			default:
			case Linear:
				return in(EasingManager::linear);
			case Step:
				return in(step(args.arg0));
			case EaseInSine:
				return in(EasingManager::sin);
			case EaseOutSine:
				return out(EasingManager::sin);
			case EaseInOutSine:
				return inOut(EasingManager::sin);
			case EaseInQuad:
				return in(EasingManager::quad);
			case EaseOutQuad:
				return out(EasingManager::quad);
			case EaseInOutQuad:
				return inOut(EasingManager::quad);
			case EaseInCubic:
				return in(EasingManager::cubic);
			case EaseOutCubic:
				return out(EasingManager::cubic);
			case EaseInOutCubic:
				return inOut(EasingManager::cubic);
			case EaseInExpo:
				return in(EasingManager::exp);
			case EaseOutExpo:
				return out(EasingManager::exp);
			case EaseInOutExpo:
				return inOut(EasingManager::exp);
			case EaseInCirc:
				return in(EasingManager::circle);
			case EaseOutCirc:
				return out(EasingManager::circle);
			case EaseInOutCirc:
				return inOut(EasingManager::circle);
			case EaseInQuart:
				return in(quart);
			case EaseOutQuart:
				return out(quart);
			case EaseInOutQuart:
				return inOut(quart);
			case EaseInQuint:
				return in(quint);
			case EaseOutQuint:
				return out(quint);
			case EaseInOutQuint:
				return inOut(quint);
			case EaseInBack:
				return in(back(args.arg0));
			case EaseOutBack:
				return out(back(args.arg0));
			case EaseInOutBack:
				return inOut(back(args.arg0));
			case EaseInElastic:
				return in(elastic(args.arg0));
			case EaseOutElastic:
				return out(elastic(args.arg0));
			case EaseInOutElastic:
				return inOut(elastic(args.arg0));
			case EaseInBounce:
				return in(bounce(args.arg0));
			case EaseOutBounce:
				return out(bounce(args.arg0));
			case EaseInOutBounce:
				return inOut(bounce(args.arg0));
		}
	}

	// The MIT license notice below applies to the easing functions below except for bounce and step
	/**
	 * Copyright (c) Facebook, Inc. and its affiliates.
	 *
	 * This source code is licensed under the MIT license found in the
	 * LICENSE file in the root directory of this source tree.
	 */

	/**
	 * Runs an easing function forwards.
	 */
	static Function<Double, Double> in(Function<Double, Double> easing)
	{
		return easing;
	}

	/**
	 * Runs an easing function backwards.
	 */
	static Function<Double, Double> out(Function<Double, Double> easing)
	{
		return t -> 1 - easing.apply(1 - t);
	}

	/**
	 * Makes any easing function symmetrical. The easing function will run
	 * forwards for half of the duration, then backwards for the rest of the
	 * duration.
	 */
	static Function<Double, Double> inOut(Function<Double, Double> easing)
	{
		return t ->
		{
			if (t < 0.5)
			{
				return easing.apply(t * 2) / 2;
			}
			return 1 - easing.apply((1 - t) * 2) / 2;
		};
	}

	/**
	 * A stepping function, returns 1 for any positive value of `n`.
	 */
	static Function<Double, Double> step0()
	{
		return n -> n > 0 ? 1D : 0;
	}

	/**
	 * A stepping function, returns 1 if `n` is greater than or equal to 1.
	 */
	static Function<Double, Double> step1()
	{
		return n -> n >= 1D ? 1D : 0;
	}

	/**
	 * A linear function, `f(t) = t`. Position correlates to elapsed time one to
	 * one.
	 * <p>
	 * http://cubic-bezier.com/#0,0,1,1
	 */
	static double linear(double t)
	{
		return t;
	}

	/**
	 * A simple inertial interaction, similar to an object slowly accelerating to
	 * speed.
	 *
	 * http://cubic-bezier.com/#.42,0,1,1
	 */
	// static ease(t) {
	// 		if (!ease) {
	// 				ease = Easing.bezier(0.42, 0, 1, 1);
	// 		}
	// 		return ease(t);
	// }

	/**
	 * A quadratic function, `f(t) = t * t`. Position equals the square of elapsed
	 * time.
	 * <p>
	 * http://easings.net/#easeInQuad
	 */
	static double quad(double t)
	{
		return t * t;
	}

	/**
	 * A cubic function, `f(t) = t * t * t`. Position equals the cube of elapsed
	 * time.
	 * <p>
	 * http://easings.net/#easeInCubic
	 */
	static double cubic(double t)
	{
		return t * t * t;
	}

	/**
	 * A power function. Position is equal to the Nth power of elapsed time.
	 * <p>
	 * n = 4: http://easings.net/#easeInQuart
	 * n = 5: http://easings.net/#easeInQuint
	 */
	static Function<Double, Double> poly(double n)
	{
		return (t) -> Math.pow(t, n);
	}

	/**
	 * A sinusoidal function.
	 * <p>
	 * http://easings.net/#easeInSine
	 */
	static double sin(double t)
	{
		return 1 - MathHelper.cos((float) ((t * Math.PI) / 2));
	}

	/**
	 * A circular function.
	 * <p>
	 * http://easings.net/#easeInCirc
	 */
	static double circle(double t)
	{
		return 1 - MathHelper.sqrt(1 - t * t);
	}

	/**
	 * An exponential function.
	 * <p>
	 * http://easings.net/#easeInExpo
	 */
	static double exp(double t)
	{
		return Math.pow(2, 10 * (t - 1));
	}

	/**
	 * A simple elastic interaction, similar to a spring oscillating back and
	 * forth.
	 * <p>
	 * Default bounciness is 1, which overshoots a little bit once. 0 bounciness
	 * doesn't overshoot at all, and bounciness of N > 1 will overshoot about N
	 * times.
	 * <p>
	 * http://easings.net/#easeInElastic
	 */
	static Function<Double, Double> elastic(Double bounciness)
	{
		double p = (bounciness == null ? 1 : bounciness) * Math.PI;
		return t -> 1 - Math.pow(MathHelper.cos((float) ((t * Math.PI) / 2)), 3) * MathHelper.cos((float) (t * p));
	}


	/**
	 * Use with `Animated.parallel()` to create a simple effect where the object
	 * animates back slightly as the animation starts.
	 * <p>
	 * Wolfram Plot:
	 * <p>
	 * - http://tiny.cc/back_default (s = 1.70158, default)
	 */
	static Function<Double, Double> back(Double s)
	{
		double p = s == null ? 1.70158 : s * 1.70158;
		return t -> t * t * ((p + 1) * t - p);
	}

	/**
	 * Provides a simple bouncing effect.
	 * <p>
	 * Props to Waterded#6455 for making the bounce adjustable and GiantLuigi4#6616 for helping clean it up
	 * using min instead of ternaries
	 * http://easings.net/#easeInBounce
	 */
	static Function<Double, Double> bounce(Double s)
	{
		double k = s == null ? 0.5 : s;
		Function<Double, Double> q = x -> (121.0 / 16.0) * x * x;
		Function<Double, Double> w = x -> ((121.0 / 4.0) * k) * Math.pow(x - (6.0 / 11.0), 2) + 1 - k;
		Function<Double, Double> r = x -> 121 * k * k * Math.pow(x - (9.0 / 11.0), 2) + 1 - k * k;
		Function<Double, Double> t = x -> 484 * k * k * k * Math.pow(x - (10.5 / 11.0), 2) + 1 - k * k * k;
		return x -> min(q.apply(x), w.apply(x), r.apply(x), t.apply(x));
	}

	static Function<Double, Double> step(Double stepArg) {
		int steps = stepArg != null ? stepArg.intValue() : 2;
		double[] intervals = stepRange(steps);
		return t -> intervals[findIntervalBorderIndex(t, intervals, false)];
	}

	static double min(double a, double b, double c, double d)
	{
		return Math.min(Math.min(a, b), Math.min(c, d));
	}

	// The MIT license notice below applies to the function findIntervalBorderIndex
	/* The MIT License (MIT)

	Copyright (c) 2015 Boris Chumichev

	Permission is hereby granted, free of charge, to any person obtaining a copy of
	this software and associated documentation files (the "Software"), to deal in
	the Software without restriction, including without limitation the rights to
	use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
	the Software, and to permit persons to whom the Software is furnished to do so,
	subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
	FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
	COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
	CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

	/**
	 *
	 * Utilizes bisection method to search an interval to which
	 * point belongs to, then returns an index of left or right
	 * border of the interval
	 *
	 * @param {Number} point
	 * @param {Array} intervals
	 * @param {Boolean} useRightBorder
	 * @returns {Number}
	 */
	static int findIntervalBorderIndex(double point, double[] intervals, boolean useRightBorder) {
		//If point is beyond given intervals
		if (point < intervals[0])
			return 0;
		if (point > intervals[intervals.length - 1])
			return intervals.length - 1;
		//If point is inside interval
		//Start searching on a full range of intervals
		int indexOfNumberToCompare = 0;
		int leftBorderIndex = 0;
		int rightBorderIndex = intervals.length - 1;
		//Reduce searching range till it find an interval point belongs to using binary search
		while (rightBorderIndex - leftBorderIndex != 1) {
			indexOfNumberToCompare = leftBorderIndex + (rightBorderIndex - leftBorderIndex) / 2;
			if (point >= intervals[indexOfNumberToCompare]) {
				leftBorderIndex = indexOfNumberToCompare;
			} else {
				rightBorderIndex = indexOfNumberToCompare;
			}
		}
		return useRightBorder ? rightBorderIndex : leftBorderIndex;
	}

	static double[] stepRange(int steps) {
		final double stop = 1;
		if (steps < 2) throw new IllegalArgumentException("steps must be > 2, got:" + steps);
		double stepLength = stop / (double)steps;
		// There must be an easier way of doing this but I just don't care
		AtomicInteger i = new AtomicInteger();
		return DoubleStream.generate(() -> i.getAndIncrement() * stepLength)
				.limit(steps)
				.toArray();
	};
}
