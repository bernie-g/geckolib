package software.bernie.geckolib.animatable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.network.packet.AnimDataSyncPacket;
import software.bernie.geckolib.network.packet.AnimTriggerPacket;
import software.bernie.geckolib.mixins.fabric.ItemRendererAccessor;

import javax.annotation.Nullable;
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
        return getFactory().getManagerForId(instanceId).getData(dataTicket);
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
        if (relatedEntity.level.isClientSide()) {
            getFactory().getManagerForId(instanceId).setData(dataTicket, data);
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
        AnimDataSyncPacket<D> animDataSyncPacket = new AnimDataSyncPacket<>(getClass().toString(), instanceId, dataTicket, data);
        GeckoLibNetwork.sendToTrackingEntityAndSelf(animDataSyncPacket, entityToTrack);
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
        if (relatedEntity.level.isClientSide()) {
            getFactory().getManagerForId(instanceId).tryTriggerAnimation(controllerName, animName);
        } else {
            AnimTriggerPacket animTriggerPacket = new AnimTriggerPacket(getClass().toString(), instanceId, controllerName, animName);
            GeckoLibNetwork.sendToTrackingEntityAndSelf(animTriggerPacket, relatedEntity);
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
        AnimTriggerPacket animTriggerPacket = new AnimTriggerPacket(getClass().toString(), instanceId, controllerName, animName);
        GeckoLibNetwork.sendWithCallback(animTriggerPacket, packetCallback);
    }

    /**
     * Internal interface for safely providing a custom renderer instances at runtime.<br>
     * This can be safely instantiated as a new anonymous class inside your {@link Item} class
     */
    interface RenderProvider {

        static RenderProvider of(ItemStack itemStack) {
            return of(itemStack.getItem());
        }

        static RenderProvider of(Item item) {
            GeoItem geoItem = (GeoItem) item;
            return geoItem.getRenderProvider().get();
        }

        default BlockEntityWithoutLevelRenderer getCustomRenderer(){
            return ((ItemRendererAccessor) Minecraft.getInstance().getItemRenderer()).getBlockEntityRenderer();
        }


        default Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<LivingEntity> original) {
            HumanoidModel<LivingEntity> replacement = getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);

            if (replacement != original) {
                original.copyPropertiesTo(replacement);
                return replacement;
            }

            return original;
        }

        default HumanoidModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<LivingEntity> original) {
            return original;
        }
    }

    /**
     * Create your {@link RenderProvider} reference here.<br>
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
    void createRenderer(Consumer<RenderProvider> consumer);

    /**
     * Getter for the cached RenderProvider in your class
     */
    Supplier<RenderProvider> getRenderProvider();
}
