package software.bernie.geckolib3.core.animation.factory;

import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationData;

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
	 * For itemstacks, this is typically a hashcode of their nbt. {@link Entity Entities} and {@link BlockEntity BlockEntities}
	 * can pass anything, as they typically only have one {@link AnimationData} per factory anyway
	 * @param uniqueID A unique integer ID. For every ID the same animation manager
	 *                 will be returned.
	 */
	public abstract <T extends GeoAnimatable> AnimationData<T> getOrCreateAnimationData(int uniqueID);
}
