package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.stateless.StatelessGeoSingletonAnimatable;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public record StatelessSingletonPlayAnimPacket(String syncableId, long instanceId, RawAnimation animation) implements MultiloaderPacket {
    public static final Type<StatelessSingletonPlayAnimPacket> TYPE = new Type<>(GeckoLibConstants.id("stateless_singleton_play_anim"));
    public static final StreamCodec<FriendlyByteBuf, StatelessSingletonPlayAnimPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StatelessSingletonPlayAnimPacket::syncableId,
            ByteBufCodecs.VAR_LONG, StatelessSingletonPlayAnimPacket::instanceId,
            RawAnimation.STREAM_CODEC, StatelessSingletonPlayAnimPacket::animation,
            StatelessSingletonPlayAnimPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            GeoAnimatable animatable = GeckoLibUtil.getSyncedAnimatable(this.syncableId);

            if (animatable instanceof StatelessGeoSingletonAnimatable statelessAnimatable)
                statelessAnimatable.handleClientAnimationPlay(animatable, this.instanceId, this.animation);
        });
    }
}
