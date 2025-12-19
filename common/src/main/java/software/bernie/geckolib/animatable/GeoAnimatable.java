package software.bernie.geckolib.animatable;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;

/**
 * This is the root interface for all animatable objects in Geckolib
 * <p>
 * Generally speaking you should use one of the sub-interfaces relevant to your specific object so that your model can be automatically handled
 * <p>
 * See:
 * <ul>
 *     <li>{@code GeoBlock}</li>
 *     <li>{@code GeoEntity}</li>
 *     <li>{@code GeoItem}</li>
 * </ul>
 */
public interface GeoAnimatable {
	/**
	 * Register your {@link AnimationController AnimationControllers} and their respective animations and conditions
	 * <p>
	 * Override this method in your animatable object and add your controllers via {@link AnimatableManager.ControllerRegistrar#add ControllerRegistrar.add}
	 * <p>
	 * You may add as many controllers as wanted
	 * <p>
	 * Each controller can only play <u>one</u> animation at a time, and so animations that you intend to play concurrently should be handled in independent controllers
	 * <p>
	 * Note having multiple animations playing via multiple controllers can override parts of one animation with another if both animations use the same bones or child bones
	 *
	 * @param controllers The object to register your controller instances to
	 */
	void registerControllers(AnimatableManager.ControllerRegistrar controllers);

	/**
	 * Each instance of a {@code GeoAnimatable} must return an instance of an {@link AnimatableInstanceCache}, which handles instance-specific animation info
	 * <p>
	 * Generally speaking, you should create your cache using {@code GeckoLibUtil#createCache} and store it in your animatable instance, returning that cached instance when called
	 *
	 * @return A cached instance of an {@code AnimatableInstanceCache}
	 */
	AnimatableInstanceCache getAnimatableInstanceCache();

	/**
	 * Override the default handling for instantiating an AnimatableInstanceCache for this animatable
	 * <p>
	 * Don't override this unless you know what you're doing.
	 */
    @ApiStatus.OverrideOnly
	default @Nullable AnimatableInstanceCache animatableCacheOverride() {
		return null;
	}
}
