package software.bernie.geckolib3.core.easing;

import java.util.Locale;

public enum EasingType {
	NONE, CUSTOM, Linear, Step, EaseInSine, EaseOutSine, EaseInOutSine, EaseInQuad, EaseOutQuad, EaseInOutQuad,
	EaseInCubic, EaseOutCubic, EaseInOutCubic, EaseInQuart, EaseOutQuart, EaseInOutQuart, EaseInQuint, EaseOutQuint,
	EaseInOutQuint, EaseInExpo, EaseOutExpo, EaseInOutExpo, EaseInCirc, EaseOutCirc, EaseInOutCirc, EaseInBack,
	EaseOutBack, EaseInOutBack, EaseInElastic, EaseOutElastic, EaseInOutElastic, EaseInBounce, EaseOutBounce,
	EaseInOutBounce;

	public static EasingType getEasingTypeFromString(String search) {
		return switch (search.toLowerCase(Locale.ROOT)) {
		default -> NONE;
		case "custom" -> CUSTOM;
		case "linear" -> Linear;
		case "step" ->  Step;
		case "easeinsine" ->  EaseInSine;
		case "easeoutsine" ->  EaseOutSine;
		case "easeinoutsine" ->  EaseInOutSine;
		case "easeinquad" ->  EaseInQuad;
		case "easeoutquad" ->  EaseOutQuad;
		case "easeinoutquad" ->  EaseInOutQuad;
		case "easeincubic" -> EaseInCubic;
		case "easeoutcubic" ->  EaseOutCubic;
		case "easeinoutcubic" ->  EaseInOutCubic;
		case "easeinquart" ->  EaseInQuart;
		case "easeoutquart" ->  EaseOutQuart;
		case "easeinoutquart" ->  EaseInOutQuart;
		case "easeinquint" ->  EaseInQuint;
		case "easeoutquint" ->  EaseOutQuint;
		case "easeinoutquint" -> EaseInOutQuint;
		case "easeinexpo" ->  EaseInExpo;
		case "easeoutexpo" ->  EaseOutExpo;
		case "easeinoutexpo" ->  EaseInOutExpo;
		case "easeincirc" ->  EaseInCirc;
		case "easeoutcirc" ->  EaseOutCirc;
		case "easeinoutcirc" ->  EaseInOutCirc;
		case "easeinback" ->  EaseInBack;
		case "easeoutback" -> EaseOutBack;
		case "easeinoutback" ->  EaseInOutBack;
		case "easeinelastic" ->  EaseInElastic;
		case "easeoutelastic" ->  EaseOutElastic;
		case "easeinoutelastic" ->  EaseInOutElastic;
		case "easeinbounce" ->  EaseInBounce;
		case "easeoutbounce" ->  EaseOutBounce;
		case "easeinoutbounce" -> EaseInOutBounce;
	};
	}
}
