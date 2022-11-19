package software.bernie.geckolib.core.animation.factory;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;

/**
 * AnimationFactory implementation for instantiated objects such as Entities or BlockEntities. Returns a single {@link AnimatableManager} instance per factory.
 */
public class InstancedAnimationFactory extends AnimationFactory {
	private AnimatableManager<?> manager;

	public InstancedAnimationFactory(GeoAnimatable animatable) {
		super(animatable);
	}

	/**
	 * Gets the {@link AnimatableManager} instance for this factory.
	 * Because this factory subclass expects a 1:1 relationship of factory to animatable,
	 * only one {@code AnimatableManager} instance is used
	 */
	@Override
	public AnimatableManager<?> getManagerForId(long uniqueId) {
		if (this.manager == null) {
			this.manager = new AnimatableManager<>();

			this.animatable.registerControllers(this.manager);
		}

		return this.manager;
	}
}