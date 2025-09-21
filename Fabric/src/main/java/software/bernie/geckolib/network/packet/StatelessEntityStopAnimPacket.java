package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.stateless.StatelessAnimatable;
import software.bernie.geckolib.animatable.stateless.StatelessGeoEntity;
import software.bernie.geckolib.animatable.stateless.StatelessGeoReplacedEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.RenderUtils;

/**
 * Packet for instructing clients to set a stop state for an animation for a {@link StatelessGeoEntity} or
 * {@link StatelessGeoReplacedEntity}
 */
public class StatelessEntityStopAnimPacket extends AbstractPacket {
    private final int entityId;
    private final boolean isReplacedEntity;

    private final String animation;

    public StatelessEntityStopAnimPacket(int entityId, String animation) {
        this(entityId, false, animation);
    }

    public StatelessEntityStopAnimPacket(int entityId, boolean isReplacedEntity, String animation) {
        this.entityId = entityId;
        this.isReplacedEntity = isReplacedEntity;
        this.animation = animation;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeVarInt(this.entityId);
        buf.writeBoolean(this.isReplacedEntity);

        buf.writeUtf(this.animation);

        return buf;
    }

    @Override
    public ResourceLocation getPacketID() {
        return GeckoLibNetwork.STATELESS_ENTITY_STOP_ANIM_PACKET_ID;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final int entityId = buf.readVarInt();
        final boolean isReplacedEntity = buf.readBoolean();

        final String animation = buf.readUtf();

        client.execute(() -> runOnThread(entityId, isReplacedEntity, animation));
    }

    private static void runOnThread(int entityId, boolean isReplacedEntity, String animation) {
        Entity entity = ClientUtils.getLevel().getEntity(entityId);

        if (entity == null)
            return;

        GeoAnimatable animatable = isReplacedEntity ?
                                   RenderUtils.getReplacedAnimatable(entity.getType()) :
                                   entity instanceof GeoAnimatable entityAnimatable ? entityAnimatable : null;

        if (animatable instanceof StatelessAnimatable statelessAnimatable)
            statelessAnimatable.handleClientAnimationStop(animatable, entityId, animation);
    }
}
