package software.bernie.geckolib3.util;

import java.util.Objects;

import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GeckoLibUtil {
	public static int getIDFromStack(ItemStack stack) {
		return Objects.hash(stack.getItem(), stack.getCount(),
				stack.hasTagCompound() ? stack.getTagCompound().toString() : 1);
	}

    @SuppressWarnings("rawtypes")
	public static AnimationController getControllerForStack(AnimationFactory factory, ItemStack stack,
			String controllerName) {
		return factory.getOrCreateAnimationData(getIDFromStack(stack)).getAnimationControllers().get(controllerName);
	}
}
