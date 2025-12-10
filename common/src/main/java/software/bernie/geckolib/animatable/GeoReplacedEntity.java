package software.bernie.geckolib.animatable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;

import java.util.function.Consumer;

/**
 * The {@link GeoAnimatable} interface specific to {@link Entity Entities}
 * <p>
 * This interface is <u>specifically</u> for entities replacing the rendering of other, existing entities
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Entity-Animations">GeckoLib Wiki - Entity Animations</a>
 */
public interface GeoReplacedEntity extends SingletonGeoAnimatable {
	/**
	 * Returns the {@link EntityType} this entity is intending to replace
	 * <p>
	 * This is used for rendering and animations
	 */
	EntityType<?> getReplacingEntityType();

	/**
	 * Get server-synced animation data via its relevant {@link SerializableDataTicket}
	 * <p>
	 * Should only be used on the <u>client-side</u>
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param entity The entity instance relevant to the data being set
	 * @param dataTicket The data ticket for the data to retrieve
	 * @return The synced data, or null if no data of that type has been synced
	 */
	@ApiStatus.NonExtendable
	default <D> @Nullable D getAnimData(Entity entity, SerializableDataTicket<D> dataTicket) {
		return getAnimatableInstanceCache().getManagerForId(entity.getId()).getAnimatableData(dataTicket);
	}

	/**
	 * Saves an arbitrary syncable piece of data to this animatable's {@link AnimatableManager}
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param relatedEntity An entity related to the state of the data for syncing
	 * @param dataTicket The DataTicket to sync the data for
	 * @param data The data to sync
	 */
	@ApiStatus.NonExtendable
	default <D> void setAnimData(Entity relatedEntity, SerializableDataTicket<D> dataTicket, D data) {
		if (relatedEntity.level().isClientSide()) {
			getAnimatableInstanceCache().getManagerForId(relatedEntity.getId()).setAnimatableData(dataTicket, data);
		}
		else {
			GeckoLibServices.NETWORK.syncEntityAnimData(relatedEntity, true, dataTicket, data);
		}
	}

	/**
	 * Trigger an animation for this Entity, based on the controller name and animation name
	 * <p>
	 * This can be fired from either the client or the server, but optimally you would call it from the server
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param relatedEntity An entity related to the state of the data for syncing
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	@ApiStatus.NonExtendable
	default void triggerAnim(Entity relatedEntity, @Nullable String controllerName, String animName) {
		if (relatedEntity.level().isClientSide()) {
			AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(relatedEntity.getId());

			if (animatableManager == null)
				return;

			if (controllerName != null) {
				animatableManager.tryTriggerAnimation(controllerName, animName);
			}
			else {
				animatableManager.tryTriggerAnimation(animName);
			}
		}
		else {
			GeckoLibServices.NETWORK.triggerEntityAnim(relatedEntity, true, controllerName, animName);
		}
	}

	/**
	 * Stop a previously triggered animation for this Entity for the given controller name and animation name
	 * <p>
	 * This can be fired from either the client or the server, but optimally you would call it from the server
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param relatedEntity An entity related to the state of the data for syncing
	 * @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
	 * @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
	 */
	@ApiStatus.NonExtendable
	default void stopTriggeredAnim(Entity relatedEntity, @Nullable String controllerName, @Nullable String animName) {
		if (relatedEntity.level().isClientSide()) {
			AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(relatedEntity.getId());

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
			GeckoLibServices.NETWORK.stopTriggeredEntityAnim(relatedEntity, true, controllerName, animName);
		}
	}

	// These methods aren't used for GeoReplacedEntity
	@ApiStatus.NonExtendable
	@Override
	default void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {}

	// These methods aren't used for GeoReplacedEntity
	@ApiStatus.NonExtendable
	@Override
	default Object getRenderProvider() {
		return null;
	}
}
