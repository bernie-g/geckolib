package software.bernie.geckolib3.util;

import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

import java.util.Objects;

import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.world.storage.GeckoLibIdTracker;

import static net.minecraftforge.common.util.Constants.NBT.TAG_INT;
import static software.bernie.geckolib3.world.storage.GeckoLibIdTracker.Type.ITEM;

public class GeckoLibUtil {
    private static final String GECKO_LIB_ID_NBT = "GeckoLibID";

    /**
     * Tries to return the ID stored in the stack's NBT data.
     * <p>
     * If no ID exists, it will calculate a pseudo-unique one based on
     * the stack's item, its NBT data, and the stack's size.
     */
    public static int getIDFromStack(ItemStack stack) {
        if (stackHasIDTag(stack)) {
            return stack.getTag().getInt(GECKO_LIB_ID_NBT);
        }
        return Objects.hash(stack.getItem().getRegistryName(), stack.getTag(), stack.getCount());
    }

    /**
     * This generates a new ID and writes it to the stack's NBT data, if it
     * doesn't already have an ID on it.
     * <p>
     * Note: This may make items unstackable since it modifies their NBT data.
     * Only use on items with a max stack size of one, or if you know what
     * you're doing.
     */
    public static void writeIDToStack(ItemStack stack, ServerWorld world) {
        if (!stackHasIDTag(stack)) {
            final int id = GeckoLibIdTracker.from(world).getNextId(ITEM);
            stack.getOrCreateTag().putInt(GECKO_LIB_ID_NBT, id);
        }
    }

    /**
     * Returns true if the stack has an ID stored in its NBT data.
     */
    public static boolean stackHasIDTag(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(GECKO_LIB_ID_NBT, TAG_INT);
    }

    public static AnimationController getControllerForStack(AnimationFactory factory, ItemStack stack,
                                                            String controllerName) {
        return getControllerForID(factory, getIDFromStack(stack), controllerName);
    }

    public static AnimationController getControllerForID(AnimationFactory factory, Integer id,
                                                         String controllerName) {
        return factory.getOrCreateAnimationData(id).getAnimationControllers().get(controllerName);
    }
}
