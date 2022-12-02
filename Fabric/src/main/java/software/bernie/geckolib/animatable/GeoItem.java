package software.bernie.geckolib.animatable;

import com.google.common.base.Suppliers;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.AnimatableIdCache;
import software.bernie.geckolib.util.RenderUtils;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * The {@link software.bernie.geckolib.core.animatable.GeoAnimatable GeoAnimatable} interface specific to {@link net.minecraft.world.item.Item Items}.
 * This also applies to armor, as they are just items too.
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Item-Animations">GeckoLib Wiki - Item Animations</a>
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Armor-Animations">GeckoLib Wiki - Armor Animations</a>
 */
public interface GeoItem extends SingletonGeoAnimatable {
	String ID_NBT_KEY = "GeckoLibID";

	/**
	 * Safety wrapper to distance the client-side code from common code.<br>
	 * This should be cached in your {@link net.minecraft.world.item.Item Item} class
	 */
	static Supplier<Object> makeRenderer(GeoItem item) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
			return () -> null;

		return Suppliers.memoize(() -> {
			AtomicReference<Object> renderProvider = new AtomicReference<>();
			item.createRenderer(renderProvider::set);
			return renderProvider.get();
		});
	}

	/**
	 * Gets the unique identifying number from this ItemStack's {@link net.minecraft.nbt.Tag NBT},
	 * or {@link Long#MAX_VALUE} if one hasn't been assigned
	 */
	static long getId(ItemStack stack) {
		CompoundTag tag = stack.getTag();

		if (tag == null)
			return Long.MAX_VALUE;

		return tag.getLong(ID_NBT_KEY);
	}

	/**
	 * Gets the unique identifying number from this ItemStack's {@link net.minecraft.nbt.Tag NBT}.<br>
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
	 * Returns the current age/tick of the animatable instance.<br>
	 * By default this is just the animatable's age in ticks, but this method allows for non-ticking custom animatables to provide their own values
	 * @param itemStack The ItemStack representing this animatable
	 * @return The current tick/age of the animatable, for animation purposes
	 */
	@Override
	default double getTick(Object itemStack) {
		return RenderUtils.getCurrentTick();
	}
}
