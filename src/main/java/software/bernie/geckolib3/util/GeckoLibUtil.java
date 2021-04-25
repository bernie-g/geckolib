package software.bernie.geckolib3.util;

import java.util.Objects;

import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GeckoLibUtil {
	public static int getIDFromStack(ItemStack stack) {
		return Objects.hash(stack.getItem().getRegistryName(), stack.getTag(), stack.getCount());
	}

	public static AnimationController getControllerForStack(AnimationFactory factory, ItemStack stack,
			String controllerName) {
		return getControllerForID(factory, getIDFromStack(stack), controllerName);
	}

	public static AnimationController getControllerForID(AnimationFactory factory, Integer id, String controllerName) {
		return factory.getOrCreateAnimationData(id).getAnimationControllers().get(controllerName);
	}
}
