package software.bernie.geckolib.util;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.object.EasingType;
import software.bernie.geckolib.animation.object.LoopType;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.loading.object.BakedModelFactory;

import java.util.Objects;

/**
 * Helper class for various GeckoLib-specific functions.
 */
public final class GeckoLibUtil {
	/**
	 * Creates a new AnimatableInstanceCache for the given animatable object
	 *
	 * @param animatable The animatable object
	 */
	public static AnimatableInstanceCache createInstanceCache(GeoAnimatable animatable) {
		AnimatableInstanceCache cache = animatable.animatableCacheOverride();

		return cache != null ? cache : createInstanceCache(animatable, !(animatable instanceof Entity) && !(animatable instanceof BlockEntity));
	}

	/**
	 * Creates a new AnimatableInstanceCache for the given animatable object
	 * <p>
	 * Recommended to use {@link GeckoLibUtil#createInstanceCache(GeoAnimatable)} unless you know what you're doing
	 *
	 * @param animatable The animatable object
	 * @param singletonObject Whether the object is a singleton/flyweight object, and uses ints to differentiate animatable instances
	 */
	public static AnimatableInstanceCache createInstanceCache(GeoAnimatable animatable, boolean singletonObject) {
		AnimatableInstanceCache cache = animatable.animatableCacheOverride();

		if (cache != null)
			return cache;

		return singletonObject ? new SingletonAnimatableInstanceCache(animatable) : new InstancedAnimatableInstanceCache(animatable);
	}

	/**
	 * Register a custom {@link LoopType} with GeckoLib, allowing for dynamic handling of post-animation looping
	 * <p>
	 * <b><u>MUST be called during mod construct</u></b>
	 *
	 * @param name The name of the {@code LoopType} handler
	 * @param loopType The {@code LoopType} implementation to use for the given name
	 */
	synchronized public static LoopType addCustomLoopType(String name, LoopType loopType) {
		return LoopType.register(name, loopType);
	}

	/**
	 * Register a custom {@link EasingType} with GeckoLib allowing for dynamic handling of animation transitions and curves
	 * <p>
	 * <b><u>MUST be called during mod construct</u></b>
	 *
	 * @param name The name of the {@code EasingType} handler
	 * @param easingType The {@code EasingType} implementation to use for the given name
	 */
	synchronized public static EasingType addCustomEasingType(String name, EasingType easingType) {
		return EasingType.register(name, easingType);
	}

	/**
	 * Register a custom {@link BakedModelFactory} with GeckoLib, allowing for dynamic handling of geo model loading
	 * <p>
	 * <b><u>MUST be called during mod construct</u></b>
	 *
	 * @param namespace The namespace (modid) to register the factory for
	 * @param factory The factory responsible for model loading under the given namespace
	 */
	synchronized public static void addCustomBakedModelFactory(String namespace, BakedModelFactory factory) {
		BakedModelFactory.register(namespace, factory);
	}

	/**
	 * Register a custom {@link SerializableDataTicket} with GeckoLib for handling custom data transmission
	 * <p>
	 * NOTE: You do not need to register non-serializable {@link DataTicket DataTickets}.
	 * <p>
	 * <b><u>MUST be called during mod construct</u></b>
	 *
	 * @param dataTicket The SerializableDataTicket to register
	 * @return The dataTicket you passed in
	 */
	synchronized public static <D> SerializableDataTicket<D> addDataTicket(SerializableDataTicket<D> dataTicket) {
		return DataTickets.registerSerializable(dataTicket);
	}

	/**
	 * Perform an {@link Object#equals(Object)} check on two {@link PatchedDataComponentMap}s,
	 * ignoring any GeckoLib stack ids that may be present.
	 * <p>
	 * This is typically only called by an internal mixin
	 */
	@ApiStatus.Internal
	public static boolean areComponentsMatchingIgnoringGeckoLibId(PatchedDataComponentMap map1, PatchedDataComponentMap map2) {
		final DataComponentType<Long> stackId = GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get();
		boolean patched = false;

		if (map1.has(stackId)) {
			PatchedDataComponentMap prevMap = map1;
			boolean copyOnWrite = prevMap.copyOnWrite;
			(map1 = map1.copy()).remove(stackId);
			map1.copyOnWrite = copyOnWrite;
			patched = true;
		}

		if (map2.has(stackId)) {
			PatchedDataComponentMap prevMap = map2;
			boolean copyOnWrite = prevMap.copyOnWrite;
			(map2 = map2.copy()).remove(stackId);
			map2.copyOnWrite = copyOnWrite;
			patched = true;
		}

		return patched && Objects.equals(map1, map2);
	}

    private GeckoLibUtil() {}
}
