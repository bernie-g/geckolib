package software.bernie.geckolib.cache;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;

/**
 * Storage class that keeps track of the last animatable id used, and provides new ones on request
 * <p>
 * Generally only used for {@link net.minecraft.world.item.Item Items}, but any {@link SingletonAnimatableInstanceCache singleton} will likely use this.
 */
public final class AnimatableIdCache extends SavedData {
	private static final Factory<AnimatableIdCache> FACTORY = new Factory<>(AnimatableIdCache::new, AnimatableIdCache::new, null);
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
	 *
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
		return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_KEY);
	}
}
