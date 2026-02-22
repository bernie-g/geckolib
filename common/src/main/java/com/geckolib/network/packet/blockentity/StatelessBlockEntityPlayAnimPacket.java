package com.geckolib.network.packet.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.stateless.StatelessGeoBlockEntity;
import com.geckolib.animation.RawAnimation;
import com.geckolib.network.packet.MultiloaderPacket;
import com.geckolib.util.ClientUtil;

import java.util.function.Consumer;

public record StatelessBlockEntityPlayAnimPacket(BlockPos blockPos, RawAnimation animation) implements MultiloaderPacket {
    public static final Type<StatelessBlockEntityPlayAnimPacket> TYPE = new Type<>(GeckoLibConstants.id("stateless_block_entity_play_anim"));
    public static final StreamCodec<FriendlyByteBuf, StatelessBlockEntityPlayAnimPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StatelessBlockEntityPlayAnimPacket::blockPos,
            RawAnimation.STREAM_CODEC, StatelessBlockEntityPlayAnimPacket::animation,
            StatelessBlockEntityPlayAnimPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            final Level level = ClientUtil.getLevel();

            if (level != null && level.getBlockEntity(this.blockPos) instanceof GeoBlockEntity blockEntity && blockEntity instanceof StatelessGeoBlockEntity statelessAnimatable)
                statelessAnimatable.handleClientAnimationPlay(blockEntity, 0, this.animation);
        });
    }
}
