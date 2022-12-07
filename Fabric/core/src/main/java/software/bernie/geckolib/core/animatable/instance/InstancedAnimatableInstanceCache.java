package software.bernie.geckolib.core.animatable.instance;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;

/**
 * AnimatableInstanceCache implementation for instantiated objects such as Entities or BlockEntities. Returns a single {@link AnimatableManager} instance per cache.
 */
public class InstancedAnimatableInstanceCache extends AnimatableInstanceCache {
	protected AnimatableManager<?> manager;

	public InstancedAnimatableInstanceCache(GeoAnimatable animatable) {
		super(animatable);
	}

	/**
	 * Gets the {@link AnimatableManager} instance from this cache.
	 * Because this cache subclass expects a 1:1 relationship of cache to animatable,
	 * only one {@code AnimatableManager} instance is used
	 */
	@Override
	public AnimatableManager<?> getManagerForId(long uniqueId) {
		if (this.manager == null)
			this.manager = new AnimatableManager<>(this.animatable);

		return this.manager;
	}
}