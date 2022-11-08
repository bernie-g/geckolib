package software.bernie.geckolib3.core.animation.factory;

import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationData;

/**
 * AnimationFactory implementation for instantiated objects such as Entities or BlockEntities. Returns a single {@link AnimationData} instance per factory.
 */
public class InstancedAnimationFactory extends AnimationFactory {
	private AnimationData<?> animationData;

	public InstancedAnimationFactory(GeoAnimatable animatable) {
		super(animatable);
	}

	/**
	 * Gets the {@link AnimationData} instance for this factory.
	 * Because this factory subclass expects a 1:1 relationship of factory to animatable,
	 * only one {@code AnimationData} instance is used
	 */
	@Override
	public AnimationData<?> getAnimationData(int uniqueId) {
		if (this.animationData == null) {
			this.animationData = new AnimationData<>();

			this.animatable.registerControllers(this.animationData);
		}

		return this.animationData;
	}
}