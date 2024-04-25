package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.function.Consumer;

public record EntityAnimTriggerPacket(int entityId, boolean isReplacedEntity, String controllerName, String animName) implements MultiloaderPacket {
    public static final CustomPacketPayload.Type<EntityAnimTriggerPacket> TYPE = new Type<>(GeckoLibConstants.id("entity_anim_trigger"));
    public static final StreamCodec<FriendlyByteBuf, EntityAnimTriggerPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            EntityAnimTriggerPacket::entityId,
            ByteBufCodecs.BOOL,
            EntityAnimTriggerPacket::isReplacedEntity,
            ByteBufCodecs.STRING_UTF8,
            EntityAnimTriggerPacket::controllerName,
            ByteBufCodecs.STRING_UTF8,
            EntityAnimTriggerPacket::animName,
            EntityAnimTriggerPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            Entity entity = ClientUtil.getLevel().getEntity(this.entityId);

            if (entity == null)
                return;

            if (!this.isReplacedEntity) {
                if (entity instanceof GeoEntity geoEntity)
                    geoEntity.triggerAnim(this.controllerName.isEmpty() ? null : this.controllerName, this.animName);

                return;
            }

            if (RenderUtil.getReplacedAnimatable(entity.getType()) instanceof GeoReplacedEntity replacedEntity)
                replacedEntity.triggerAnim(entity, this.controllerName.isEmpty() ? null : this.controllerName, this.animName);
        });
    }
}
