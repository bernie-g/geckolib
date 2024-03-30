package software.bernie.geckolib.service;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.network.packet.*;

/**
 * Loader-agnostic service interface for GeckoLib's networking functionalities
 */
public interface GeckoLibNetworking {
    static void init() {
        registerPacket(BlockEntityAnimTriggerPacket.ID, true, BlockEntityAnimTriggerPacket.class, BlockEntityAnimTriggerPacket::decode);
        registerPacket(BlockEntityDataSyncPacket.ID, true, BlockEntityDataSyncPacket.class, BlockEntityDataSyncPacket::decode);
        registerPacket(EntityAnimTriggerPacket.ID, true, EntityAnimTriggerPacket.class, EntityAnimTriggerPacket::decode);
        registerPacket(EntityDataSyncPacket.ID, true, EntityDataSyncPacket.class, EntityDataSyncPacket::decode);
        registerPacket(SingletonAnimTriggerPacket.ID, true, SingletonAnimTriggerPacket.class, SingletonAnimTriggerPacket::decode);
        registerPacket(SingletonDataSyncPacket.ID, true, SingletonDataSyncPacket.class, SingletonDataSyncPacket::decode);
    }

    /**
     * Register a GeckoLib packet for use
     */
    @ApiStatus.Internal
    private static <P extends MultiloaderPacket> void registerPacket(ResourceLocation id, boolean isClientBound, Class<P> messageType, FriendlyByteBuf.Reader<P> decoder) {
        GeckoLibServices.NETWORK.registerPacketInternal(id, isClientBound, messageType, decoder);
    }

    /**
     * Register a GeckoLib packet for use
     * <p>
     * <b><u>FOR GECKOLIB USE ONLY</u></b>
     */
    @ApiStatus.Internal
    <P extends MultiloaderPacket> void registerPacketInternal(ResourceLocation id, boolean isClientBound, Class<P> messageType, FriendlyByteBuf.Reader<P> decoder);

    /**
     * Send a packet to all players currently tracking a given entity
     * <p>
     * Good as a shortcut for sending a packet to all players that may have an interest in a given entity or its dealings
     * <p>
     * Will also send the packet to the entity itself if the entity is also a player
     */
    void sendToAllPlayersTrackingEntity(MultiloaderPacket packet, Entity trackingEntity);

    /**
     * Send a packet to all players tracking a given block position
     */
    void sendToAllPlayersTrackingBlock(MultiloaderPacket packet, ServerLevel level, BlockPos pos);

    /**
     * Send a packet to the given player
     */
    void sendToPlayer(MultiloaderPacket packet, ServerPlayer player);

    /**
     * Sync a {@link SerializableDataTicket} from server to clientside for the given block
     */
    default <D> void syncBlockEntityAnimData(BlockPos pos, SerializableDataTicket<D> dataTicket, D data, ServerLevel level) {
        sendToAllPlayersTrackingBlock(new BlockEntityDataSyncPacket<>(pos, dataTicket, data), level, pos);
    }
    /**
     * {@link software.bernie.geckolib.animatable.GeoBlockEntity#triggerAnim(String, String) Trigger} an animation for the given {@link software.bernie.geckolib.animatable.GeoBlockEntity GeoBlockEntity}
     */
    default void triggerBlockEntityAnim(BlockPos pos, @Nullable String controllerName, String animName, ServerLevel level) {
        sendToAllPlayersTrackingBlock(new BlockEntityAnimTriggerPacket(pos, controllerName, animName), level, pos);
    }

    /**
     * Sync a {@link SerializableDataTicket} from server to clientside for the given entity
     */
    default <D> void syncEntityAnimData(Entity entity, boolean isReplacedEntity, SerializableDataTicket<D> dataTicket, D data) {
        sendToAllPlayersTrackingEntity(new EntityDataSyncPacket<>(entity.getId(), isReplacedEntity, dataTicket, data), entity);
    }
    /**
     * {@link software.bernie.geckolib.animatable.GeoEntity#triggerAnim(String, String) Trigger} an animation for the given {@link software.bernie.geckolib.animatable.GeoEntity GeoEntity}
     */
    default void triggerEntityAnim(Entity entity, boolean isReplacedEntity, @Nullable String controllerName, String animName) {
        sendToAllPlayersTrackingEntity(new EntityAnimTriggerPacket(entity.getId(), isReplacedEntity, controllerName, animName), entity);
    }

    /**
     * Sync a {@link SerializableDataTicket} from server to clientside for the given {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable SingletonGeoAnimatable}
     */
    default <D> void syncSingletonAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
        sendToAllPlayersTrackingEntity(new SingletonDataSyncPacket<>(getClass().toString(), instanceId, dataTicket, data), entityToTrack);
    }
    /**
     * {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable#triggerAnim(Entity, long, String, String) Trigger} an animation for the given {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable SingletonGeoAnimatable}
     */
    default void triggerSingletonAnim(String animatableClassName, Entity entityToTrack, long instanceId, @Nullable String controllerName, String animName) {
        sendToAllPlayersTrackingEntity(new SingletonAnimTriggerPacket(animatableClassName, instanceId, controllerName, animName), entityToTrack);
    }
}
