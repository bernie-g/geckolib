package anightdazingzoroark.riftlib.util;

import java.util.Objects;

import net.minecraft.item.ItemStack;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.manager.AnimationFactory;

public class RiftLibUtil {
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
