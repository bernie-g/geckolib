package software.bernie.geckolib3.core.animation.factory;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimatableManager;

/**
 * AnimationFactory implementation for singleton/flyweight objects such as Items. Utilises a keyed map to differentiate different instances of the object.
 */
public class SingletonAnimationFactory extends AnimationFactory {
	private final Long2ObjectMap<AnimatableManager<?>> managers = new Long2ObjectOpenHashMap<>();

	public SingletonAnimationFactory(GeoAnimatable animatable) {
		super(animatable);
	}

	/**
	 * Gets an {@link AnimatableManager} instance from this factory cached under the id provided, or a new one if one doesn't already exist.<br>
	 * This factory subclass assumes that all animatable instances will be sharing this factory instance, and so differentiates data by ids.
	 */
	@Override
	public AnimatableManager<?> getManagerForId(long uniqueId) {
		if (!this.managers.containsKey(uniqueId)) {
			AnimatableManager<?> data = new AnimatableManager<>();

			this.animatable.registerControllers(data);
			this.managers.put(uniqueId, data);
		}

		return this.managers.get(uniqueId);
	}
}