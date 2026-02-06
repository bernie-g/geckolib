package software.bernie.geckolib.animatable;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;

/// This is the root interface for all animatable objects in Geckolib
///
/// Generally speaking you should use one of the sub-interfaces relevant to your specific object so that your model can be automatically handled
///
/// See:
///
///   - `GeoBlock`
///   - `GeoEntity`
///   - `GeoItem`
///
public interface GeoAnimatable {
	/// Register your [AnimationControllers][AnimationController] and their respective animations and conditions
	///
	/// Override this method in your animatable object and add your controllers via [ControllerRegistrar.add][AnimatableManager.ControllerRegistrar#add]
	///
	/// You may add as many controllers as wanted
	///
	/// Each controller can only play <u>one</u> animation at a time, and so animations that you intend to play concurrently should be handled in independent controllers
	///
	/// Note having multiple animations playing via multiple controllers can override parts of one animation with another if both animations use the same bones or child bones
	///
	/// @param controllers The object to register your controller instances to
	void registerControllers(AnimatableManager.ControllerRegistrar controllers);

	/// Each instance of a `GeoAnimatable` must return an instance of an [AnimatableInstanceCache], which handles instance-specific animation info
	///
	/// Generally speaking, you should create your cache using `GeckoLibUtil#createCache` and store it in your animatable instance, returning that cached instance when called
	///
	/// @return A cached instance of an `AnimatableInstanceCache`
	AnimatableInstanceCache getAnimatableInstanceCache();

	/// Override the default handling for instantiating an AnimatableInstanceCache for this animatable
	///
	/// Don't override this unless you know what you're doing.
	@ApiStatus.OverrideOnly
	default @Nullable AnimatableInstanceCache animatableCacheOverride() {
		return null;
	}
}
