package com.geckolib.network.packet.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.stateless.StatelessAnimatable;
import com.geckolib.animation.RawAnimation;
import com.geckolib.network.packet.MultiloaderPacket;
import com.geckolib.util.ClientUtil;
import com.geckolib.util.RenderUtil;

import java.util.function.Consumer;

public record StatelessEntityPlayAnimPacket(int entityId, boolean isReplacedEntity, RawAnimation animation) implements MultiloaderPacket {
    public static final Type<StatelessEntityPlayAnimPacket> TYPE = new Type<>(GeckoLibConstants.id("stateless_entity_play_anim"));
    public static final StreamCodec<FriendlyByteBuf, StatelessEntityPlayAnimPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StatelessEntityPlayAnimPacket::entityId,
            ByteBufCodecs.BOOL, StatelessEntityPlayAnimPacket::isReplacedEntity,
            RawAnimation.STREAM_CODEC, StatelessEntityPlayAnimPacket::animation,
            StatelessEntityPlayAnimPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            final Level level = ClientUtil.getLevel();
            final Entity entity;

            if (level == null || (entity = level.getEntity(this.entityId)) == null)
                return;

            GeoAnimatable animatable = this.isReplacedEntity ?
                                       RenderUtil.getReplacedAnimatable(entity.getType()) :
                                       entity instanceof GeoAnimatable entityAnimatable ? entityAnimatable : null;

            if (animatable instanceof StatelessAnimatable statelessAnimatable)
                statelessAnimatable.handleClientAnimationPlay(animatable, this.entityId, this.animation);
        });
    }
}
