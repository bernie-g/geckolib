package com.geckolib.util;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.instance.InstancedAnimatableInstanceCache;
import com.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import com.geckolib.animation.object.EasingType;
import com.geckolib.animation.object.LoopType;
import com.geckolib.cache.GeckoLibResources;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.constant.dataticket.SerializableDataTicket;
import com.geckolib.loading.loader.GeckoLibLoader;

import java.util.Objects;

/// Helper class for various GeckoLib-specific functions.
public final class GeckoLibUtil {
	/// Creates a new AnimatableInstanceCache for the given animatable object
	///
	/// @param animatable The animatable object
	public static AnimatableInstanceCache createInstanceCache(GeoAnimatable animatable) {
		AnimatableInstanceCache cache = animatable.animatableCacheOverride();

		return cache != null ? cache : createInstanceCache(animatable, !(animatable instanceof Entity) && !(animatable instanceof BlockEntity));
	}

	/// Creates a new AnimatableInstanceCache for the given animatable object
	///
	/// Recommended to use [GeckoLibUtil#createInstanceCache(GeoAnimatable)] unless you know what you're doing
	///
	/// @param animatable The animatable object
	/// @param singletonObject Whether the object is a singleton/flyweight object, and uses ints to differentiate animatable instances
	public static AnimatableInstanceCache createInstanceCache(GeoAnimatable animatable, boolean singletonObject) {
		AnimatableInstanceCache cache = animatable.animatableCacheOverride();

		if (cache != null)
			return cache;

		return singletonObject ? new SingletonAnimatableInstanceCache(animatable) : new InstancedAnimatableInstanceCache(animatable);
	}

	/// Register a custom [LoopType] with GeckoLib, allowing for dynamic handling of post-animation looping
	///
	/// **<u>MUST be called during mod construct</u>**
	///
	/// @param name The name of the `LoopType` handler
	/// @param loopType The `LoopType` implementation to use for the given name
	synchronized public static LoopType addCustomLoopType(String name, LoopType loopType) {
		return LoopType.register(name, loopType);
	}

	/// Register a custom [EasingType] with GeckoLib allowing for dynamic handling of animation transitions and curves
	///
	/// **<u>MUST be called during mod construct</u>**
	///
	/// @param name The name of the `EasingType` handler
	/// @param easingType The `EasingType` implementation to use for the given name
	synchronized public static EasingType addCustomEasingType(String name, EasingType easingType) {
		return EasingType.register(name, easingType);
	}

	/// Register a zero-parameter [EasingType] with GeckoLib for handling animation transitions and value curves
	///
	/// **<u>MUST be called during mod construct</u>**
	///
	/// @param name The name of the `EasingType` handler
	/// @param function The interpolation function for this easing type
	synchronized public static EasingType addCustomSimpleEasingType(String name, Double2DoubleFunction function) {
		return EasingType.registerSimple(name, function);
	}

	/// Register a custom [GeckoLibLoader] with GeckoLib for handling custom resource loading
	///
	/// **<u>MUST be called during mod construct</u>**
	///
	/// @param predicate A predicate that determines whether the given resource should be handled by the associated loader
	synchronized public static void addResourceLoader(GeckoLibLoader.Predicate predicate, GeckoLibLoader<?> loader) {
		GeckoLibResources.addLoader(predicate, loader);
	}

	/// Register a custom [SerializableDataTicket] with GeckoLib for handling custom data transmission
	///
	/// NOTE: You do not need to register non-serializable [DataTickets][DataTicket].
	///
	/// **<u>MUST be called during mod construct</u>**
	///
	/// @param dataTicket The SerializableDataTicket to register
	/// @return The dataTicket you passed in
	synchronized public static <D> SerializableDataTicket<D> addDataTicket(SerializableDataTicket<D> dataTicket) {
		return DataTickets.registerSerializable(dataTicket);
	}

	/// Perform an [Object#equals(Object)] check on two [PatchedDataComponentMap]s,
	/// ignoring any GeckoLib stack ids that may be present.
	///
	/// This is typically only called by an internal mixin
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
