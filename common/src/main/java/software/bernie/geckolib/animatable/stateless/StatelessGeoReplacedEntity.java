package software.bernie.geckolib.animatable.stateless;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.network.packet.entity.StatelessEntityPlayAnimPacket;
import software.bernie.geckolib.network.packet.entity.StatelessEntityStopAnimPacket;

/**
 * Extension of {@link StatelessAnimatable} for {@link GeoReplacedEntity} animatables
 */
public interface StatelessGeoReplacedEntity extends StatelessGeoSingletonAnimatable, GeoReplacedEntity {
    /**
     * Start or continue an animation, letting its pre-defined loop type determine whether it should loop or not
     */
    default void playAnimation(String animation, Entity relatedEntity) {
        playAnimation(animation, relatedEntity, relatedEntity.getId());
    }

    /**
     * Start or continue an animation, forcing it to loop continuously until stopped
     */
    default void playLoopingAnimation(String animation, Entity relatedEntity) {
        playLoopingAnimation(animation, relatedEntity, relatedEntity.getId());
    }

    /**
     * Start or continue an animation, then hold the pose at the end of the animation until otherwise stopped
     */
    default void playAndHoldAnimation(String animation, Entity relatedEntity) {
        playAndHoldAnimation(animation, relatedEntity, relatedEntity.getId());
    }

    /**
     * Stop an already-playing animation
     */
    default void stopAnimation(RawAnimation animation, Entity relatedEntity) {
        stopAnimation(animation, relatedEntity, relatedEntity.getId());
    }

    /**
     * Start or continue a pre-defined animation
     */
    default void playAnimation(RawAnimation animation, Entity relatedEntity) {
        playAnimation(animation, relatedEntity, relatedEntity.getId());
    }

    /**
     * Stop an already-playing animation
     */
    default void stopAnimation(String animation, Entity relatedEntity) {
        stopAnimation(animation, relatedEntity, relatedEntity.getId());
    }

    @Override
    default void playAnimation(RawAnimation animation, Entity relatedEntity, long instanceId) {
        if (relatedEntity.level().isClientSide()) {
            handleClientAnimationPlay(this, instanceId, animation);
        }
        else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityPlayAnimPacket((int)instanceId, true, animation), relatedEntity);
        }
    }

    @Override
    default void stopAnimation(String animation, Entity relatedEntity, long instanceId) {
        if (relatedEntity.level().isClientSide()) {
            handleClientAnimationStop(this, instanceId, animation);
        }
        else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityStopAnimPacket((int)instanceId, true, animation), relatedEntity);
        }
    }
}
