package com.geckolib.network.packet.singleton;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.SingletonGeoAnimatable;
import com.geckolib.cache.SyncedSingletonAnimatableCache;
import com.geckolib.constant.dataticket.SerializableDataTicket;
import com.geckolib.network.packet.MultiloaderPacket;
import com.geckolib.util.ClientUtil;

import java.util.function.Consumer;

public record SingletonDataSyncPacket<D>(String syncableId, long instanceId, SerializableDataTicket<D> dataTicket, D data) implements MultiloaderPacket {
    public static final CustomPacketPayload.Type<SingletonDataSyncPacket<?>> TYPE = new Type<>(GeckoLibConstants.id("singleton_data_sync"));
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final StreamCodec<RegistryFriendlyByteBuf, SingletonDataSyncPacket<?>> CODEC = StreamCodec.of((buf, packet) -> {
        SerializableDataTicket.STREAM_CODEC.encode(buf, packet.dataTicket);
        buf.writeUtf(packet.syncableId);
        buf.writeVarLong(packet.instanceId);
        ((StreamCodec)packet.dataTicket.streamCodec()).encode(buf, packet.data);
    }, buf -> {
        final SerializableDataTicket dataTicket = SerializableDataTicket.STREAM_CODEC.decode(buf);

        return new SingletonDataSyncPacket<>(buf.readUtf(), buf.readVarLong(), dataTicket, dataTicket.streamCodec().decode(buf));
    });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            final Player player = ClientUtil.getClientPlayer();
            final GeoAnimatable animatable;

            if (player == null || (animatable = SyncedSingletonAnimatableCache.getSyncedAnimatable(this.syncableId)) == null)
                return;

            if (animatable instanceof SingletonGeoAnimatable singleton)
                singleton.setAnimData(player, this.instanceId, this.dataTicket, this.data);
        });
    }
}
