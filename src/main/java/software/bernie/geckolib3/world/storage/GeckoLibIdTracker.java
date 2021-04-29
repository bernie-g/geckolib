package software.bernie.geckolib3.world.storage;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

// This stores a small amount of NBT data with each world in order to track the last
// free IDs for various tasks (such as animation data ids for items)
public class GeckoLibIdTracker extends WorldSavedData {
	private static final String NAME = "geckolib_ids";
	private final Object2IntMap<String> usedIds = new Object2IntOpenHashMap<>();

	public GeckoLibIdTracker() {
		super(NAME);
		this.usedIds.defaultReturnValue(-1);
	}

	public static GeckoLibIdTracker from(ServerWorld world) {
		return world.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().getOrCreate(GeckoLibIdTracker::new,
				NAME);
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

	@Override
	public void read(CompoundNBT tag) {
		this.usedIds.clear();
		for (String key : tag.keySet()) {
			if (tag.contains(key, 99)) {
				this.usedIds.put(key, tag.getInt(key));
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		for (Object2IntMap.Entry<String> id : this.usedIds.object2IntEntrySet()) {
			tag.putInt(id.getKey(), id.getIntValue());
		}
		return tag;
	}
}
