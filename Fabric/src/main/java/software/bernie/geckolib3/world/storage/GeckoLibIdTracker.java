package software.bernie.geckolib3.world.storage;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

//This stores a small amount of NBT data with each world in order to track the last
//free IDs for various tasks (such as animation data ids for items)
public class GeckoLibIdTracker extends PersistentState {
	private static final String NAME = "geckolib_ids";
	private final Object2IntMap<String> usedIds = new Object2IntOpenHashMap<>();

	public GeckoLibIdTracker() {
		super();
		this.usedIds.defaultReturnValue(-1);
	}

	public static GeckoLibIdTracker from(ServerWorld world) {
		return world.getServer().getOverworld().getPersistentStateManager().getOrCreate(GeckoLibIdTracker::fromNbt,
				GeckoLibIdTracker::new, NAME);
	}

	public static GeckoLibIdTracker fromNbt(NbtCompound nbt) {
		GeckoLibIdTracker idCountsState = new GeckoLibIdTracker();
		idCountsState.usedIds.clear();
		for (String key : nbt.getKeys()) {
			if (nbt.contains(key, NbtCompound.NUMBER_TYPE)) {
				idCountsState.usedIds.put(key, nbt.getInt(key));
			}
		}
		return idCountsState;
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		for (Object2IntMap.Entry<String> id : this.usedIds.object2IntEntrySet()) {
			nbt.putInt(id.getKey(), id.getIntValue());
		}
		return nbt;
	}

	public int getNextId(Type type) {
		int i = this.usedIds.getInt(type.key) + 1;
		this.usedIds.put(type.key, i);
		this.markDirty();
		return i;
	}

	public enum Type {
		ITEM("Item");

		private final String key;

		Type(String key) {
			this.key = key;
		}
	}

}