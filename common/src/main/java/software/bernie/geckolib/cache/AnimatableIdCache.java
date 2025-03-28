package software.bernie.geckolib.cache;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;

/**
 * Storage class that keeps track of the last animatable id used, and provides new ones on request
 * <p>
 * Generally only used for {@link net.minecraft.world.item.Item Items}, but any {@link SingletonAnimatableInstanceCache singleton} will likely use this.
 */
public final class AnimatableIdCache extends SavedData {
	public static final SavedDataType<AnimatableIdCache> TYPE = new SavedDataType<>(GeckoLibConstants.MODID + "_id_cache", AnimatableIdCache::new, AnimatableIdCache::codec, null);

	private static Codec<AnimatableIdCache> codec(SavedData.Context context) {
		return RecordCodecBuilder.create(builder -> builder.group(
				RecordCodecBuilder.point(context.levelOrThrow()),
				Codec.LONG.fieldOf("last_id").forGetter(cache -> cache.lastId)
		).apply(builder, AnimatableIdCache::new));
	}

	private long lastId;

	private AnimatableIdCache(SavedData.Context context) {
		this(context.levelOrThrow(), 0);
	}

	private AnimatableIdCache(ServerLevel level, long lastId) {
		this.lastId = lastId;
	}

	/**
	 * Get the next free id from the id cache
	 *
	 * @param level An arbitrary ServerLevel. It doesn't matter which one
	 * @return The next free ID, which is immediately reserved for use after calling this method
	 */
	public static long getFreeId(ServerLevel level) {
		return getCache(level.getServer().overworld()).getNextId();
	}

	private long getNextId() {
		setDirty();

		return ++this.lastId;
	}

	private static AnimatableIdCache getCache(ServerLevel level) {
		return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
	}
}
