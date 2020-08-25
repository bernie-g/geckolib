package software.bernie.geckolib.event.predicate;

import software.bernie.geckolib.entity.IAnimatable;

public class SpecialAnimationPredicate<T extends IAnimatable> extends AnimationTestPredicate<T>
{
	public SpecialAnimationPredicate(T entity, double animationTick)
	{
		super(entity, animationTick);
	}
}
