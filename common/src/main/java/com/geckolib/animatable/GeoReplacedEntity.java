package com.geckolib.animatable;

import com.geckolib.GeckoLibServices;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/// The [GeoAnimatable] interface specific to [Entities][Entity]
///
/// This interface is <u>specifically</u> for entities replacing the rendering of other, existing entities
///
/// @see <a href="https://github.com/bernie-g/geckolib/wiki/Entity-Animations">GeckoLib Wiki - Entity Animations</a>
public interface GeoReplacedEntity extends SingletonGeoAnimatable {
	/// Trigger an animation for this Entity, based on the controller name and animation name
	///
	/// This can be fired from either the client or the server, but optimally you would call it from the server
	///
	/// **<u>DO NOT OVERRIDE</u>**
	///
	/// @param relatedEntity An entity related to the state of the data for syncing
	/// @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	/// @param animName The name of animation to trigger. This needs to have been registered with the controller via [AnimationController.triggerableAnim][AnimationController#triggerableAnim]
	@ApiStatus.NonExtendable
	default void triggerAnim(Entity relatedEntity, @Nullable String controllerName, String animName) {
        //noinspection resource
        if (relatedEntity.level().isClientSide()) {
			AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(relatedEntity.getId());

			if (controllerName != null) {
				animatableManager.tryTriggerAnimation(controllerName, animName);
			}
			else {
				animatableManager.tryTriggerAnimation(animName);
			}
		}
		else {
			GeckoLibServices.NETWORK.triggerEntityAnim(relatedEntity, true, controllerName, animName);
		}
	}

	/// Stop a previously triggered animation for this Entity for the given controller name and animation name
	///
	/// This can be fired from either the client or the server, but optimally you would call it from the server
	///
	/// **<u>DO NOT OVERRIDE</u>**
	///
	/// @param relatedEntity An entity related to the state of the data for syncing
	/// @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
	/// @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
	@ApiStatus.NonExtendable
	default void stopTriggeredAnim(Entity relatedEntity, @Nullable String controllerName, @Nullable String animName) {
        //noinspection resource
        if (relatedEntity.level().isClientSide()) {
			AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(relatedEntity.getId());

			if (controllerName != null) {
				animatableManager.stopTriggeredAnimation(controllerName, animName);
			}
			else {
				animatableManager.stopTriggeredAnimation(animName);
			}
		}
		else {
			GeckoLibServices.NETWORK.stopTriggeredEntityAnim(relatedEntity, true, controllerName, animName);
		}
	}

	// These methods aren't used for GeoReplacedEntity
	@ApiStatus.NonExtendable
	@Override
	default void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {}

	// These methods aren't used for GeoReplacedEntity
	@SuppressWarnings("DataFlowIssue")
    @ApiStatus.NonExtendable
	@Override
	default Object getRenderProvider() {
		return null;
	}
}
