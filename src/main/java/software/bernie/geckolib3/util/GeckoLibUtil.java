package software.bernie.geckolib3.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.world.storage.GeckoLibIdTracker;

import static software.bernie.geckolib3.world.storage.GeckoLibIdTracker.Type.ITEM;

public class GeckoLibUtil {
    private static final String GECKO_LIB_ID_NBT = "GeckoLibID";

    public static Integer getIDFromStack(ItemStack stack) {
        if (stack.hasTag()) {
            final CompoundNBT tag = stack.getTag();
            if (tag.contains(GECKO_LIB_ID_NBT, 99)) {
                return tag.getInt(GECKO_LIB_ID_NBT);
            }
        }
        return null;
    }

    public static void ensureStackIDExists(ItemStack stack, ServerWorld world) {
        if (getIDFromStack(stack) == null) {
            final int nextId = GeckoLibIdTracker.from(world).getNextId(ITEM);
            stack.getOrCreateTag().putInt(GECKO_LIB_ID_NBT, nextId);
        }
    }

    public static AnimationController getControllerForStack(AnimationFactory factory, ItemStack stack,
                                                            String controllerName) {
        return factory.getOrCreateAnimationData(getIDFromStack(stack)).getAnimationControllers().get(controllerName);
    }
}
