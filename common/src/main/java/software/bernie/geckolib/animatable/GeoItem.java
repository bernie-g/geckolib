package software.bernie.geckolib.animatable;

import com.google.common.base.Suppliers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.cache.AnimatableIdCache;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.ContextAwareAnimatableManager;
import software.bernie.geckolib.util.RenderUtil;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * The {@link GeoAnimatable GeoAnimatable} interface specific to {@link net.minecraft.world.item.Item Items}
 * <p>
 * This also applies to armor, as they are just items too.
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Item-Animations">GeckoLib Wiki - Item Animations</a>
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Armor-Animations">GeckoLib Wiki - Armor Animations</a>
 */
public interface GeoItem extends SingletonGeoAnimatable {
	String ID_NBT_KEY = "GeckoLibID";

	/**
	 * Safety wrapper to distance the client-side code from common code
	 * <p>
	 * This should be cached in your {@link net.minecraft.world.item.Item Item} class
	 */
	static Supplier<Object> makeRenderer(GeoItem item) {
		if (!GeckoLibServices.PLATFORM.isPhysicalClient())
			return () -> null;

		return Suppliers.memoize(() -> {
			AtomicReference<Object> renderProvider = new AtomicReference<>();
			item.createRenderer(renderProvider::set);
			return renderProvider.get();
		});
	}

	/**
	 * Getter for the cached RenderProvider in your class
	 */
	@Override
	default Supplier<Object> getRenderProvider() {
		return GeoItem.makeRenderer(this);
	}

	/**
	 * Gets the unique identifying number from this ItemStack's {@link Tag NBT},
	 * or {@link Long#MAX_VALUE} if one hasn't been assigned
	 */
	static long getId(ItemStack stack) {
		CompoundTag tag = stack.getTag();

		if (tag == null)
			return Long.MAX_VALUE;

		return tag.getLong(ID_NBT_KEY);
	}

	/**
	 * Gets the unique identifying number from this ItemStack's {@link Tag NBT}
	 * <p>
	 * If no ID has been reserved for this stack yet, it will reserve a new id and assign it
	 */
	static long getOrAssignId(ItemStack stack, ServerLevel level) {
		CompoundTag tag = stack.getOrCreateTag();
		long id = tag.getLong(ID_NBT_KEY);

		if (tag.contains(ID_NBT_KEY, Tag.TAG_ANY_NUMERIC))
			return id;

		id = AnimatableIdCache.getFreeId(level);

		tag.putLong(ID_NBT_KEY, id);

		return id;
	}
	
	/**
	 * Returns the current age/tick of the animatable instance
	 * <p>
	 * By default this is just the animatable's age in ticks, but this method allows for non-ticking custom animatables to provide their own values
	 *
	 * @param itemStack The ItemStack representing this animatable
	 * @return The current tick/age of the animatable, for animation purposes
	 */
	@Override
	default double getTick(Object itemStack) {
		return RenderUtil.getCurrentTick();
	}

	/**
	 * Whether this item animatable is perspective aware, handling animations differently depending on the {@link ItemDisplayContext render perspective}
	 */
	default boolean isPerspectiveAware() {
		return false;
	}

	/**
	 * Replaces the default AnimatableInstanceCache for GeoItems if {@link GeoItem#isPerspectiveAware()} is true, for perspective-dependent handling
	 */
	@Nullable
	@Override
	default AnimatableInstanceCache animatableCacheOverride() {
		if (isPerspectiveAware())
			return new ContextBasedAnimatableInstanceCache(this);

		return SingletonGeoAnimatable.super.animatableCacheOverride();
	}

	/**
	 * AnimatableInstanceCache specific to GeoItems, for doing render perspective based animations
	 */
	class ContextBasedAnimatableInstanceCache extends SingletonAnimatableInstanceCache {
		public ContextBasedAnimatableInstanceCache(GeoAnimatable animatable) {
			super(animatable);
		}

		/**
		 * Gets an {@link AnimatableManager} instance from this cache, cached under the id provided, or a new one if one doesn't already exist
		 * <p>
		 * This subclass assumes that all animatable instances will be sharing this cache instance, and so differentiates data by ids
		 */
		@Override
		public AnimatableManager<?> getManagerForId(long uniqueId) {
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
						ItemDisplayContext context = getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

						return context == null ? ItemDisplayContext.NONE : context;
					}
				});

			return this.managers.get(uniqueId);
		}
	}
}
