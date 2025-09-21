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
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.NetworkUtil;
import software.bernie.geckolib.util.RenderUtils;

/**
 * Packet for instructing clients to set a play state for an animation for a {@link StatelessGeoEntity} or
 * {@link StatelessGeoReplacedEntity}
 */
public class StatelessEntityPlayAnimPacket extends AbstractPacket {
    private final int entityId;
    private final boolean isReplacedEntity;

    private final RawAnimation animation;

    public StatelessEntityPlayAnimPacket(int entityId, RawAnimation animation) {
        this(entityId, false, animation);
    }

    public StatelessEntityPlayAnimPacket(int entityId, boolean isReplacedEntity, RawAnimation animation) {
        this.entityId = entityId;
        this.isReplacedEntity = isReplacedEntity;
        this.animation = animation;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeVarInt(this.entityId);
        buf.writeBoolean(this.isReplacedEntity);

        NetworkUtil.writeRawAnimationToBuffer(this.animation, buf);

        return buf;
    }

    @Override
    public ResourceLocation getPacketID() {
        return GeckoLibNetwork.STATELESS_ENTITY_PLAY_ANIM_PACKET_ID;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final int entityId = buf.readVarInt();
        final boolean isReplacedEntity = buf.readBoolean();

        final RawAnimation animation = NetworkUtil.readRawAnimationFromBuffer(buf);

        client.execute(() -> runOnThread(entityId, isReplacedEntity, animation));
    }

    private static void runOnThread(int entityId, boolean isReplacedEntity, RawAnimation animation) {
        Entity entity = ClientUtils.getLevel().getEntity(entityId);

        if (entity == null)
            return;

        GeoAnimatable animatable = isReplacedEntity ?
                                   RenderUtils.getReplacedAnimatable(entity.getType()) :
                                   entity instanceof GeoAnimatable entityAnimatable ? entityAnimatable : null;

        if (animatable instanceof StatelessAnimatable statelessAnimatable)
            statelessAnimatable.handleClientAnimationPlay(animatable, entityId, animation);
    }
}
