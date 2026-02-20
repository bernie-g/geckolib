package software.bernie.geckolib.cache;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;

/// Storage class that keeps track of the last animatable id used, and provides new ones on request
///
/// Generally only used for [Items][net.minecraft.world.item.Item], but any [singleton][SingletonAnimatableInstanceCache] will likely use this.
public final class AnimatableIdCache extends SavedData {
	private static final Codec<AnimatableIdCache> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.LONG.fieldOf("last_id").forGetter(cache -> cache.lastId)
	).apply(builder, AnimatableIdCache::new));
	@SuppressWarnings("DataFlowIssue")
    public static final SavedDataType<AnimatableIdCache> TYPE = new SavedDataType<>(GeckoLibConstants.id("animatable_id_ticker"), AnimatableIdCache::new, CODEC, null);

	private long lastId;

	private AnimatableIdCache() {
		this(0);
	}

	private AnimatableIdCache(long lastId) {
		this.lastId = lastId;
	}

	/// Get the next free id from the id cache
	///
	/// @param level An arbitrary ServerLevel. It doesn't matter which one
	/// @return The next free ID, which is immediately reserved for use after calling this method
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
