/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */
package software.bernie.geckolib.core.animatable;

import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

/**
 * This is the root interface for all animatable objects in Geckolib.
 * Generally speaking you should use one of the sub-interfaces relevant to your specific object so that your model can be automatically handled.<br>
 * See:<br>
 * <ul>
 *     <li>{@code GeoBlock}</li>
 *     <li>{@code GeoEntity}</li>
 *     <li>{@code GeoItem}</li>
 * </ul>
 */
public interface GeoAnimatable {
	/**
	 * Register your {@link AnimationController AnimationControllers} and their respective animations and conditions.
	 * Override this method in your animatable object and add your controllers via {@link software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar#add ControllerRegistrar.add}.
	 * You may add as many controllers as wanted.
	 * <br><br>
	 * Each controller can only play <u>one</u> animation at a time, and so animations that you intend to play concurrently should be handled in independent controllers.
	 * Note having multiple animations playing via multiple controllers can override parts of one animation with another if both animations use the same bones or child bones.
	 *
	 * @param controllers The object to register your controller instances to
	 */
	void registerControllers(AnimatableManager.ControllerRegistrar controllers);

	/**
	 * Each instance of a {@code GeoAnimatable} must return an instance of an {@link AnimatableInstanceCache}, which handles instance-specific animation info.
	 * Generally speaking, you should create your cache using {@code GeckoLibUtil#createCache} and store it in your animatable instance, returning that cached instance when called.
	 * @return A cached instance of an {@code AnimatableInstanceCache}
	 */
	AnimatableInstanceCache getAnimatableInstanceCache();

	/**
	 * Defines the speed in which the {@link software.bernie.geckolib.core.animation.AnimationProcessor} should return
	 * {@link CoreGeoBone GeoBones} that currently have no animations
	 * to their default position.
	 */
	default double getBoneResetTime() {
		return 1;
	}

	/**
	 * Defines whether the animations for this animatable should continue playing in the background when the game is paused.<br>
	 * By default, animation progress will be stalled while the game is paused.
	 */
	default boolean shouldPlayAnimsWhileGamePaused() {
		return false;
	}

	/**
	 * Returns the current age/tick of the animatable instance.<br>
	 * By default this is just the animatable's age in ticks, but this method allows for non-ticking custom animatables to provide their own values
	 * @param object An object related to this animatable relevant to tick calculation. Different subclasses will use this differently
	 * @return The current tick/age of the animatable, for animation purposes
	 */
	double getTick(Object object);

	/**
	 * Override the default handling for instantiating an AnimatableInstanceCache for this animatable.<br>
	 * Don't override this unless you know what you're doing.
	 */
	default AnimatableInstanceCache animatableCacheOverride() {
		return null;
	}
}
