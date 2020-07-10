package software.bernie.geckolib.easing;

import net.minecraft.util.math.MathHelper;

public class EasingManager
{
	public static double ease(double number, EasingType easingType)
	{
		switch (easingType)
		{
			case EaseInSine:
				return easeInSine(number);
			case EaseOutSine:
				return easeOutSine(number);
			case EaseInOutSine:
				return easeInOutSine(number);
			case EaseInQuad:
				return easeInQuad(number);
			case EaseOutQuad:
				return easeOutQuad(number);
			case EaseInOutQuad:
				return easeInOutQuad(number);
			case EaseInCubic:
				return easeInCubic(number);
			case EaseOutCubic:
				return easeOutCubic(number);
			case EaseInOutCubic:
				return easeInOutCubic(number);
			case EaseInQuart:
				return easeInQuart(number);
			case EaseOutQuart:
				return easeOutQuart(number);
			case EaseInOutQuart:
				return easeInOutQuart(number);
			case EaseInQuint:
				return easeInQuint(number);
			case EaseOutQuint:
				return easeOutQuint(number);
			case EaseInOutQuint:
				return easeInOutQuint(number);
			case EaseInExpo:
				return easeInExpo(number);
			case EaseOutExpo:
				return easeOutExpo(number);
			case EaseInOutExpo:
				return easeInOutExpo(number);
			case EaseInCirc:
				return easeInCirc(number);
			case EaseOutCirc:
				return easeOutCirc(number);
			case EaseInOutCirc:
				return easeInOutCirc(number);
			case EaseInBack:
				return easeInBack(number);
			case EaseOutBack:
				return easeOutBack(number);
			case EaseInOutBack:
				return easeInOutBack(number);
			case EaseInElastic:
				return easeInElastic(number);
			case EaseOutElastic:
				return easeOutElastic(number);
			case EaseInOutElastic:
				return easeInOutElastic(number);
			case EaseInBounce:
				return easeInBounce(number);
			case EaseOutBounce:
				return easeOutBounce(number);
			case EaseInOutBounce:
				return easeInOutBounce(number);
			default:
				return number;
		}
	}

	public static double easeInSine(double x)
	{
		return 1 - MathHelper.cos((float) ((x * Math.PI) / 2));
	}

	public static double easeOutSine(double x)
	{
		return MathHelper.sin((float) ((x * Math.PI) / 2));
	}

	public static double easeInOutSine(double x)
	{
		return -(MathHelper.cos((float) (Math.PI * x)) - 1) / 2;
	}

	public static double easeInQuad(double x)
	{
		return x * x;
	}

	public static double easeOutQuad(double x)
	{
		return 1 - (1 - x) * (1 - x);
	}

	public static double easeInOutQuad(double x)
	{
		return x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2;
	}

	public static double easeInCubic(double x)
	{
		return x * x * x;
	}

	public static double easeOutCubic(double x)
	{
		return 1 - Math.pow(1 - x, 3);
	}

	public static double easeInOutCubic(double x)
	{
		return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
	}

	public static double easeInQuart(double x)
	{
		return x * x * x * x;
	}

	public static double easeOutQuart(double x)
	{
		return 1 - Math.pow(1 - x, 4);
	}

	public static double easeInOutQuart(double x)
	{
		return x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
	}

	public static double easeInQuint(double x)
	{
		return x * x * x * x * x;
	}

	public static double easeOutQuint(double x)
	{
		return 1 - Math.pow(1 - x, 5);
	}

	public static double easeInOutQuint(double x)
	{
		return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
	}

	public static double easeInExpo(double x)
	{
		return x == 0 ? 0 : Math.pow(2, 10 * x - 10);
	}

	public static double easeOutExpo(double x)
	{
		return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
	}

	public static double easeInOutExpo(double x)
	{
		return x == 0
				? 0
				: x == 1
				? 1
				: x < 0.5 ? Math.pow(2, 20 * x - 10) / 2
				: (2 - Math.pow(2, -20 * x + 10)) / 2;
	}

	public static double easeInCirc(double x)
	{
		return 1 - MathHelper.sqrt(1 - Math.pow(x, 2));
	}

	public static double easeOutCirc(double x)
	{
		return MathHelper.sqrt(1 - Math.pow(x - 1, 2));
	}

	public static double easeInOutCirc(double x)
	{
		return x < 0.5
				? (1 - MathHelper.sqrt(1 - Math.pow(2 * x, 2))) / 2
				: (MathHelper.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
	}

	public static double easeInBack(double x)
	{
		double c1 = 1.70158;
		double c3 = c1 + 1;

		return c3 * x * x * x - c1 * x * x;
	}

	public static double easeOutBack(double x)
	{
		double c1 = 1.70158;
		double c3 = c1 + 1;

		return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
	}

	public static double easeInOutBack(double x)
	{
		double c1 = 1.70158;
		double c2 = c1 * 1.525;

		return x < 0.5
				? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
				: (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
	}

	public static double easeInElastic(double x)
	{
		double c4 = (2 * Math.PI) / 3;
		return x == 0
				? 0
				: x == 1
				? 1
				: -Math.pow(2, 10 * x - 10) * MathHelper.sin((float) ((x * 10 - 10.75) * c4));
	}

	public static double easeOutElastic(double x)
	{
		double c4 = (2 * Math.PI) / 3;
		return x == 0
				? 0
				: x == 1
				? 1
				: Math.pow(2, -10 * x) * MathHelper.sin((float) ((x * 10 - 0.75) * c4)) + 1;
	}

	public static double easeInOutElastic(double x)
	{
		double c5 = (2 * Math.PI) / 4.5;
		return x == 0
				? 0
				: x == 1
				? 1
				: x < 0.5
				? -(Math.pow(2, 20 * x - 10) * MathHelper.sin((float) ((20 * x - 11.125) * c5))) / 2
				: (Math.pow(2, -20 * x + 10) * MathHelper.sin((float) ((20 * x - 11.125) * c5))) / 2 + 1;
	}

	public static double easeInBounce(double x)
	{
		return 1 - easeOutBounce(1 - x);
	}

	public static double easeOutBounce(double x)
	{
		double n1 = 7.5625;
		double d1 = 2.75;

		if (x < 1 / d1)
		{
			return n1 * x * x;
		}
		else if (x < 2 / d1)
		{
			return n1 * (x -= 1.5 / d1) * x + 0.75;
		}
		else if (x < 2.5 / d1)
		{
			return n1 * (x -= 2.25 / d1) * x + 0.9375;
		}
		else
		{
			return n1 * (x -= 2.625 / d1) * x + 0.984375;
		}
	}

	public static double easeInOutBounce(double x)
	{
		return x < 0.5
				? (1 - easeOutBounce(1 - 2 * x)) / 2
				: (1 + easeOutBounce(2 * x - 1)) / 2;
	}
}
