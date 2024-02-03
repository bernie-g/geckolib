package software.bernie.geckolib.network;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.packet.*;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Network handling class for GeckoLib.<br>
 * Handles packet registration and some networking functions
 */
public final class GeckoLibNetwork {
    public static final ResourceLocation ANIM_DATA_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "anim_data_sync");
    public static final ResourceLocation ANIM_TRIGGER_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "anim_trigger_sync");

    public static final ResourceLocation ENTITY_ANIM_DATA_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "entity_anim_data_sync");
    public static final ResourceLocation ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "entity_anim_trigger_sync");

    public static final ResourceLocation BLOCK_ENTITY_ANIM_DATA_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "block_entity_anim_data_sync");
    public static final ResourceLocation BLOCK_ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "block_entity_anim_trigger_sync");

    /**
     * Used to register packets that the server sends
     **/
    public static void registerClientReceiverPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ANIM_DATA_SYNC_PACKET_ID, AnimDataSyncPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ANIM_TRIGGER_SYNC_PACKET_ID, AnimTriggerPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(ENTITY_ANIM_DATA_SYNC_PACKET_ID, EntityAnimDataSyncPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID, EntityAnimTriggerPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(BLOCK_ENTITY_ANIM_DATA_SYNC_PACKET_ID, BlockEntityAnimDataSyncPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BLOCK_ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID, BlockEntityAnimTriggerPacket::receive);
    }

    public static void sendToTrackingEntityAndSelf(AbstractPacket packet, Entity entityToTrack) {
        for (ServerPlayer trackingPlayer : PlayerLookup.tracking(entityToTrack)) {
            ServerPlayNetworking.send(trackingPlayer, packet.getPacketID(), packet.encode());
        }

        if (entityToTrack instanceof ServerPlayer serverPlayer)
            ServerPlayNetworking.send(serverPlayer, packet.getPacketID(), packet.encode());
    }

    public static void sendToEntitiesTrackingChunk(AbstractPacket packet, ServerLevel level, BlockPos blockPos) {
        for (ServerPlayer trackingPlayer : PlayerLookup.tracking(level, blockPos)) {
            ServerPlayNetworking.send(trackingPlayer, packet.getPacketID(), packet.encode());
        }
    }
}
