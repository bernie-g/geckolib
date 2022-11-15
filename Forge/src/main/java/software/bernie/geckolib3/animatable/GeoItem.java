package software.bernie.geckolib3.animatable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.cache.AnimatableIdCache;

/**
 * The {@link software.bernie.geckolib3.core.animatable.GeoAnimatable GeoAnimatable} interface specific to {@link net.minecraft.world.item.Item Items}.
 * This also applies to armor, as they are just items too.
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Item-Animations">GeckoLib Wiki - Item Animations</a>
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Armor-Animations">GeckoLib Wiki - Armor Animations</a>
 */
public interface GeoItem extends SingletonGeoAnimatable {
	static final String ID_NBT_KEY = "GeckoLibID";

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
}
