package software.bernie.geckolib.model.provider;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.processor.AnimationProcessor;
import software.bernie.geckolib.animation.processor.IBone;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;

import javax.annotation.Nullable;

public interface IAnimatableModelProvider<E>
{
	/**
	 * This resource location needs to point to a json file of your animation file, i.e. "geckolib:animations/frog_animation.json"
	 *
	 * @return the animation file location
	 */
	ResourceLocation getAnimationFileLocation(E animatable);

	default float getCurrentTick()
	{
		return (Util.milliTime() / 50f);
	}

	default void setLivingAnimations(E entity)
	{
		this.setLivingAnimations(entity, null);
	}

	void setLivingAnimations(E entity, @Nullable AnimationTestPredicate customPredicate);

	AnimationProcessor getAnimationProcessor();

	Animation getAnimation(String name, ResourceLocation location);

	/**
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	default IBone getBone(String boneName)
	{
		return this.getAnimationProcessor().getBone(boneName);
	}

}
