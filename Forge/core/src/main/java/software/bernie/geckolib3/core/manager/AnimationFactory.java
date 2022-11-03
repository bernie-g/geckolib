package software.bernie.geckolib3.core.manager;

import software.bernie.geckolib3.core.animatable.GeoAnimatable;

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
	 * This creates or gets the cached animation manager for any unique ID. For
	 * itemstacks, this is typically a hashcode of their nbt. For entities it should
	 * be their unique uuid. For tile entities you can use nbt or just one constant
	 * value since they are not singletons.
	 *
	 * @param uniqueID A unique integer ID. For every ID the same animation manager
	 *                 will be returned.
	 * @return the animatable manager
	 */
	public abstract AnimationData getOrCreateAnimationData(int uniqueID);
}
