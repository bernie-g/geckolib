package software.bernie.geckolib3.util;

import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.IAnimatableItem;
import software.bernie.geckolib3.world.storage.GeckoLibIdTracker;

import static software.bernie.geckolib3.world.storage.GeckoLibIdTracker.Type.ITEM;

public class GeckoLibUtil {
    public static void ensureIdExists(IAnimatableItem item, ItemStack stack, ServerWorld world) {
        if (item.getId(stack) != null) {
            final int nextId = GeckoLibIdTracker.from(world).getNextId(ITEM);
            stack.getOrCreateTag().putInt(IAnimatableItem.GECKO_LIB_ID_NBT, nextId);
        }
    }

    public static AnimationController getController(AnimationFactory factory, Integer id, String controllerName) {
        return factory.getOrCreateAnimationData(id).getAnimationControllers().get(controllerName);
    }
}
