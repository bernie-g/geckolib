package software.bernie.geckolib.network.packet.singleton;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.stateless.StatelessGeoSingletonAnimatable;
import software.bernie.geckolib.cache.SyncedSingletonAnimatableCache;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

import java.util.function.Consumer;

public record StatelessSingletonStopAnimPacket(String syncableId, long instanceId, String animation) implements MultiloaderPacket {
    public static final Type<StatelessSingletonStopAnimPacket> TYPE = new Type<>(GeckoLibConstants.id("stateless_singleton_stop_anim"));
    public static final StreamCodec<FriendlyByteBuf, StatelessSingletonStopAnimPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StatelessSingletonStopAnimPacket::syncableId,
            ByteBufCodecs.VAR_LONG, StatelessSingletonStopAnimPacket::instanceId,
            ByteBufCodecs.STRING_UTF8, StatelessSingletonStopAnimPacket::animation,
            StatelessSingletonStopAnimPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            GeoAnimatable animatable = SyncedSingletonAnimatableCache.getSyncedAnimatable(this.syncableId);

            if (animatable instanceof StatelessGeoSingletonAnimatable statelessAnimatable)
                statelessAnimatable.handleClientAnimationStop(animatable, this.instanceId, this.animation);
        });
    }
}
