package com.geckolib.network.packet.singleton;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.cache.SyncedSingletonAnimatableCache;
import com.geckolib.network.packet.MultiloaderPacket;

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

                if (this.controllerName.isPresent()) {
                    animatableManager.tryTriggerAnimation(this.controllerName.get(), this.animName);
                }
                else {
                    animatableManager.tryTriggerAnimation(this.animName);
                }
            }
        });
    }
}
