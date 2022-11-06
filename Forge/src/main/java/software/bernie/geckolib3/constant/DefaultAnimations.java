package software.bernie.geckolib3.constant;

import software.bernie.geckolib3.core.animation.RawAnimation;

/**
 * Optionally usable class that holds constants for recommended animation paths.<br>
 * Using these won't affect much, but it may help keep some consistency in animation namings.<br>
 * Additionally, it encourages use of cached {@link software.bernie.geckolib3.core.animation.RawAnimation RawAnimations}, to reduce overheads.
 */
public final class DefaultAnimations {
	public static final RawAnimation GENERIC_PERMANENT = RawAnimation.begin().thenLoop("generic.permanent_anim");

	public static final RawAnimation ITEM_ON_USE = RawAnimation.begin().thenPlay("item.use");
}
