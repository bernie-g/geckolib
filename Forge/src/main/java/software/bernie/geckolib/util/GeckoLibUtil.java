package software.bernie.geckolib.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.EasingType;
import software.bernie.geckolib.core.animation.factory.AnimationFactory;
import software.bernie.geckolib.core.animation.factory.InstancedAnimationFactory;
import software.bernie.geckolib.core.animation.factory.SingletonAnimationFactory;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.network.SerializableDataTicket;

/**
 * Helper class for various GeckoLib-specific functions.
 */
public final class GeckoLibUtil {
	/**
	 * Creates a new AnimationFactory for the given animatable object
	 * @param animatable The animatable object
	 * @return A new AnimationFactory instance
	 */
	public static AnimationFactory createFactory(GeoAnimatable animatable) {
		return createFactory(animatable, !(animatable instanceof Entity) && !(animatable instanceof BlockEntity));
	}

	/**
	 * Creates a new AnimationFactory for the given animatable object. <br>
	 * Recommended to use {@link GeckoLibUtil#createFactory(GeoAnimatable)} unless you know what you're doing.
	 * @param animatable The animatable object
	 * @param singletonObject Whether the object is a singleton/flyweight object, and uses ints to differentiate animatable instances
	 * @return A new AnimationFactory instance
	 */
	public static AnimationFactory createFactory(GeoAnimatable animatable, boolean singletonObject) {
		return singletonObject ? new SingletonAnimationFactory(animatable) : new InstancedAnimationFactory(animatable);
	}

	/**
	 * Register a custom {@link software.bernie.geckolib.core.animation.Animation.LoopType} with GeckoLib,
	 * allowing for dynamic handling of post-animation looping.<br>
	 * <b><u>MUST be called during mod construct</u></b><br>
	 * @param name The name of the {@code LoopType} handler
	 * @param loopType The {@code LoopType} implementation to use for the given name
	 */
	synchronized public static Animation.LoopType addCustomLoopType(String name, Animation.LoopType loopType) {
		return Animation.LoopType.register(name, loopType);
	}

	/**
	 * Register a custom {@link software.bernie.geckolib.core.animation.EasingType} with GeckoLib,
	 * allowing for dynamic handling of animation transitions and curves.<br>
	 * <b><u>MUST be called during mod construct</u></b><br>
	 * @param name The name of the {@code EasingType} handler
	 * @param easingType The {@code EasingType} implementation to use for the given name
	 */
	synchronized public static EasingType addCustomEasingType(String name, EasingType easingType) {
		return EasingType.register(name, easingType);
	}

	/**
	 * Register a custom {@link software.bernie.geckolib.loading.object.BakedModelFactory} with GeckoLib,
	 * allowing for dynamic handling of geo model loading.<br>
	 * <b><u>MUST be called during mod construct</u></b><br>
	 * @param namespace The namespace (modid) to register the factory for
	 * @param factory The factory responsible for model loading under the given namespace
	 */
	synchronized public static void addCustomBakedModelFactory(String namespace, BakedModelFactory factory) {
		BakedModelFactory.register(namespace, factory);
	}

	/**
	 * Register a custom {@link SerializableDataTicket} with GeckoLib for handling custom data transmission.<br>
	 * NOTE: You do not need to register non-serializable {@link software.bernie.geckolib.core.object.DataTicket DataTickets}.
	 * @param dataTicket The SerializableDataTicket to register
	 * @return The dataTicket you passed in
	 */
	synchronized public static <D> SerializableDataTicket<D> addDataTicket(SerializableDataTicket<D> dataTicket) {
		return DataTickets.registerSerializable(dataTicket);
	}
}
