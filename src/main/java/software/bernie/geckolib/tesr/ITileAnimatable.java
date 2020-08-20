package software.bernie.geckolib.tesr;

import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.manager.AnimationManager;

public interface ITileAnimatable extends IAnimatable
{
	@Override
	BlockAnimationManager getAnimationManager();
}
