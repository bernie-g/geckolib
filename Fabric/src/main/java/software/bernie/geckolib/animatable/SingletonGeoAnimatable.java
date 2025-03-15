package software.bernie.geckolib.animatable;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.network.packet.AnimDataSyncPacket;
import software.bernie.geckolib.network.packet.AnimTriggerPacket;
import software.bernie.geckolib.network.packet.StopTriggeredSingletonAnimPacket;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The {@link GeoAnimatable} interface specific to singleton objects.
 * This primarily applies to armor and items
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Item-Animations">GeckoLib Wiki - Item Animations</a>
 */
public interface SingletonGeoAnimatable extends GeoAnimatable {
    /**
     * Register this as a synched {@code GeoAnimatable} instance with GeckoLib's networking functions.<br>
     * This should be called inside the constructor of your object.
     */
    static void registerSyncedAnimatable(GeoAnimatable animatable) {
        GeckoLibNetwork.registerSyncedAnimatable(animatable);
    }

    /**
     * Get server-synced animation data via its relevant {@link SerializableDataTicket}.<br>
     * Should only be used on the <u>client-side</u>.<br>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param instanceId The animatable's instance id
     * @param dataTicket The data ticket for the data to retrieve
     * @return The synced data, or null if no data of that type has been synced
     */
    @Nullable
    default <D> D getAnimData(long instanceId, SerializableDataTicket<D> dataTicket) {
        return getAnimatableInstanceCache().getManagerForId(instanceId).getData(dataTicket);
    }

    /**
     * Saves an arbitrary piece of syncable data to this animatable's {@link AnimatableManager}.<br>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity An entity related to the state of the data for syncing (E.G. The player holding the item)
     * @param instanceId    The unique id that identifies the specific animatable instance
     * @param dataTicket    The DataTicket to sync the data for
     * @param data          The data to sync
     */
    default <D> void setAnimData(Entity relatedEntity, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
        if (relatedEntity.level().isClientSide()) {
            getAnimatableInstanceCache().getManagerForId(instanceId).setData(dataTicket, data);
        } else {
            syncAnimData(instanceId, dataTicket, data, relatedEntity);
        }
    }

    /**
     * Syncs an arbitrary piece of data to all players targeted by the packetTarget.<br>
     * This method should only be called on the <u>server side</u>.<br>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param instanceId The unique id that identifies the specific animatable instance
     * @param dataTicket The DataTicket to sync the data for
     * @param data       The data to sync
     */
    default <D> void syncAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
        GeckoLibNetwork.sendToTrackingEntityAndSelf(new AnimDataSyncPacket<>(GeckoLibNetwork.getSyncedSingletonAnimatableId(this), instanceId, dataTicket, data), entityToTrack);
    }

    /**
     * Trigger a client-side animation for this GeoAnimatable for the given controller name and animation name.<br>
     * This can be fired from either the client or the server, but optimally you would call it from the server.<br>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity  An entity related to the animatable to trigger the animation for (E.G. The player holding the item)
     * @param instanceId     The unique id that identifies the specific animatable instance
     * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
     * @param animName       The name of animation to trigger. This needs to have been registered with the controller via {@link software.bernie.geckolib.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
     */
    default <D> void triggerAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
        if (relatedEntity.level().isClientSide()) {
            getAnimatableInstanceCache().getManagerForId(instanceId).tryTriggerAnimation(controllerName, animName);
        }
        else {
            GeckoLibNetwork.sendToTrackingEntityAndSelf(new AnimTriggerPacket(GeckoLibNetwork.getSyncedSingletonAnimatableId(this), instanceId, controllerName, animName), relatedEntity);
        }
    }

    /**
     * Remotely triggers a client-side animation for this GeoAnimatable for all players targeted by the packetTarget.<br>
     * This method should only be called on the <u>server side</u>.<br>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param instanceId     The unique id that identifies the specific animatable instance
     * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
     * @param animName       The name of animation to trigger. This needs to have been registered with the controller via {@link software.bernie.geckolib.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
     * @param packetCallback The packet callback. Used to call a custom network code
     */
    default void triggerAnim(long instanceId, @Nullable String controllerName, String animName, GeckoLibNetwork.IPacketCallback packetCallback) {
        GeckoLibNetwork.sendWithCallback(new AnimTriggerPacket(GeckoLibNetwork.getSyncedSingletonAnimatableId(this), instanceId, controllerName, animName), packetCallback);
    }

    /**
     * Stop a previously triggered animation for this GeoAnimatable for the given controller name and animation name
     * <p>
     * This can be fired from either the client or the server, but optimally you would call it from the server
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity An entity related to the animatable to trigger the animation for (E.G. The player holding the item)
     * @param instanceId The unique id that identifies the specific animatable instance
     * @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
     * @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
     */
    @ApiStatus.NonExtendable
    default void stopTriggeredAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, @Nullable String animName) {
        if (relatedEntity.level().isClientSide()) {
            AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(instanceId);

            if (animatableManager == null)
                return;

            if (controllerName != null) {
                animatableManager.stopTriggeredAnimation(controllerName, animName);
            }
            else {
                animatableManager.stopTriggeredAnimation(animName);
            }
        }
        else {
            GeckoLibNetwork.sendToTrackingEntityAndSelf(new StopTriggeredSingletonAnimPacket(GeckoLibNetwork.getSyncedSingletonAnimatableId(this), instanceId, controllerName, animName), relatedEntity);
        }
    }

    /**
     * Trigger a client-side animation for this GeoAnimatable's armor rendering for the given controller name and animation name
     * <p>
     * This can be fired from either the client or the server, but optimally you would call it from the server
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity  An entity related to the animatable to trigger the animation for (E.G. The player holding the item)
     * @param instanceId     The unique id that identifies the specific animatable instance
     * @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
     * @param animName       The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}
     */
    @ApiStatus.NonExtendable
    default void triggerArmorAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
        triggerAnim(relatedEntity, -instanceId, controllerName, animName);
    }

    /**
     * Stop a previously triggered animation for this GeoAnimatable's armor rendering for the given controller name and animation name
     * <p>
     * This can be fired from either the client or the server, but optimally you would call it from the server
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity An entity related to the animatable to trigger the animation for (E.G. The player holding the item)
     * @param instanceId The unique id that identifies the specific animatable instance
     * @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
     * @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
     */
    @ApiStatus.NonExtendable
    default void stopTriggeredArmorAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, @Nullable String animName) {
        stopTriggeredAnim(relatedEntity, -instanceId, controllerName, animName);
    }

    /**
     * Override the default handling for instantiating an AnimatableInstanceCache for this animatable.<br>
     * Don't override this unless you know what you're doing.
     */
    @Nullable
    @Override
    default AnimatableInstanceCache animatableCacheOverride() {
        return new SingletonAnimatableInstanceCache(this);
    }

    /**
     * Create your RenderProvider reference here.<br>
     * <b><u>MUST provide an anonymous class</u></b><br>
     * Example Code:
     * <pre>{@code
     * @Override
     * public void createRenderer(Consumer<RenderProvider> consumer) {
     * 	consumer.accept(new RenderProvider() {
     * 		private final GeoArmorRenderer<?> renderer = new MyArmorRenderer();
     *
     *        @Override
     *        GeoArmorRenderer<?> getRenderer(GeoArmor armor) {
     * 			return this.renderer;
     *        }
     *    }
     * }
     * }</pre>
     *
     * @param consumer
     */
    void createRenderer(Consumer<Object> consumer);

    /**
     * Getter for the cached RenderProvider in your class
     */
    Supplier<Object> getRenderProvider();
}
