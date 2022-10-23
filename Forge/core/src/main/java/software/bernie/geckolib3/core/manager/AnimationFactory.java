package software.bernie.geckolib3.core.manager;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib3.core.IAnimatable;

public abstract class AnimationFactory {
	protected final IAnimatable animatable;

	protected AnimationFactory(IAnimatable animatable) {
		this.animatable = animatable;
	}

	/**
	 * This creates or gets the cached animation manager for any unique ID. For
	 * itemstacks, this is typically a hashcode of their nbt. For entities it should
	 * be their unique uuid. For block entities you can use nbt or just one constant
	 * value since they are not singletons.
	 *
	 * @param uniqueID A unique integer ID. For every ID the same animation manager
	 *                 will be returned.
	 * @return the animatable manager
	 */
	public abstract AnimationData getOrCreateAnimationData(Integer uniqueID);

	public static AnimationFactory create(IAnimatable animatable) {
		return animatable instanceof Entity || animatable instanceof BlockEntity ? new InstancedAnimationFactory(animatable) : new SingletonAnimationFactory(animatable);
	}
}
