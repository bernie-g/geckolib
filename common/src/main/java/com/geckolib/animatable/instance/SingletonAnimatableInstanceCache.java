package com.geckolib.animatable.instance;

import com.geckolib.util.GeckoLibUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.manager.AnimatableManager;

/// AnimatableInstanceCache implementation for singleton/flyweight objects such as Items. Utilises a keyed map to differentiate different instances of the object
///
/// You should **<u>NOT</u>** be instantiating this directly unless you know what you are doing.
/// Use [GeckoLibUtil.createInstanceCache][GeckoLibUtil#createInstanceCache] instead
public class SingletonAnimatableInstanceCache extends AnimatableInstanceCache {
	protected final Long2ObjectMap<AnimatableManager<?>> managers = new Long2ObjectOpenHashMap<>();

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
		return (AnimatableManager<T>)this.managers.computeIfAbsent(uniqueId, key -> new AnimatableManager<>(this.animatable));
	}
}