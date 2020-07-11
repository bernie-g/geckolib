package software.bernie.geckolib.reload;

import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.controller.AnimationController;

import java.util.ArrayList;
import java.util.List;

public class ReloadManager
{
	private static List<AnimatedEntityModel> registeredModels = new ArrayList<>();
	private static List<AnimationController> registeredAnimationControllers = new ArrayList<>();


	public static void registerModel(AnimatedEntityModel model)
	{
		registeredModels.add(model);
	}

	public static void registerAnimationController(AnimationController controller)
	{
		registeredAnimationControllers.add(controller);
	}

	public static List<AnimatedEntityModel> getRegisteredModels()
	{
		return registeredModels;
	}

	public static List<AnimationController> getRegisteredAnimationControllers()
	{
		return registeredAnimationControllers;
	}
}
