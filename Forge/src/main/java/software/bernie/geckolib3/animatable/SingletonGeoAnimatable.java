package software.bernie.geckolib3.animatable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.SerializableDataTicket;
import software.bernie.geckolib3.network.packet.AnimDataSyncPacket;
import software.bernie.geckolib3.network.packet.AnimTriggerPacket;
import software.bernie.geckolib3.renderer.GeoArmorRenderer;
import software.bernie.geckolib3.renderer.GeoItemRenderer;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The {@link GeoAnimatable} interface specific to singleton objects.
 * This primarily applies to armor and items
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
	 * @param relatedEntity An entity related to the state of the data for syncing (E.G. The player holding the item)
	 * @param instanceId The unique id that identifies the specific animatable instance
	 * @param dataTicket The DataTicket to sync the data for
	 * @param data The data to sync
	 */
	default <D> void setAnimData(Entity relatedEntity, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
		if (relatedEntity.level.isClientSide()) {
			getFactory().getManagerForId(instanceId).setData(dataTicket, data);
		}
		else {
			syncAnimData(instanceId, dataTicket, data, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> relatedEntity));
		}
	}

	/**
	 * Syncs an arbitrary piece of data to all players targeted by the packetTarget.<br>
	 * This method should only be called on the <u>server side</u>.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param instanceId   The unique id that identifies the specific animatable instance
	 * @param dataTicket   The DataTicket to sync the data for
	 * @param data         The data to sync
	 * @param packetTarget The distribution method determining which players to sync the data to
	 */
	default <D> void syncAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, PacketDistributor.PacketTarget packetTarget) {
		GeckoLibNetwork.send(new AnimDataSyncPacket<>(getClass().toString(), instanceId, dataTicket, data), packetTarget);
	}

	/**
	 * Trigger a client-side animation for this GeoAnimatable for the given controller name and animation name.<br>
	 * This can be fired from either the client or the server, but optimally you would call it from the server.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 * @param relatedEntity An entity related to the animatable to trigger the animation for (E.G. The player holding the item)
	 * @param instanceId The unique id that identifies the specific animatable instance
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link software.bernie.geckolib3.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	default <D> void triggerAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
		if (relatedEntity.level.isClientSide()) {
			getFactory().getManagerForId(instanceId).tryTriggerAnimation(controllerName, animName);
		}
		else {
			GeckoLibNetwork.send(new AnimTriggerPacket<>(getClass().toString(), instanceId, controllerName, animName), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> relatedEntity));
		}
	}

	/**
	 * Remotely triggers a client-side animation for this GeoAnimatable for all players targeted by the packetTarget.<br>
	 * This method should only be called on the <u>server side</u>.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param instanceId     The unique id that identifies the specific animatable instance
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName       The name of animation to trigger. This needs to have been registered with the controller via {@link software.bernie.geckolib3.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 * @param packetTarget   The distribution method determining which players to sync the data to
	 */
	default <D> void triggerAnim(long instanceId, @Nullable String controllerName, String animName, PacketDistributor.PacketTarget packetTarget) {
		GeckoLibNetwork.send(new AnimTriggerPacket<>(getClass().toString(), instanceId, controllerName, animName), packetTarget);
	}

	/**
	 * Internal interface for safely providing a custom renderer instances at runtime.<br>
	 * This can be safely instantiated as a new anonymous class inside your {@link Item} class
	 */
	interface RenderProvider {
		/**
		 * Return a cached instance of the armor renderer for this armor
		 * @param armor The armor for the renderer
		 */
		default GeoArmorRenderer<?> getArmorRenderer(GeoArmor armor) {
			return null;
		}

		/**
		 * Returns a cached instance of the item renderer for this item
		 */
		default GeoItemRenderer<?> getItemRenderer() {
			return null;
		}

		/**
		 * Return the texture path of the armor texture for this model
		 * @param entity The entity related to this render (E.G. The player wearing the armor)
		 * @param stack The ItemStack for this specific armor slot
		 * @param slot The slot being rendered currently
		 * @param type The optional type of the texture. By default is either null or "overlay". See {@link net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer#getArmorResource HumanoidArmorLayer.getArmorResource}
		 */
		default <T extends Item & GeoArmor> String getArmorTexture(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
			GeoArmor armor = (GeoArmor)stack.getItem();
			GeoArmorRenderer<T> renderer = (GeoArmorRenderer<T>)getArmorRenderer(armor);

			return renderer.getTextureLocation((T)armor).toString();
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
	 * 		@Override
	 * 		GeoArmorRenderer<?> getRenderer(GeoArmor armor) {
	 * 			return this.renderer;
	 * 		}
	 * 	}
	 * }
	 * }</pre>
	 * @param consumer
	 */
	void createRenderer(Consumer<RenderProvider> consumer);

	/**
	 * Getter for the cached RenderProvider in your class
	 */
	Supplier<RenderProvider> getRenderProvider();
}
