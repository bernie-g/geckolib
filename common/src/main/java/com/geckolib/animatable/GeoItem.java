package com.geckolib.animatable;

import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animatable.manager.ContextAwareAnimatableManager;
import com.geckolib.cache.AnimatableIdCache;
import com.geckolib.constant.DataTickets;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/// The [GeoAnimatable][GeoAnimatable] interface specific to [Items][net.minecraft.world.item.Item]
///
/// This also applies to armor, as they are just items too.
///
/// @see <a href="https://github.com/bernie-g/geckolib/wiki/Item-Animations">GeckoLib Wiki - Item Animations</a>
/// @see <a href="https://github.com/bernie-g/geckolib/wiki/Armor-Animations">GeckoLib Wiki - Armor Animations</a>
public interface GeoItem extends SingletonGeoAnimatable {
	/// Register this as a synched `GeoAnimatable` instance with GeckoLib's networking functions
	///
	/// This should be called inside the constructor of your object.
	static void registerSyncedAnimatable(SingletonGeoAnimatable animatable) {
		SingletonGeoAnimatable.registerSyncedAnimatable(animatable);
	}

	/// Gets the unique identifying number from this ItemStack's [NBT][Tag],
	/// or [Long#MAX_VALUE] if one hasn't been assigned
	static long getId(ItemStack stack) {
		return Optional.ofNullable(stack.get(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get()))
				.orElse(Long.MAX_VALUE);
	}

	/// Gets the unique identifying number from this ItemStack's [NBT][Tag]
	///
	/// If no ID has been reserved for this stack yet, it will reserve a new id and assign it
	static long getOrAssignId(ItemStack stack, ServerLevel level) {
		if (!(stack.getComponents() instanceof PatchedDataComponentMap components))
			return Long.MAX_VALUE;

		Long id = components.get(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get());

		if (id == null)
			components.set(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get(), id = AnimatableIdCache.getFreeId(level));

		return id;
	}

	/// Whether this item animatable is perspective-aware, handling animations differently depending on the [render perspective][ItemDisplayContext]
	default boolean isPerspectiveAware() {
		return false;
	}

	/// Replaces the default AnimatableInstanceCache for GeoItems if [GeoItem#isPerspectiveAware()] is true, for perspective-dependent handling
	@Override
	default @Nullable AnimatableInstanceCache animatableCacheOverride() {
		if (isPerspectiveAware())
			return new ContextBasedAnimatableInstanceCache(this);

		return SingletonGeoAnimatable.super.animatableCacheOverride();
	}

	/// AnimatableInstanceCache specific to GeoItems, for doing render perspective-based animations
	///
	/// You should **<u>NOT</u>** be instantiating this directly unless you know what you are doing.
	/// Use [GeckoLibUtil.createInstanceCache][GeckoLibUtil#createInstanceCache] instead
	class ContextBasedAnimatableInstanceCache extends SingletonAnimatableInstanceCache {
		public ContextBasedAnimatableInstanceCache(GeoAnimatable animatable) {
			super(animatable);
		}

		/// Gets an [AnimatableManager] instance from this cache, cached under the id provided, or a new one if one doesn't already exist
		///
		/// This subclass assumes that all animatable instances will be sharing this cache instance, and so differentiates data by ids
		@SuppressWarnings("unchecked")
        @Override
		public AnimatableManager<GeoItem> getManagerForId(long uniqueId) {
			if (!this.managers.containsKey(uniqueId))
				this.managers.put(uniqueId, new ContextAwareAnimatableManager<GeoItem, ItemDisplayContext>(this.animatable) {
					@Override
					protected Map<ItemDisplayContext, AnimatableManager<GeoItem>> buildContextOptions(GeoAnimatable animatable) {
						Map<ItemDisplayContext, AnimatableManager<GeoItem>> map = new EnumMap<>(ItemDisplayContext.class);

						for (ItemDisplayContext context : ItemDisplayContext.values()) {
							map.put(context, new AnimatableManager<>(animatable));
						}

						return map;
					}

					@Override
					public ItemDisplayContext getCurrentContext() {
						ItemDisplayContext context = getAnimatableData(DataTickets.ITEM_RENDER_PERSPECTIVE);

						return context == null ? ItemDisplayContext.NONE : context;
					}
				});

			return (AnimatableManager<GeoItem>)this.managers.get(uniqueId);
		}
	}
}
