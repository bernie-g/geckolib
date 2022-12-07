package software.bernie.geckolib.core.animatable.instance;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;

/**
 * AnimatableInstanceCache implementation for singleton/flyweight objects such as Items. Utilises a keyed map to differentiate different instances of the object.
 */
public class SingletonAnimatableInstanceCache extends AnimatableInstanceCache {
	protected final Long2ObjectMap<AnimatableManager<?>> managers = new Long2ObjectOpenHashMap<>();

	public SingletonAnimatableInstanceCache(GeoAnimatable animatable) {
		super(animatable);
	}

	/**
	 * Gets an {@link AnimatableManager} instance from this cache, cached under the id provided, or a new one if one doesn't already exist.<br>
	 * This subclass assumes that all animatable instances will be sharing this cache instance, and so differentiates data by ids.
	 */
	@Override
	public AnimatableManager<?> getManagerForId(long uniqueId) {
		if (!this.managers.containsKey(uniqueId))
			this.managers.put(uniqueId, new AnimatableManager<>(this.animatable));

		return this.managers.get(uniqueId);
	}
}