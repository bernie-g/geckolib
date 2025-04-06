package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.cache.SyncedSingletonAnimatableCache;

import java.util.Optional;
import java.util.function.Consumer;

public record SingletonAnimTriggerPacket(String syncableId, long instanceId, Optional<String> controllerName, String animName) implements MultiloaderPacket {
    public static final CustomPacketPayload.Type<SingletonAnimTriggerPacket> TYPE = new Type<>(GeckoLibConstants.id("singleton_anim_trigger"));
    public static final StreamCodec<FriendlyByteBuf, SingletonAnimTriggerPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SingletonAnimTriggerPacket::syncableId,
            ByteBufCodecs.VAR_LONG, SingletonAnimTriggerPacket::instanceId,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), SingletonAnimTriggerPacket::controllerName,
            ByteBufCodecs.STRING_UTF8, SingletonAnimTriggerPacket::animName,
            SingletonAnimTriggerPacket::new);

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

                if (animatableManager != null)
                    animatableManager.tryTriggerAnimation(this.controllerName.orElse(null), this.animName);
            }
        });
    }
}
