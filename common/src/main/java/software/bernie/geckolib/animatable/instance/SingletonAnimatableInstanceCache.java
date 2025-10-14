package software.bernie.geckolib.animatable.instance;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;

/**
 * AnimatableInstanceCache implementation for singleton/flyweight objects such as Items. Utilises a keyed map to differentiate different instances of the object
 * <p>
 * You should <b><u>NOT</u></b> be instantiating this directly unless you know what you are doing.
 * Use {@link software.bernie.geckolib.util.GeckoLibUtil#createInstanceCache GeckoLibUtil.createInstanceCache} instead
 */
public class SingletonAnimatableInstanceCache extends AnimatableInstanceCache {
	protected final Long2ObjectMap<AnimatableManager<?>> managers = new Long2ObjectOpenHashMap<>();

	public SingletonAnimatableInstanceCache(GeoAnimatable animatable) {
		super(animatable);
	}

	/**
	 * Gets an {@link AnimatableManager} instance from this cache, cached under the id provided, or a new one if one doesn't already exist
	 * <p>
	 * This subclass assumes that all animatable instances will be sharing this cache instance, and so differentiates data by ids
	 */
	@Override
	public AnimatableManager<?> getManagerForId(long uniqueId) {
		return this.managers.computeIfAbsent(uniqueId, key -> new AnimatableManager<>(this.animatable));
	}
}