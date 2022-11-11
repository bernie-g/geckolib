package software.bernie.geckolib3.core.animation.factory;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationData;

/**
 * AnimationFactory implementation for singleton/flyweight objects such as Items. Utilises a keyed map to differentiate different instances of the object.
 */
public class SingletonAnimationFactory extends AnimationFactory {
	private final Int2ObjectOpenHashMap<AnimationData<?>> animationDataMap = new Int2ObjectOpenHashMap<>();

	public SingletonAnimationFactory(GeoAnimatable animatable) {
		super(animatable);
	}

	/**
	 * Gets an {@link AnimationData} instance from this factory cached under the id provided, or a new one if one doesn't already exist.<br>
	 * This factory subclass assumes that all animatable instances will be sharing this factory instance, and so differentiates data by integer ids.
	 */
	@Override
	public AnimationData<?> getAnimationData(int uniqueId) {
		if (!this.animationDataMap.containsKey(uniqueId)) {
			AnimationData<?> data = new AnimationData<>();

			this.animatable.registerControllers(data);
			this.animationDataMap.put(uniqueId, data);
		}

		return this.animationDataMap.get(uniqueId);
	}
}