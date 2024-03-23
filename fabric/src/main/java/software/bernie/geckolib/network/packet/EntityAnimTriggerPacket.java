package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.RenderUtil;

/**
 * Packet for syncing user-definable animations that can be triggered from the
 * server for {@link net.minecraft.world.entity.Entity Entities}
 */
public class EntityAnimTriggerPacket extends AbstractPacket {
    private final int entityId;
    private final boolean isReplacedEntity;

    private final String controllerName;
    private final String animName;

    public EntityAnimTriggerPacket(int entityId, @Nullable String controllerName, String animName) {
        this(entityId, false, controllerName, animName);
    }

    public EntityAnimTriggerPacket(int entityId, boolean isReplacedEntity, @Nullable String controllerName,
                                   String animName) {
        this.entityId = entityId;
        this.isReplacedEntity = isReplacedEntity;
        this.controllerName = controllerName == null ? "" : controllerName;
        this.animName = animName;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeVarInt(this.entityId);
        buf.writeBoolean(this.isReplacedEntity);

        buf.writeUtf(this.controllerName);
        buf.writeUtf(this.animName);

        return buf;
    }

    @Override
    public ResourceLocation getPacketID() {
        return GeckoLibNetwork.ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final int entityId = buf.readVarInt();
        final boolean isReplacedEntity = buf.readBoolean();

        final String controllerName = buf.readUtf();
        final String animName = buf.readUtf();

        client.execute(() -> runOnThread(entityId, isReplacedEntity, controllerName, animName));
    }

    private static void runOnThread(int entityId, boolean isReplacedEntity, String controllerName, String animName) {
        Entity entity = ClientUtil.getLevel().getEntity(entityId);
        if (entity == null)
            return;

        if (!isReplacedEntity) {
            if (entity instanceof GeoEntity geoEntity) {
                geoEntity.triggerAnim(controllerName.isEmpty() ? null : controllerName, animName);
            }
            return;
        }

        GeoAnimatable animatable = RenderUtil.getReplacedAnimatable(entity.getType());
        if (animatable instanceof GeoReplacedEntity replacedEntity)
            replacedEntity.triggerAnim(entity, controllerName.isEmpty() ? null : controllerName, animName);
    }
}
