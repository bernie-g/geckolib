package software.bernie.geckolib3.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.network.messages.SyncAnimationMsg;

public class GeckoLibNetwork {
    private static final Map<String, Supplier<ISyncable>> SYNCABLES = new HashMap<>();

    private static final String PROTOCOL_VERSION = "0"; // This should be updated whenever packets change
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GeckoLib.ModID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void initialize() {
        // This would get incremented for every new message,
        // but we only have one right now
        int id = -1;

        // Server --> Client
        SyncAnimationMsg.register(CHANNEL, ++id);
    }

    public static void syncAnimation(PacketDistributor.PacketTarget target, ISyncable syncable, int id, int state) {
        if (!target.getDirection().getOriginationSide().isServer()) {
            throw new IllegalArgumentException("Only the server can request animation syncs!");
        }
        final String key = syncable.getSyncKey();
        if (!SYNCABLES.containsKey(key)) {
            throw new IllegalArgumentException("Syncable not registered for " + key);
        }
        CHANNEL.send(target, new SyncAnimationMsg(key, id, state));
    }

    public static ISyncable getSyncable(String key) {
        final Supplier<ISyncable> delegate = SYNCABLES.get(key);
        return delegate == null ? null : delegate.get();
    }

    public static <E extends ForgeRegistryEntry<E>, T extends ForgeRegistryEntry<E> & ISyncable> void registerSyncable(T entry) {
        final IRegistryDelegate<?> delegate = entry.delegate;
        final String key = entry.getSyncKey();
        if (SYNCABLES.putIfAbsent(key, () -> (ISyncable) delegate.get()) != null) {
            throw new IllegalArgumentException("Syncable already registered for " + key);
        }
        GeckoLib.LOGGER.debug("Registered syncable for " + key);
    }
}
