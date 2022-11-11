package software.bernie.geckolib3.core.animation.factory;

import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationData;
import software.bernie.geckolib3.core.object.DataTicket;

/**
 * The base factory class responsible for returning the {@link AnimationData} for a given instanceof of a {@link GeoAnimatable}.
 * This class is abstracted and not intended for direct use. See either {@link SingletonAnimationFactory} or {@link InstancedAnimationFactory}
 */
public abstract class AnimationFactory {
	protected final GeoAnimatable animatable;

	protected AnimationFactory(GeoAnimatable animatable) {
		this.animatable = animatable;
	}

	/**
	 * This creates or gets the cached animation manager for any unique ID.<br>
	 * For itemstacks, this is typically a hashcode of their nbt. {@code Entities} and {@code BlockEntities}
	 * can pass anything, as they typically only have one {@link AnimationData} per factory anyway
	 * @param uniqueId A unique integer ID. For every ID the same animation manager
	 *                 will be returned.
	 */
	public abstract <T extends GeoAnimatable> AnimationData<T> getAnimationData(int uniqueId);

	/**
	 * Helper method to set a data point in the {@link AnimationData#setData data collection} for this animatable.
	 * @param uniqueId The unique identifier for this animatable instance
	 * @param dataTicket The DataTicket for the data
	 * @param data The data to store
	 */
	public <D> void addDataPoint(int uniqueId, DataTicket<D> dataTicket, D data) {
		getAnimationData(uniqueId).setData(dataTicket, data);
	}

	/**
	 * Helper method to get a data point from the {@link AnimationData#getData data collection} for this animatable.
	 * @param uniqueId The unique identifier for this animatable instance
	 * @param dataTicket The DataTicket for the data
	 */
	public <D> D getDataPoint(int uniqueId, DataTicket<D> dataTicket) {
		return getAnimationData(uniqueId).getData(dataTicket);
	}
}
