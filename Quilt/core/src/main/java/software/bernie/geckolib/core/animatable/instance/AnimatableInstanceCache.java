package software.bernie.geckolib.core.animatable.instance;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.object.DataTicket;

/**
 * The base cache class responsible for returning the {@link AnimatableManager} for a given instanceof of a {@link GeoAnimatable}.
 * This class is abstracted and not intended for direct use. See either {@link SingletonAnimatableInstanceCache} or {@link InstancedAnimatableInstanceCache}
 */
public abstract class AnimatableInstanceCache {
	protected final GeoAnimatable animatable;

	public AnimatableInstanceCache(GeoAnimatable animatable) {
		this.animatable = animatable;
	}

	/**
	 * This creates or gets the cached animatable manager for any unique ID.<br>
	 * For itemstacks, this is typically a reserved ID provided by GeckoLib. {@code Entities} and {@code BlockEntities}
	 * pass their position or int ID. They typically only have one {@link AnimatableManager} per cache anyway
	 * @param uniqueId A unique ID. For every ID the same animation manager
	 *                 will be returned.
	 */
	public abstract <T extends GeoAnimatable> AnimatableManager<T> getManagerForId(long uniqueId);

	/**
	 * Helper method to set a data point in the {@link AnimatableManager#setData manager} for this animatable.
	 * @param uniqueId The unique identifier for this animatable instance
	 * @param dataTicket The DataTicket for the data
	 * @param data The data to store
	 */
	public <D> void addDataPoint(long uniqueId, DataTicket<D> dataTicket, D data) {
		getManagerForId(uniqueId).setData(dataTicket, data);
	}

	/**
	 * Helper method to get a data point from the {@link AnimatableManager#getData data collection} for this animatable.
	 * @param uniqueId The unique identifier for this animatable instance
	 * @param dataTicket The DataTicket for the data
	 */
	public <D> D getDataPoint(long uniqueId, DataTicket<D> dataTicket) {
		return getManagerForId(uniqueId).getData(dataTicket);
	}
}
