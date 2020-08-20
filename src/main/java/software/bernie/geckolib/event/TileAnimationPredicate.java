package software.bernie.geckolib.event;

import software.bernie.geckolib.tesr.ITileAnimatable;

public class TileAnimationPredicate<T extends ITileAnimatable> extends AnimationTestPredicate<T>
{
	public TileAnimationPredicate(T entity, double animationTick)
	{
		super(entity, animationTick);
	}
}
