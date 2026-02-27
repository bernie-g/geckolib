package com.geckolib.animatable;

import com.geckolib.GeckoLibServices;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.cache.SyncedSingletonAnimatableCache;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/// The [GeoAnimatable] interface specific to singleton objects
///
/// This primarily applies to armor and items
///
/// @see <a href="https://github.com/bernie-g/geckolib/wiki/Item-Animations">GeckoLib Wiki - Item Animations</a>
public interface SingletonGeoAnimatable extends GeoAnimatable {
    /// Register this as a synched `SingletonGeoAnimatable` instance with GeckoLib's networking functions
    ///
    /// This should be called inside the constructor of your object.
    static void registerSyncedAnimatable(SingletonGeoAnimatable animatable) {
        SyncedSingletonAnimatableCache.registerSyncedAnimatable(animatable);
    }

    /// Trigger a client-side animation for this GeoAnimatable for the given controller name and animation name
    ///
    /// This can be fired from either the client or the server, but optimally you would call it from the server
    ///
    /// **<u>DO NOT OVERRIDE</u>**
    ///
    /// @param relatedEntity  An entity related to the animatable to trigger the animation for (E.G., The player holding the item)
    /// @param instanceId     The unique id that identifies the specific animatable instance
    /// @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
    /// @param animName       The name of animation to trigger. This needs to have been registered with the controller via [AnimationController.triggerableAnim][AnimationController#triggerableAnim]
    @ApiStatus.NonExtendable
    default void triggerAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
        //noinspection resource
        if (relatedEntity.level().isClientSide()) {
            AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(instanceId);

            if (controllerName != null) {
                animatableManager.tryTriggerAnimation(controllerName, animName);
            }
            else {
                animatableManager.tryTriggerAnimation(animName);
            }
        }
        else {
            GeckoLibServices.NETWORK.triggerSingletonAnim(this, relatedEntity, instanceId, controllerName, animName);
        }
    }

    /// Stop a previously triggered animation for this GeoAnimatable for the given controller name and animation name
    ///
    /// This can be fired from either the client or the server, but optimally you would call it from the server
    ///
    /// **<u>DO NOT OVERRIDE</u>**
    ///
    /// @param relatedEntity An entity related to the animatable to trigger the animation for (E.G., The player holding the item)
    /// @param instanceId The unique id that identifies the specific animatable instance
    /// @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
    /// @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
    @ApiStatus.NonExtendable
    default void stopTriggeredAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, @Nullable String animName) {
        //noinspection resource
        if (relatedEntity.level().isClientSide()) {
            AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(instanceId);

            if (controllerName != null) {
                animatableManager.stopTriggeredAnimation(controllerName, animName);
            }
            else {
                animatableManager.stopTriggeredAnimation(animName);
            }
        }
        else {
            GeckoLibServices.NETWORK.stopTriggeredSingletonAnim(this, relatedEntity, instanceId, controllerName, animName);
        }
    }

    /// Trigger a client-side animation for this GeoAnimatable's armor rendering for the given controller name and animation name
    ///
    /// This can be fired from either the client or the server, but optimally you would call it from the server
    ///
    /// **<u>DO NOT OVERRIDE</u>**
    ///
    /// @param relatedEntity  An entity related to the animatable to trigger the animation for (E.G., The player holding the item)
    /// @param instanceId     The unique id that identifies the specific animatable instance
    /// @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
    /// @param animName       The name of animation to trigger. This needs to have been registered with the controller via [AnimationController.triggerableAnim][AnimationController#triggerableAnim]
    @ApiStatus.NonExtendable
    default void triggerArmorAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
        triggerAnim(relatedEntity, -instanceId, controllerName, animName);
    }

    /// Stop a previously triggered animation for this GeoAnimatable's armor rendering for the given controller name and animation name
    ///
    /// This can be fired from either the client or the server, but optimally you would call it from the server
    ///
    /// **<u>DO NOT OVERRIDE</u>**
    ///
    /// @param relatedEntity An entity related to the animatable to trigger the animation for (E.G., The player holding the item)
    /// @param instanceId The unique id that identifies the specific animatable instance
    /// @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
    /// @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
    @ApiStatus.NonExtendable
    default void stopTriggeredArmorAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, @Nullable String animName) {
        stopTriggeredAnim(relatedEntity, -instanceId, controllerName, animName);
    }

    /// Override the default handling for instantiating an AnimatableInstanceCache for this animatable
    ///
    /// Don't override this unless you know what you're doing
    @Override
    default @Nullable AnimatableInstanceCache animatableCacheOverride() {
        return new SingletonAnimatableInstanceCache(this);
    }

    /// Create your GeoRenderProvider reference here
    ///
    /// **<u>MUST provide an anonymous class</u>**
    ///
    /// Example Code:
    /// <pre>
    /// `void createRenderer(Consumer<GeoRenderProvider> consumer){consumer.accept(new GeoRenderProvider(){private final BlockEntityWithoutLevelRenderer itemRenderer;getItemRenderer(GeoArmor armor){if (this.itemRenderer == null)this.itemRenderer = new MyItemRenderer();return this.itemRenderer;}}}`</pre>
    ///
    /// @param consumer Consumer of your new GeoRenderProvider instance
    default void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {}

    /// Get the cached [GeoRenderProvider] from your [AnimatableInstanceCache]
    default Object getRenderProvider() {
        return getAnimatableInstanceCache().getRenderProvider();
    }
}
