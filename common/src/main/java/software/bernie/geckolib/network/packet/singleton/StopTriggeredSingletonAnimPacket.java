package software.bernie.geckolib.network.packet.singleton;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.cache.SyncedSingletonAnimatableCache;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

import java.util.Optional;
import java.util.function.Consumer;

public record StopTriggeredSingletonAnimPacket(String syncableId, long instanceId, Optional<String> controllerName, Optional<String> animName) implements MultiloaderPacket {
    public static final Type<StopTriggeredSingletonAnimPacket> TYPE = new Type<>(GeckoLibConstants.id("stop_triggered_singleton_anim"));
    public static final StreamCodec<FriendlyByteBuf, StopTriggeredSingletonAnimPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StopTriggeredSingletonAnimPacket::syncableId,
            ByteBufCodecs.VAR_LONG, StopTriggeredSingletonAnimPacket::instanceId,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), StopTriggeredSingletonAnimPacket::controllerName,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), StopTriggeredSingletonAnimPacket::animName,
            StopTriggeredSingletonAnimPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            GeoAnimatable animatable = SyncedSingletonAnimatableCache.getSyncedAnimatable(this.syncableId);

            if (animatable != null) {
                AnimatableManager<GeoAnimatable> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(this.instanceId);

                if (this.controllerName.isPresent()) {
                    animatableManager.stopTriggeredAnimation(this.controllerName.get(), this.animName.orElse(null));
                }
                else {
                    animatableManager.stopTriggeredAnimation(this.animName.orElse(null));
                }
            }
        });
    }
}
