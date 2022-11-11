package software.bernie.geckolib3.core.manager;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import software.bernie.geckolib3.core.IAnimatable;

/**
 * TODO 1.20+:
 * <ul>
 *     <li>Remove {@code animationDataMap}</li>
 *     <li>Make {@code AnimationFactory} abstract</li>
 *     <li>Make {@code getOrCreateAnimationData} abstract</li>
 * </ul>
 */
public class AnimationFactory {
	protected final IAnimatable animatable;
	private final Int2ObjectOpenHashMap<AnimationData> animationDataMap = new Int2ObjectOpenHashMap<>();

	/**
	 * Deprecated, use {@code GeckolibUtil#createFactory(IAnimatable)}
	 * 
	 * @param animatable The animatable object the factory is for
	 */
	@Deprecated(forRemoval = true)
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
	public AnimationData getOrCreateAnimationData(int uniqueID) {
		if (!this.animationDataMap.containsKey(uniqueID)) {
			AnimationData data = new AnimationData();

			this.animatable.registerControllers(data);
			this.animationDataMap.put(uniqueID, data);
		}

		return animationDataMap.get(uniqueID);
	}

	/**
	 * Use {@link AnimationFactory#getOrCreateAnimationData(int)}
	 */
	@Deprecated(forRemoval = true)
	public AnimationData getOrCreateAnimationData(Integer uniqueID) {
		return getOrCreateAnimationData((int)uniqueID);
	}
}
