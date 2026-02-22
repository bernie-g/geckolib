package com.geckolib.animatable.stateless;

import net.minecraft.world.entity.Entity;
import com.geckolib.GeckoLibServices;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animation.RawAnimation;
import com.geckolib.network.packet.entity.StatelessEntityPlayAnimPacket;
import com.geckolib.network.packet.entity.StatelessEntityStopAnimPacket;

/// Extension of [StatelessAnimatable] for [GeoEntity] animatables
public non-sealed interface StatelessGeoEntity extends StatelessAnimatable, GeoEntity {
    /// Start or continue a pre-defined animation
    @Override
    default void playAnimation(RawAnimation animation) {
        if (!(this instanceof Entity self))
            throw new ClassCastException("Cannot use StatelessGeoEntity on a non-entity animatable!");

        if (self.level().isClientSide()) {
            handleClientAnimationPlay(this, self.getId(), animation);
        }
        else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityPlayAnimPacket(self.getId(), false, animation), self);
        }
    }

    /// Stop an already-playing animation
    @Override
    default void stopAnimation(String animation) {
        if (!(this instanceof Entity self))
            throw new ClassCastException("Cannot use StatelessGeoEntity on a non-entity animatable!");

        if (self.level().isClientSide()) {
            handleClientAnimationStop(this, self.getId(), animation);
        }
        else {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingEntity(new StatelessEntityStopAnimPacket(self.getId(), false, animation), self);
        }
    }
}
