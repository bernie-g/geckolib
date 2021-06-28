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
		super(NAME);
		this.usedIds.defaultReturnValue(-1);
	}

	public static GeckoLibIdTracker from(ServerWorld world) {
		return world.getServer().getOverworld().getPersistentStateManager().getOrCreate(GeckoLibIdTracker::new, NAME);
	}

	@Override
	public void fromTag(NbtCompound tag) {
		this.usedIds.clear();
		for (String key : tag.getKeys()) {
			if (tag.contains(key, 99)) {
				this.usedIds.put(key, tag.getInt(key));
			}
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		for (Object2IntMap.Entry<String> id : this.usedIds.object2IntEntrySet()) {
			tag.putInt(id.getKey(), id.getIntValue());
		}
		return tag;
	}

	public int getNextId(Type type) {
		final int id = this.usedIds.getInt(type.key) + 1;
		this.usedIds.put(type.key, id);
		this.isDirty();
		return id;
	}

	public enum Type {
		ITEM("Item");

		private final String key;

		Type(String key) {
			this.key = key;
		}
	}
}
