package com.geckolib.animatable.instance;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

/// AnimatableInstanceCache implementation for singleton/flyweight objects such as Items. Utilises a keyed map to differentiate different instances of the object
///
/// You should **<u>NOT</u>** be instantiating this directly unless you know what you are doing.
/// Use [GeckoLibUtil.createInstanceCache][GeckoLibUtil#createInstanceCache] instead
public class SingletonAnimatableInstanceCache extends AnimatableInstanceCache {
	protected final LoadingCache<Long, AnimatableManager<?>> managers = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5)).build(new CacheLoader<>() {
        @Override
        public AnimatableManager<?> load(Long key) {
            return createManagerForId(key);
        }
    });

    @ApiStatus.Internal
	public SingletonAnimatableInstanceCache(GeoAnimatable animatable) {
		super(animatable);
	}

	/// Gets an [AnimatableManager] instance from this cache, cached under the id provided, or a new one if one doesn't already exist
	///
	/// This subclass assumes that all animatable instances will be sharing this cache instance, and so differentiates data by ids
	@SuppressWarnings("unchecked")
    @Override
	public <T extends GeoAnimatable> AnimatableManager<T> getManagerForId(long uniqueId) {
		try {
			return (AnimatableManager<T>)this.managers.get(uniqueId);
		}
		catch (ExecutionException ignored) {
			final AnimatableManager<T> manager = createManagerForId(uniqueId);

			this.managers.put(uniqueId, manager);

			return manager;
		}
	}

	/// Create a new [AnimatableManager] instance for the given identifier
	protected <T extends GeoAnimatable> AnimatableManager<T> createManagerForId(long uniqueId) {
		return new AnimatableManager<>(this.animatable);
	}
}