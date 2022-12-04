package software.bernie.geckolib.cache;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;

/**
 * Storage class that keeps track of the last animatable id used, and provides new ones on request.<br>
 * Generally only used for {@link net.minecraft.world.item.Item Items}, but any
 * {@link SingletonAnimatableInstanceCache singleton} will likely use this.
 */
public final class AnimatableIdCache extends SavedData {
	private static final String DATA_KEY = "geckolib_id_cache";
	private long lastId;

	private AnimatableIdCache() {
		this(new CompoundTag());
	}

	private AnimatableIdCache(CompoundTag tag) {
		this.lastId = tag.getLong("last_id");
	}

	/**
	 * Get the next free id from the id cache
	 * @param level An arbitrary ServerLevel. It doesn't matter which one
	 * @return The next free ID, which is immediately reserved for use after calling this method
	 */
	public static long getFreeId(ServerLevel level) {
		return getCache(level).getNextId();
	}

	private long getNextId() {
		setDirty();

		return ++this.lastId;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		tag.putLong("last_id", this.lastId);

		return tag;
	}

	private static AnimatableIdCache getCache(ServerLevel level) {
		DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
		AnimatableIdCache cache = storage.computeIfAbsent(AnimatableIdCache::new, AnimatableIdCache::new, DATA_KEY);

		if (cache.lastId == 0) {
			AnimatableIdCache legacyCache = storage.get(AnimatableIdCache::fromLegacy, "geckolib_ids");

			if (legacyCache != null)
				cache.lastId = legacyCache.lastId;
		}

		return cache;
	}

	/**
	 * Legacy wrapper for existing worlds pre-4.0.<br>
	 * Remove this at some point in the future
	 */
	private static AnimatableIdCache fromLegacy(CompoundTag tag) {
		AnimatableIdCache legacyCache = new AnimatableIdCache();

		for (String key : tag.getAllKeys()) {
			if (tag.contains(key, Tag.TAG_ANY_NUMERIC))
				legacyCache.lastId = Math.max(legacyCache.lastId, tag.getInt(key));
		}

		return legacyCache;
	}
}
