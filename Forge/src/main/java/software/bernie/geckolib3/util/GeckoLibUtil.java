package software.bernie.geckolib3.util;

import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.manager.InstancedAnimationFactory;
import software.bernie.geckolib3.core.manager.SingletonAnimationFactory;
import software.bernie.geckolib3.world.storage.GeckoLibIdTracker;

import java.util.Objects;

import static software.bernie.geckolib3.world.storage.GeckoLibIdTracker.Type.ITEM;

public class GeckoLibUtil {
	private static final String GECKO_LIB_ID_NBT = "GeckoLibID";

	/**
	 * Tries to return the ID stored in the stack's NBT data.
	 * <p>
	 * If no ID exists, it will calculate a pseudo-unique one based on the stack's
	 * item, its NBT data, and the stack's size.
	 */
	public static int getIDFromStack(ItemStack stack) {
		if (stackHasIDTag(stack)) {
			return stack.getTag().getInt(GECKO_LIB_ID_NBT);
		}
		return Objects.hash(stack.getItem().toString(), stack.getTag(), stack.getCount());
	}

	/**
	 * This generates a new ID and writes it to the stack's NBT data, if it doesn't
	 * already have an ID on it.
	 * <p>
	 * Note: This may make items unstackable since it modifies their NBT data. Only
	 * use on items with a max stack size of one, or if you know what you're doing.
	 */
	public static void writeIDToStack(ItemStack stack, ServerLevel world) {
		if (!stackHasIDTag(stack)) {
			final int id = GeckoLibIdTracker.from(world).getNextId(ITEM);
			stack.getOrCreateTag().putInt(GECKO_LIB_ID_NBT, id);
		}
	}

	/**
	 * Convenience method that combines the effects of both
	 * {@linkplain #getIDFromStack} and {@linkplain #writeIDToStack}.
	 * <p>
	 * Will always return a unique ID that's stored in the stack's NBT data.
	 */
	public static int guaranteeIDForStack(ItemStack stack, ServerLevel world) {
		if (!stackHasIDTag(stack)) {
			final int id = GeckoLibIdTracker.from(world).getNextId(ITEM);
			stack.getOrCreateTag().putInt(GECKO_LIB_ID_NBT, id);
			return id;
		} else {
			return stack.getTag().getInt(GECKO_LIB_ID_NBT);
		}
	}

	/**
	 * Removes the unique ID from the given stack, if present.
	 */
	public static void removeIDFromStack(ItemStack stack) {
		if (stackHasIDTag(stack)) {
			stack.getTag().remove(GECKO_LIB_ID_NBT);
		}
	}

	/**
	 * Returns true if the stack has an ID stored in its NBT data.
	 */
	public static boolean stackHasIDTag(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(GECKO_LIB_ID_NBT, Tag.TAG_INT);
	}

	public static AnimationController getControllerForStack(AnimationFactory factory, ItemStack stack,
			String controllerName) {
		return getControllerForID(factory, getIDFromStack(stack), controllerName);
	}

	public static AnimationController getControllerForID(AnimationFactory factory, Integer id, String controllerName) {
		return factory.getOrCreateAnimationData(id.intValue()).getAnimationControllers().get(controllerName);
	}

	/**
	 * Creates a new AnimationFactory for the given animatable object
	 * @param animatable The animatable object
	 * @return A new AnimationFactory instance
	 */
	public static AnimationFactory createFactory(IAnimatable animatable) {
		return createFactory(animatable, !(animatable instanceof Entity) && !(animatable instanceof BlockEntity));
	}

	/**
	 * Creates a new AnimationFactory for the given animatable object. <br>
	 * Recommended to use {@link GeckoLibUtil#createFactory(IAnimatable)} unless you know what you're doing.
	 * @param animatable The animatable object
	 * @param singletonObject Whether the object is a singleton/flyweight object, and uses ints to differentiate animatable instances
	 * @return A new AnimationFactory instance
	 */
	public static AnimationFactory createFactory(IAnimatable animatable, boolean singletonObject) {
		return singletonObject ? new SingletonAnimationFactory(animatable) : new InstancedAnimationFactory(animatable);
	}
}
