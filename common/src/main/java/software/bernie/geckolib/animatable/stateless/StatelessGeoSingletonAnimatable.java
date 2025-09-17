package software.bernie.geckolib.animatable.stateless;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.cache.SyncedSingletonAnimatableCache;
import software.bernie.geckolib.network.packet.singleton.StatelessSingletonPlayAnimPacket;
import software.bernie.geckolib.network.packet.singleton.StatelessSingletonStopAnimPacket;

/**
 * Extension of {@link StatelessAnimatable} for {@link SingletonGeoAnimatable} animatables.
 * <p>
 * Animatables <b><u>MUST</u></b> be registered with {@link SingletonGeoAnimatable#registerSyncedAnimatable} to use this interface
 */
public non-sealed interface StatelessGeoSingletonAnimatable extends StatelessAnimatable {
    /**
     * Start or continue an animation, letting its pre-defined loop type determine whether it should loop or not
     */
    default void playAnimation(String animation, Entity relatedEntity, long instanceId) {
        playAnimation(RawAnimation.begin().thenPlay(animation), relatedEntity, instanceId);
    }

    /**
     * Start or continue an animation, forcing it to loop continuously until stopped
     */
    default void playLoopingAnimation(String animation, Entity relatedEntity, long instanceId) {
        playAnimation(RawAnimation.begin().thenLoop(animation), relatedEntity, instanceId);
    }

    /**
     * Start or continue an animation, then hold the pose at the end of the animation until otherwise stopped
     */
    default void playAndHoldAnimation(String animation, Entity relatedEntity, long instanceId) {
        playAnimation(RawAnimation.begin().thenPlayAndHold(animation), relatedEntity, instanceId);
    }

    /**
     * Start or continue a pre-defined animation
     */
    default void playAnimation(RawAnimation animation, Entity relatedEntity, long instanceId) {
        if (!(this instanceof SingletonGeoAnimatable animatable))
            throw new ClassCastException("Cannot use StatelessGeoEntity on a non-SingletonGeoAnimatable!");

        if (relatedEntity.level().isClientSide) {
            handleClientAnimationPlay(animatable, instanceId, animation);
        }
        else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessSingletonPlayAnimPacket(SyncedSingletonAnimatableCache.getOrCreateId(animatable), instanceId, animation), relatedEntity);
        }
    }

    /**
     * Stop an already-playing animation
     */
    default void stopAnimation(String animation, Entity relatedEntity, long instanceId) {
        if (!(this instanceof SingletonGeoAnimatable animatable))
            throw new ClassCastException("Cannot use StatelessGeoEntity on a non-SingletonGeoAnimatable!");

        if (relatedEntity.level().isClientSide) {
            handleClientAnimationStop(animatable, instanceId, animation);
        }
        else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessSingletonStopAnimPacket(SyncedSingletonAnimatableCache.getOrCreateId(animatable), instanceId, animation), relatedEntity);
        }
    }

    // Unsupported method handlers below; do not use

    /**
     * @deprecated Use {@link #playAnimation(String, Entity, long)} instead.
     */
    @Deprecated
    default void playAnimation(String animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonGeoAnimatable");
    }

    /**
     * @deprecated Use {@link #playLoopingAnimation(String, Entity, long)} instead.
     */
    @Deprecated
    default void playLoopingAnimation(String animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonGeoAnimatable");
    }

    /**
     * @deprecated Use {@link #playAndHoldAnimation(String, Entity, long)} instead.
     */
    @Deprecated
    default void playAndHoldAnimation(String animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonGeoAnimatable");
    }

    /**
     * @deprecated Use {@link #playAnimation(RawAnimation, Entity, long)} instead.
     */
    @Deprecated
    default void playAnimation(RawAnimation animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonGeoAnimatable");
    }

    /**
     * @deprecated Use {@link #stopAnimation(String, Entity, long)} instead.
     */
    @Deprecated
    default void stopAnimation(String animation) {
        throw new IllegalStateException("Cannot use non-level method handlers on StatelessSingletonGeoAnimatable");
    }
}
