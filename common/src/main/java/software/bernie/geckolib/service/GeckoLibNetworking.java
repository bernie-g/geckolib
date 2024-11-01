package software.bernie.geckolib.service;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.network.packet.*;

import java.util.Optional;

/**
 * Loader-agnostic service interface for GeckoLib's networking functionalities
 */
public interface GeckoLibNetworking {
    static void init() {
        registerPacket(BlockEntityAnimTriggerPacket.TYPE, BlockEntityAnimTriggerPacket.CODEC, true);
        registerPacket(BlockEntityDataSyncPacket.TYPE, BlockEntityDataSyncPacket.CODEC, true);
        registerPacket(EntityAnimTriggerPacket.TYPE, EntityAnimTriggerPacket.CODEC, true);
        registerPacket(EntityDataSyncPacket.TYPE, EntityDataSyncPacket.CODEC, true);
        registerPacket(SingletonAnimTriggerPacket.TYPE, SingletonAnimTriggerPacket.CODEC, true);
        registerPacket(SingletonDataSyncPacket.TYPE, SingletonDataSyncPacket.CODEC, true);
    }

    /**
     * Register a GeckoLib packet for use
     */
    @ApiStatus.Internal
    private static <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacket(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean isClientBound) {
        GeckoLibServices.NETWORK.registerPacketInternal(payloadType, codec, isClientBound);
    }

    /**
     * Register a GeckoLib packet for use
     * <p>
     * <b><u>FOR GECKOLIB USE ONLY</u></b>
     */
    @ApiStatus.Internal
    <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean isClientBound);

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
     * Sync a {@link SerializableDataTicket} from server to clientside for the given entity
     */
    default <D> void syncEntityAnimData(Entity entity, boolean isReplacedEntity, SerializableDataTicket<D> dataTicket, D data) {
        sendToAllPlayersTrackingEntity(new EntityDataSyncPacket<>(entity.getId(), isReplacedEntity, dataTicket, data), entity);
    }

    /**
     * Sync a {@link SerializableDataTicket} from server to clientside for the given {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable SingletonGeoAnimatable}
     */
    default <D> void syncSingletonAnimData(Class<?> animatableClass, long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
        sendToAllPlayersTrackingEntity(new SingletonDataSyncPacket<>(animatableClass.getName(), instanceId, dataTicket, data), entityToTrack);
    }

    /**
     * {@link software.bernie.geckolib.animatable.GeoBlockEntity#triggerAnim(String, String) Trigger} an animation for the
     * given {@link software.bernie.geckolib.animatable.GeoBlockEntity GeoBlockEntity}
     */
    default void triggerBlockEntityAnim(BlockPos pos, ServerLevel level, @Nullable String controllerName, String animName) {
        sendToAllPlayersTrackingBlock(new BlockEntityAnimTriggerPacket(pos, Optional.ofNullable(controllerName), animName), level, pos);
    }

    /**
     * {@link software.bernie.geckolib.animatable.GeoEntity#triggerAnim(String, String) Trigger} an animation for the given
     * {@link software.bernie.geckolib.animatable.GeoEntity GeoEntity}
     */
    default void triggerEntityAnim(Entity entity, boolean isReplacedEntity, @Nullable String controllerName, String animName) {
        sendToAllPlayersTrackingEntity(new EntityAnimTriggerPacket(entity.getId(), isReplacedEntity, Optional.ofNullable(controllerName), animName), entity);
    }

    /**
     * {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable#triggerAnim(Entity, long, String, String) Trigger}
     * an animation for the given {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable SingletonGeoAnimatable}
     */
    default void triggerSingletonAnim(Class<?> animatableClass, Entity entityToTrack, long instanceId, @Nullable String controllerName, String animName) {
        sendToAllPlayersTrackingEntity(new SingletonAnimTriggerPacket(animatableClass.getName(), instanceId, Optional.ofNullable(controllerName), animName), entityToTrack);
    }

    /**
     * {@link software.bernie.geckolib.animatable.GeoBlockEntity#stopTriggeredAnim(String, String) Stop} a previously triggered
     * animation for the given {@link software.bernie.geckolib.animatable.GeoBlockEntity GeoBlockEntity}
     */
    default void stopTriggeredBlockEntityAnim(BlockPos pos, ServerLevel level, @Nullable String controllerName, @Nullable String animName) {
        sendToAllPlayersTrackingBlock(new StopTriggeredBlockEntityAnimPacket(pos, Optional.ofNullable(controllerName), Optional.ofNullable(animName)), level, pos);
    }

    /**
     * {@link software.bernie.geckolib.animatable.GeoEntity#stopTriggeredAnim(String, String) Stop} a previously triggered
     * animation for the given {@link software.bernie.geckolib.animatable.GeoEntity GeoEntity}
     */
    default void stopTriggeredEntityAnim(Entity entity, boolean isReplacedEntity, @Nullable String controllerName, @Nullable String animName) {
        sendToAllPlayersTrackingEntity(new StopTriggeredEntityAnimPacket(entity.getId(), isReplacedEntity, Optional.ofNullable(controllerName), Optional.ofNullable(animName)), entity);
    }

    /**
     * {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable#stopTriggeredAnim(Entity, long, String, String) Stop}
     * a previously triggered animation for the given {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable SingletonGeoAnimatable}
     */
    default void stopTriggeredSingletonAnim(Class<?> animatableClass, Entity entityToTrack, long instanceId, @Nullable String controllerName, @Nullable String animName) {
        sendToAllPlayersTrackingEntity(new StopTriggeredSingletonAnimPacket(animatableClass.getName(), instanceId, Optional.ofNullable(controllerName), Optional.ofNullable(animName)), entityToTrack);
    }
}
