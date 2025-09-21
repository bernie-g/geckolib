package software.bernie.geckolib.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
 * Network handling class for GeckoLib.
 * <p>
 * Handles packet registration and some networking functions
 */
public final class GeckoLibNetwork {
    public static final ResourceLocation ANIM_DATA_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "anim_data_sync");
    public static final ResourceLocation ANIM_TRIGGER_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "anim_trigger_sync");
    public static final ResourceLocation STOP_TRIGGERED_ANIM_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "stop_triggered_anim");
    public static final ResourceLocation STATELESS_PLAY_ANIM_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "stateless_singleton_play_anim");
    public static final ResourceLocation STATELESS_STOP_ANIM_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "stateless_singleton_stop_anim");

    public static final ResourceLocation ENTITY_ANIM_DATA_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "entity_anim_data_sync");
    public static final ResourceLocation ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "entity_anim_trigger_sync");
    public static final ResourceLocation STOP_TRIGGERED_ENTITY_ANIM_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "stop_triggered_entity_anim");
    public static final ResourceLocation STATELESS_ENTITY_PLAY_ANIM_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "stateless_entity_play_anim");
    public static final ResourceLocation STATELESS_ENTITY_STOP_ANIM_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "stateless_entity_stop_anim");

    public static final ResourceLocation BLOCK_ENTITY_ANIM_DATA_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "block_entity_anim_data_sync");
    public static final ResourceLocation BLOCK_ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "block_entity_anim_trigger_sync");
    public static final ResourceLocation STOP_TRIGGERED_BLOCK_ENTITY_ANIM_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "stop_triggered_block_entity_anim");
    public static final ResourceLocation STATELESS_BLOCK_ENTITY_PLAY_ANIM_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "stateless_block_entity_play_anim");
    public static final ResourceLocation STATELESS_BLOCK_ENTITY_STOP_ANIM_PACKET_ID = new ResourceLocation(GeckoLib.MOD_ID, "stateless_block_entity_stop_anim");

    private static final Int2ObjectMap<String> ANIMATABLE_IDENTITIES = new Int2ObjectOpenHashMap<>();
    public static final Map<String, GeoAnimatable> SYNCED_ANIMATABLES = new Object2ObjectOpenHashMap<>();

    /**
     * Used to register packets that the server sends
     **/
    public static void registerClientReceiverPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ANIM_DATA_SYNC_PACKET_ID, AnimDataSyncPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ANIM_TRIGGER_SYNC_PACKET_ID, AnimTriggerPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STOP_TRIGGERED_ANIM_PACKET_ID, StopTriggeredSingletonAnimPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STATELESS_PLAY_ANIM_PACKET_ID, StatelessSingletonPlayAnimPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STATELESS_STOP_ANIM_PACKET_ID, StatelessSingletonStopAnimPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(ENTITY_ANIM_DATA_SYNC_PACKET_ID, EntityAnimDataSyncPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID, EntityAnimTriggerPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STOP_TRIGGERED_ENTITY_ANIM_PACKET_ID, StopTriggeredEntityAnimPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STATELESS_ENTITY_PLAY_ANIM_PACKET_ID, StatelessEntityPlayAnimPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STATELESS_ENTITY_STOP_ANIM_PACKET_ID, StatelessEntityStopAnimPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(BLOCK_ENTITY_ANIM_DATA_SYNC_PACKET_ID, BlockEntityAnimDataSyncPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BLOCK_ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID, BlockEntityAnimTriggerPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STOP_TRIGGERED_BLOCK_ENTITY_ANIM_PACKET_ID, StopTriggeredBlockEntityAnimPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STATELESS_BLOCK_ENTITY_PLAY_ANIM_PACKET_ID, StatelessBlockEntityPlayAnimPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STATELESS_BLOCK_ENTITY_STOP_ANIM_PACKET_ID, StatelessBlockEntityStopAnimPacket::receive);
    }

    /**
     * Registers a synced {@link GeoAnimatable} object for networking support.<br>
     * It is recommended that you don't call this directly, instead implementing and calling {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable#registerSyncedAnimatable}
     */
    synchronized public static void registerSyncedAnimatable(GeoAnimatable animatable) {
        GeoAnimatable existing = SYNCED_ANIMATABLES.put(getSyncedSingletonAnimatableId(animatable), animatable);

        if (existing == null)
            GeckoLib.LOGGER.debug("Registered SyncedAnimatable for " + animatable.getClass());
    }

    /**
     * Gets a registered synced {@link GeoAnimatable} object by name
     * <p>
     * The input string <b>MUST</b> be the {@link Class#getName()} of the animatable, passed through
     * {@link #getSyncedSingletonAnimatableId(GeoAnimatable)}
     *
     * @param syncedAnimatableId the animatable id
     */
    @Nullable
    public static GeoAnimatable getSyncedAnimatable(String syncedAnimatableId) {
        GeoAnimatable animatable = SYNCED_ANIMATABLES.get(syncedAnimatableId);

        if (animatable == null)
            GeckoLib.LOGGER.error("Attempting to retrieve unregistered synced animatable! (" + syncedAnimatableId + ")");

        return animatable;
    }

    public static void sendWithCallback(AbstractPacket packet, IPacketCallback callback) {
        callback.onReadyToSend(packet);
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

    /**
     * Get a synced singleton animatable's id for use with {@link #SYNCED_ANIMATABLES}
     * <p>
     * This <b><u>MUST</u></b> be used when retrieving from {@link #SYNCED_ANIMATABLES}
     * as this method eliminates class duplication collisions
     */
    public static String getSyncedSingletonAnimatableId(GeoAnimatable animatable) {
        return ANIMATABLE_IDENTITIES.computeIfAbsent(System.identityHashCode(animatable), i -> {
            String baseId = animatable.getClass().getName();
            i = 0;

            while (SYNCED_ANIMATABLES.containsKey(baseId + i)) {
                i++;
            }

            return baseId + i;
        });
    }

    public interface IPacketCallback {
        void onReadyToSend(AbstractPacket packetToSend);
    }
}
