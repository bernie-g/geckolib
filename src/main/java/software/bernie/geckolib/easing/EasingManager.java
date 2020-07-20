package software.bernie.geckolib.easing;

import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class EasingManager
{
	public static double ease(double number, EasingType easingType, @Nullable List<Double> easingArgs)
	{
		Function<Double, Double> quart = poly(4);
		Function<Double, Double> quint = poly(5);

		Double firstArg = easingArgs == null || easingArgs.size() < 1 ? null : easingArgs.get(0);
		Double secondArg = easingArgs == null || easingArgs.size() < 2 ? null : easingArgs.get(1);
		Double thirdArg = easingArgs == null || easingArgs.size() < 3 ? null : easingArgs.get(2);

		switch (easingType)
		{
			case Linear:
				return linear().apply(number);
			case EaseInSine:
				return in(EasingManager::sin).apply(number);
			case EaseOutSine:
				return out(EasingManager::sin).apply(number);
			case EaseInOutSine:
				return inOut(EasingManager::sin).apply(number);
			case EaseInQuad:
				return in(EasingManager::quad).apply(number);
			case EaseOutQuad:
				return out(EasingManager::quad).apply(number);
			case EaseInOutQuad:
				return inOut(EasingManager::quad).apply(number);
			case EaseInCubic:
				return in(EasingManager::cubic).apply(number);
			case EaseOutCubic:
				return out(EasingManager::cubic).apply(number);
			case EaseInOutCubic:
				return inOut(EasingManager::cubic).apply(number);
			case EaseInExpo:
				return in(EasingManager::exp).apply(number);
			case EaseOutExpo:
				return out(EasingManager::exp).apply(number);
			case EaseInOutExpo:
				return inOut(EasingManager::exp).apply(number);
			case EaseInCirc:
				return in(EasingManager::circle).apply(number);
			case EaseOutCirc:
				return out(EasingManager::circle).apply(number);
			case EaseInOutCirc:
				return inOut(EasingManager::circle).apply(number);
			case EaseInQuart:
				return in(quart).apply(number);
			case EaseOutQuart:
				return out(quart).apply(number);
			case EaseInOutQuart:
				return inOut(quart).apply(number);
			case EaseInQuint:
				return in(quint).apply(number);
			case EaseOutQuint:
				return out(quint).apply(number);
			case EaseInOutQuint:
				return inOut(quint).apply(number);
			case EaseInBack:
				return in(back(firstArg)).apply(number);
			case EaseOutBack:
				return out(back(firstArg)).apply(number);
			case EaseInOutBack:
				return inOut(back(firstArg)).apply(number);
			case EaseInElastic:
				return in(elastic(firstArg)).apply(number);
			case EaseOutElastic:
				return out(elastic(firstArg)).apply(number);
			case EaseInOutElastic:
				return inOut(elastic(firstArg)).apply(number);
			case EaseInBounce:
				return in(bounce(firstArg)).apply(number);
			case EaseOutBounce:
				return out(bounce(firstArg)).apply(number);
			case EaseInOutBounce:
				return inOut(bounce(firstArg)).apply(number);
			default:
				return number;
		}
	}

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
	static Function<Double, Double> linear()
	{
		return Function.identity();
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
		Double p = (bounciness == null ? 1 : bounciness) * Math.PI;
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
		Double p = s == null ? 1.70158 : s;
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
		Function<Double, Double> q = x -> (121 / 16) * x * x;
		Function<Double, Double> w = x -> ((121 / 4) * k) * Math.pow(x - (6 / 11), 2) + 1 - k;
		Function<Double, Double> r = x -> 121 * k * k * Math.pow(x - (9 / 11), 2) + 1 - k * k;
		Function<Double, Double> t = x -> 484 * k * k * k * Math.pow(x - (10.5 / 11), 2) + 1 - k * k * k;
		return x -> min(q.apply(x), w.apply(x), r.apply(x), t.apply(x));
	}

	static double min(double a, double b, double c, double d)
	{
		return Math.min(Math.min(a, b), Math.min(c, d));
	}

}
