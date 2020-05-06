package software.bernie.geckolib;

import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationControllerCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface IAnimatedEntity
{
	AnimationControllerCollection getAnimationControllers();
}
