package software.bernie.geckolib.animatable.stateless;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.network.packet.blockentity.StatelessBlockEntityPlayAnimPacket;
import software.bernie.geckolib.network.packet.blockentity.StatelessBlockEntityStopAnimPacket;

/// Extension of [StatelessAnimatable] for [GeoBlockEntity] animatables
public non-sealed interface StatelessGeoBlockEntity extends StatelessAnimatable, GeoBlockEntity {
    /// Start or continue a pre-defined animation
    @Override
    default void playAnimation(RawAnimation animation) {
        if (!(this instanceof BlockEntity self))
            throw new ClassCastException("Cannot use StatelessGeoBlockEntity on a non-BlockEntity animatable!");

        if (self.getLevel() instanceof ServerLevel level) {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingBlock(new StatelessBlockEntityPlayAnimPacket(self.getBlockPos(), animation), level, self.getBlockPos());
        }
        else {
            handleClientAnimationPlay(this, 0, animation);
        }
    }

    /// Stop an already-playing animation
    @Override
    default void stopAnimation(String animation) {
        if (!(this instanceof BlockEntity self))
            throw new ClassCastException("Cannot use StatelessGeoBlockEntity on a non-BlockEntity animatable!");

        if (self.getLevel() instanceof ServerLevel level) {
            GeckoLibServices.NETWORK.sendToAllPlayersTrackingBlock(new StatelessBlockEntityStopAnimPacket(self.getBlockPos(), animation), level, self.getBlockPos());
        }
        else {
            handleClientAnimationStop(this, 0, animation);
        }
    }
}
