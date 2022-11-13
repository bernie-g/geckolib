package software.bernie.geckolib3.animatable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.model.GeoModel;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.SerializableDataTicket;
import software.bernie.geckolib3.network.packet.EntityAnimDataSyncPacket;
import software.bernie.geckolib3.network.packet.EntityAnimTriggerPacket;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The {@link GeoAnimatable} interface specific to {@link Entity Entities}.
 * This interface is <u>specifically</u> for entities replacing the rendering of other, existing entities.
 * @see software.bernie.example.entity.ReplacedCreeperEntity
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Entity-Animations">GeckoLib Wiki - Entity Animations</a>
 */
public interface GeoReplacedEntity extends SingletonGeoAnimatable {
	@Override
	default Supplier<GeoModel<?>> getGeoModel() {
		return () -> RenderUtils.getGeoModelForEntityType(getReplacingEntityType());
	}

	/**
	 * We don't need this for replaced entities
	 */
	@Override
	default void createRenderer(Consumer<RenderProvider> consumer) {}

	/**
	 * We don't need this for replaced entities
	 */
	@Override
	default Supplier<RenderProvider> getRenderProvider() {
		return () -> null;
	}

	/**
	 * Returns the {@link EntityType} this entity is intending to replace.<br>
	 * This is used for rendering an animation purposes.
	 */
	EntityType<?> getReplacingEntityType();

	/**
	 * Get server-synced animation data via its relevant {@link SerializableDataTicket}.<br>
	 * Should only be used on the <u>client-side</u>.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 * @param entity The entity instance relevant to the data being set
	 * @param dataTicket The data ticket for the data to retrieve
	 * @return The synced data, or null if no data of that type has been synced
	 */
	@Nullable
	default <D> D getAnimData(Entity entity, SerializableDataTicket<D> dataTicket) {
		return getFactory().getManagerForId(entity.getId()).getData(dataTicket);
	}

	/**
	 * Saves an arbitrary syncable piece of data to this animatable's {@link AnimatableManager}.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 * @param relatedEntity An entity related to the state of the data for syncing
	 * @param dataTicket The DataTicket to sync the data for
	 * @param data The data to sync
	 */
	default <D> void setAnimData(Entity relatedEntity, SerializableDataTicket<D> dataTicket, D data) {
		if (relatedEntity.getLevel().isClientSide()) {
			getFactory().getManagerForId(relatedEntity.getId()).setData(dataTicket, data);
		}
		else {
			GeckoLibNetwork.send(new EntityAnimDataSyncPacket<>(relatedEntity.getId(), dataTicket, data), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> relatedEntity));
		}
	}

	/**
	 * Trigger an animation for this Entity, based on the controller name and animation name.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 * @param relatedEntity An entity related to the state of the data for syncing
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link software.bernie.geckolib3.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	default void triggerAnim(Entity relatedEntity, @Nullable String controllerName, String animName) {
		if (relatedEntity.getLevel().isClientSide()) {
			getFactory().getManagerForId(relatedEntity.getId()).tryTriggerAnimation(controllerName, animName);
		}
		else {
			GeckoLibNetwork.send(new EntityAnimTriggerPacket<>(relatedEntity.getId(), controllerName, animName), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> relatedEntity));
		}
	}
}
