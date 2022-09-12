package software.bernie.geckolib3.world.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

// This stores a small amount of NBT data with each world in order to track the last
// free IDs for various tasks (such as animation data ids for items)
public class GeckoLibIdTracker extends SavedData {
    private static final String NAME = "geckolib_ids";
    private final Object2IntMap<String> usedIds = new Object2IntOpenHashMap<>();

    public GeckoLibIdTracker() {
        this.usedIds.defaultReturnValue(-1);
    }

    public static GeckoLibIdTracker from(ServerLevel world) {
        return world.getServer()
                .overworld()
                .getDataStorage()
                .computeIfAbsent(GeckoLibIdTracker::load, GeckoLibIdTracker::new, NAME);
    }

    public static GeckoLibIdTracker load(CompoundTag tag) {
        GeckoLibIdTracker tracker = new GeckoLibIdTracker();
        tracker.usedIds.clear();
        for(String key : tag.getAllKeys()) {
            if (tag.contains(key, 99)) {
                tracker.usedIds.put(key, tag.getInt(key));
            }
        }
        return tracker;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        for(Object2IntMap.Entry<String> id : this.usedIds.object2IntEntrySet()) {
            tag.putInt(id.getKey(), id.getIntValue());
        }
        return tag;
    }

    public int getNextId(Type type) {
        final int id = this.usedIds.getInt(type.key) + 1;
        this.usedIds.put(type.key, id);
        this.setDirty();
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
