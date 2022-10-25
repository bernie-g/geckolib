package software.bernie.geckolib3.core.manager;

import software.bernie.geckolib3.core.IAnimatable;

/**
 * AnimationFactory implementation for instantiated objects such as Entities or BlockEntities. Returns a single {@link AnimationData} instance per factory.
 */
public class InstancedAnimationFactory extends AnimationFactory {
	private AnimationData animationData;

	public InstancedAnimationFactory(IAnimatable animatable) {
		super(animatable);
	}

	@Override
	public AnimationData getOrCreateAnimationData(int uniqueID) {
		if (this.animationData == null) {
			this.animationData = new AnimationData();

			this.animatable.registerControllers(this.animationData);
		}

		return this.animationData;
	}
}