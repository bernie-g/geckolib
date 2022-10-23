package software.bernie.geckolib3.core.manager;

import software.bernie.geckolib3.core.IAnimatable;

import java.util.HashMap;

/**
 * AnimationFactory implementation for singleton/flyweight objects such as Items. Utilises a keyed map to differentiate different instances of the object.
 */
public class SingletonAnimationFactory extends AnimationFactory {
	private final HashMap<Integer, AnimationData> animationDataMap = new HashMap<>();

	public SingletonAnimationFactory(IAnimatable animatable) {
		super(animatable);
	}

	@Override
	public AnimationData getOrCreateAnimationData(Integer uniqueID) {
		if (!this.animationDataMap.containsKey(uniqueID)) {
			AnimationData data = new AnimationData();

			this.animatable.registerControllers(data);
			this.animationDataMap.put(uniqueID, data);
		}

		return this.animationDataMap.get(uniqueID);
	}
}
