package software.bernie.geckolib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.network.packet.MultiloaderPacket;
import software.bernie.geckolib.service.GeckoLibNetworking;
import software.bernie.geckolib.util.ClientUtil;

/**
 * Forge service implementation for GeckoLib's networking functionalities
 */
public final class GeckoLibNetworkingForge implements GeckoLibNetworking {
    private static final SimpleChannel CHANNEL = ChannelBuilder.named(GeckoLibConstants.id("main")).simpleChannel();

    public static void init() {
        GeckoLibNetworking.init();
    }

    /**
     * Register a GeckoLib packet for use
     * <p>
     * <b><u>FOR GECKOLIB USE ONLY</u></b>
     */
    @Override
    public <P extends MultiloaderPacket> void registerPacketInternal(ResourceLocation id, boolean isClientBound, Class<P> messageType, FriendlyByteBuf.Reader<P> decoder) {
        CHANNEL.messageBuilder(messageType).encoder(MultiloaderPacket::write).decoder(decoder).consumerMainThread((packet, context) -> {
            packet.receiveMessage(context.getSender() != null ? context.getSender() : ClientUtil.getClientPlayer(), context::enqueueWork);
            context.setPacketHandled(true);
        }).add();
    }

    /**
     * Send a packet to all players currently tracking a given entity
     * <p>
     * Good as a shortcut for sending a packet to all players that may have an interest in a given entity or its dealings
     * <p>
     * Will also send the packet to the entity itself if the entity is also a player
     */
    @Override
    public void sendToAllPlayersTrackingEntity(MultiloaderPacket packet, Entity trackingEntity) {
        CHANNEL.send(packet, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(trackingEntity));
    }

    /**
     * Send a packet to all players tracking a given block position
     */
    @Override
    public void sendToAllPlayersTrackingBlock(MultiloaderPacket packet, ServerLevel level, BlockPos pos) {
        CHANNEL.send(packet, PacketDistributor.TRACKING_CHUNK.with(level.getChunkAt(pos)));
    }

    /**
     * Send a packet to the given player
     */
    @Override
    public void sendToPlayer(MultiloaderPacket packet, ServerPlayer player) {
        CHANNEL.send(packet, PacketDistributor.PLAYER.with(player));
    }
}
