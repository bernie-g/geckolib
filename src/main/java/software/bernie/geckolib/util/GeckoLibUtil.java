package software.bernie.geckolib.util;

import net.minecraft.item.ItemStack;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.manager.AnimationFactory;

import java.util.Objects;

public class GeckoLibUtil
{
	public static int getIDFromStack(ItemStack stack)
	{
		return Objects.hash(stack.getItem(), stack.getCount(), stack.hasTag() ? stack.getTag().toString() : 1);
	}

	public static AnimationController getControllerForStack(AnimationFactory factory, ItemStack stack, String controllerName)
	{
		return factory.getOrCreateAnimationData(getIDFromStack(stack)).getAnimationControllers().get(controllerName);
	}
}
