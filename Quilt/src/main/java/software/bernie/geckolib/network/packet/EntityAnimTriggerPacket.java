package software.bernie.geckolib.network.packet;

import javax.annotation.Nullable;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.RenderUtils;

/**
 * Packet for syncing user-definable animations that can be triggered from the
 * server for {@link net.minecraft.world.entity.Entity Entities}
 */
public class EntityAnimTriggerPacket extends AbstractPacket {
    private final int ENTITY_ID;
    private final boolean IS_REPLACED_ENTITY;

    private final String CONTROLLER_NAME;
    private final String ANIM_NAME;

    public EntityAnimTriggerPacket(int entityId, @Nullable String controllerName, String animName) {
        this(entityId, false, controllerName, animName);
    }

    public EntityAnimTriggerPacket(int entityId, boolean isReplacedEntity, @Nullable String controllerName,
                                   String animName) {
        this.ENTITY_ID = entityId;
        this.IS_REPLACED_ENTITY = isReplacedEntity;
        this.CONTROLLER_NAME = controllerName == null ? "" : controllerName;
        this.ANIM_NAME = animName;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeVarInt(this.ENTITY_ID);
        buf.writeBoolean(this.IS_REPLACED_ENTITY);

        buf.writeUtf(this.CONTROLLER_NAME);
        buf.writeUtf(this.ANIM_NAME);

        return buf;
    }

    @Override
    public ResourceLocation getPacketID() {
        return GeckoLibNetwork.ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final int ENTITY_ID = buf.readVarInt();
        final boolean IS_REPLACED_ENTITY = buf.readBoolean();

        final String CONTROLLER_NAME = buf.readUtf();
        final String ANIM_NAME = buf.readUtf();

        client.execute(() -> runOnThread(ENTITY_ID, IS_REPLACED_ENTITY, CONTROLLER_NAME, ANIM_NAME));
    }

    private static void runOnThread(int entityId, boolean isReplacedEntity, String controllerName, String animName) {
        Entity entity = ClientUtils.getLevel().getEntity(entityId);
        if (entity == null)
            return;

        if (!isReplacedEntity) {
            if (entity instanceof GeoEntity geoEntity) {
                geoEntity.triggerAnim(controllerName.isEmpty() ? null : controllerName, animName);
            }
            return;
        }

        GeoAnimatable animatable = RenderUtils.getReplacedAnimatable(entity.getType());
        if (animatable instanceof GeoReplacedEntity replacedEntity)
            replacedEntity.triggerAnim(entity, controllerName.isEmpty() ? null : controllerName, animName);
    }
}
