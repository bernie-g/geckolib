package com.geckolib.animatable;

import com.geckolib.GeckoLibServices;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.renderer.GeoReplacedEntityRenderer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/// The [GeoAnimatable] interface specific to [Entities][net.minecraft.world.entity.Entity]
///
/// This also applies to Projectiles and other Entity subclasses
///
/// **NOTE:** This <u>cannot</u> be used for entities using the [GeoReplacedEntityRenderer]
/// as you aren't extending `Entity`. Use [GeoReplacedEntity] instead.
///
/// @see <a href="https://github.com/bernie-g/geckolib/wiki/Entity-Animations">GeckoLib Wiki - Entity Animations</a>
public interface GeoEntity extends GeoAnimatable {
	/// Trigger an animation for this Entity, based on the controller name and animation name
	///
	/// This can be fired from either the client or the server, but optimally you would call it from the server
	///
	/// **<u>DO NOT OVERRIDE</u>**
	///
	/// @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	/// @param animName The name of animation to trigger. This needs to have been registered with the controller via [AnimationController.triggerableAnim][AnimationController#triggerableAnim]
	@ApiStatus.NonExtendable
	default void triggerAnim(@Nullable String controllerName, String animName) {
		Entity entity = (Entity)this;

        //noinspection resource
        if (entity.level().isClientSide()) {
			AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(entity.getId());

			if (controllerName != null) {
				animatableManager.tryTriggerAnimation(controllerName, animName);
			}
			else {
				animatableManager.tryTriggerAnimation(animName);
			}
		}
		else {
			GeckoLibServices.NETWORK.triggerEntityAnim(entity, false, controllerName, animName);
		}
	}

	/// Stop a previously triggered animation for this Entity for the given controller name and animation name
	///
	/// This can be fired from either the client or the server, but optimally you would call it from the server
	///
	/// **<u>DO NOT OVERRIDE</u>**
	///
	/// @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
	/// @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
	@ApiStatus.NonExtendable
	default void stopTriggeredAnim(@Nullable String controllerName, @Nullable String animName) {
		Entity entity = (Entity)this;

        //noinspection resource
        if (entity.level().isClientSide()) {
			AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(entity.getId());

			if (controllerName != null) {
				animatableManager.stopTriggeredAnimation(controllerName, animName);
			}
			else {
				animatableManager.stopTriggeredAnimation(animName);
			}
		}
		else {
			GeckoLibServices.NETWORK.stopTriggeredEntityAnim(entity, false, controllerName, animName);
		}
	}
}
