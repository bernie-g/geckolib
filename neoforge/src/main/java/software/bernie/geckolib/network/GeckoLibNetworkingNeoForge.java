package software.bernie.geckolib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.network.packet.MultiloaderPacket;
import software.bernie.geckolib.service.GeckoLibNetworking;
import software.bernie.geckolib.util.ClientUtil;

import java.util.function.Consumer;

/**
 * NeoForge service implementation for GeckoLib's networking functionalities
 */
public class GeckoLibNetworkingNeoForge implements GeckoLibNetworking {
    private static IPayloadRegistrar registrar = null;

    public static void init(IEventBus modBus) {
        modBus.addListener((Consumer<RegisterPayloadHandlerEvent>) event -> {
            registrar = event.registrar(GeckoLibConstants.MODID);
            GeckoLibNetworking.init();
            registrar = null;
        });
    }

    /**
     * Register a GeckoLib packet for use
     * <p>
     * <b><u>FOR GECKOLIB USE ONLY</u></b>
     */
    @Override
    public <P extends MultiloaderPacket> void registerPacketInternal(ResourceLocation id, boolean isClientBound, Class<P> messageType, FriendlyByteBuf.Reader<P> decoder) {
        registrar.play(id, decoder, (packet, context) -> packet.receiveMessage(context.player().orElseGet(ClientUtil::getClientPlayer), context.workHandler()::execute));
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
        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(trackingEntity).send(packet);
    }

    /**
     * Send a packet to all players tracking a given block position
     */
    @Override
    public void sendToAllPlayersTrackingBlock(MultiloaderPacket packet, ServerLevel level, BlockPos pos) {
        PacketDistributor.TRACKING_CHUNK.with(level.getChunkAt(pos)).send(packet);
    }

    /**
     * Send a packet to the given player
     */
    @Override
    public void sendToPlayer(MultiloaderPacket packet, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(packet);
    }
}
