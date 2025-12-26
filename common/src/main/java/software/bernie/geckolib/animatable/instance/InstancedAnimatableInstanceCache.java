package software.bernie.geckolib.animatable.instance;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;

/**
 * AnimatableInstanceCache implementation for instantiated objects such as Entities or BlockEntities. Returns a single {@link AnimatableManager} instance per cache
 * <p>
 * You should <b><u>NOT</u></b> be instantiating this directly unless you know what you are doing.
 * Use {@link software.bernie.geckolib.util.GeckoLibUtil#createInstanceCache GeckoLibUtil.createInstanceCache} instead
 */
public class InstancedAnimatableInstanceCache extends AnimatableInstanceCache {
	protected final Supplier<AnimatableManager<?>> manager = Suppliers.memoize(() -> new AnimatableManager<>(this.animatable));

	public InstancedAnimatableInstanceCache(GeoAnimatable animatable) {
		super(animatable);
	}

	/**
	 * Gets the {@link AnimatableManager} instance from this cache
	 * <p>
	 * Because this cache subclass expects a 1:1 relationship of cache to animatable, only one {@code AnimatableManager} instance is used
	 */
	@SuppressWarnings("unchecked")
    @Override
	public AnimatableManager<?> getManagerForId(long uniqueId) {
		return this.manager.get();
	}
}