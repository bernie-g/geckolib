package software.bernie.geckolib3.core.manager;

import java.util.HashMap;

import software.bernie.geckolib3.core.IAnimatable;

public class AnimationFactory {
	private final IAnimatable animatable;
	private HashMap<Integer, AnimationData> animationDataMap = new HashMap<>();

	public AnimationFactory(IAnimatable animatable) {
		this.animatable = animatable;
	}

	/**
	 * This creates or gets the cached animation manager for any unique ID. For
	 * itemstacks, this is typically a hashcode of their nbt. For entities it should
	 * be their unique uuid. For tile entities you can use nbt or just one constant
	 * value since they are not singletons.
	 *
	 * @param uniqueID A unique integer ID. For every ID the same animation manager
	 *                 will be returned.
	 * @return the animatable manager
	 */
	public AnimationData getOrCreateAnimationData(Integer uniqueID) {
		if (!animationDataMap.containsKey(uniqueID)) {
			AnimationData data = new AnimationData();
			animatable.registerControllers(data);
			animationDataMap.put(uniqueID, data);
		}
		return animationDataMap.get(uniqueID);
	}
}
