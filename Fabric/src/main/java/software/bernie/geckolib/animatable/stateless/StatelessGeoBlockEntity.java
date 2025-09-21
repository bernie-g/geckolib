package software.bernie.geckolib.animatable.stateless;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.network.packet.StatelessBlockEntityPlayAnimPacket;
import software.bernie.geckolib.network.packet.StatelessBlockEntityStopAnimPacket;

/**
 * Extension of {@link StatelessAnimatable} for {@link GeoBlockEntity} animatables
 */
public non-sealed interface StatelessGeoBlockEntity extends StatelessAnimatable, GeoBlockEntity {
    /**
     * Start or continue a pre-defined animation
     */
    @Override
    default void playAnimation(RawAnimation animation) {
        if (!(this instanceof BlockEntity self))
            throw new ClassCastException("Cannot use StatelessGeoBlockEntity on a non-blockentity animatable!");

        if (self.getLevel() instanceof ServerLevel level) {
            GeckoLibNetwork.sendToEntitiesTrackingChunk(new StatelessBlockEntityPlayAnimPacket(self.getBlockPos(), animation), level, self.getBlockPos());
        }
        else {
            handleClientAnimationPlay(this, 0, animation);
        }
    }

    /**
     * Stop an already-playing animation
     */
    @Override
    default void stopAnimation(String animation) {
        if (!(this instanceof BlockEntity self))
            throw new ClassCastException("Cannot use StatelessGeoBlockEntity on a non-blockentity animatable!");

        if (self.getLevel() instanceof ServerLevel level) {
            GeckoLibNetwork.sendToEntitiesTrackingChunk(new StatelessBlockEntityStopAnimPacket(self.getBlockPos(), animation), level, self.getBlockPos());
        }
        else {
            handleClientAnimationStop(this, 0, animation);
        }
    }
}