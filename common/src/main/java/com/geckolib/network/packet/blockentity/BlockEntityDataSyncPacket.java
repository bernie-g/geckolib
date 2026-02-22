package com.geckolib.network.packet.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.constant.dataticket.SerializableDataTicket;
import com.geckolib.network.packet.MultiloaderPacket;
import com.geckolib.util.ClientUtil;

import java.util.function.Consumer;

public record BlockEntityDataSyncPacket<D>(BlockPos pos, SerializableDataTicket<D> dataTicket, D data) implements MultiloaderPacket {
    public static final CustomPacketPayload.Type<BlockEntityDataSyncPacket<?>> TYPE = new Type<>(GeckoLibConstants.id("blockentity_data_sync"));
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityDataSyncPacket<?>> CODEC = StreamCodec.of((buf, packet) -> {
        SerializableDataTicket.STREAM_CODEC.encode(buf, packet.dataTicket);
        buf.writeBlockPos(packet.pos);
        ((StreamCodec)packet.dataTicket.streamCodec()).encode(buf, packet.data);
    }, buf -> {
        final SerializableDataTicket dataTicket = SerializableDataTicket.STREAM_CODEC.decode(buf);

        return new BlockEntityDataSyncPacket<>(buf.readBlockPos(), dataTicket, dataTicket.streamCodec().decode(buf));
    });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            final Level level = ClientUtil.getLevel();

            if (level != null && level.getBlockEntity(this.pos) instanceof GeoBlockEntity blockEntity)
                blockEntity.setAnimData(this.dataTicket, this.data);
        });
    }
}
