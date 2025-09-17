package software.bernie.geckolib.animatable.stateless;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.network.packet.entity.StatelessEntityPlayAnimPacket;
import software.bernie.geckolib.network.packet.entity.StatelessEntityStopAnimPacket;

/**
 * Extension of {@link StatelessAnimatable} for {@link GeoEntity} animatables
 */
public non-sealed interface StatelessGeoEntity extends StatelessAnimatable {
    /**
     * Start or continue a pre-defined animation
     */
    @Override
    default void playAnimation(RawAnimation animation) {
        if (!(this instanceof Entity self) || !(this instanceof GeoEntity animatable))
            throw new ClassCastException("Cannot use StatelessGeoEntity on a non-entity animatable!");

        if (self.level().isClientSide) {
            handleClientAnimationPlay(animatable, self.getId(), animation);
        }
        else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityPlayAnimPacket(self.getId(), false, animation), self);
        }
    }

    /**
     * Stop an already-playing animation
     */
    @Override
    default void stopAnimation(String animation) {
        if (!(this instanceof Entity self) || !(this instanceof GeoEntity animatable))
            throw new ClassCastException("Cannot use StatelessGeoEntity on a non-entity animatable!");

        if (self.level().isClientSide) {
            handleClientAnimationStop(animatable, self.getId(), animation);
        }
        else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityStopAnimPacket(self.getId(), false, animation), self);
        }
    }
}
