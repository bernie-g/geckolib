package software.bernie.geckolib3.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.network.messages.S2CSyncAnimationMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GeckoLibNetwork {
    private static final Map<String, Supplier<ISyncable>> SYNCABLES = new HashMap<>();
    private static final Identifier SYNCABLE = new Identifier(GeckoLib.ModID,"syncable");

    public static void registerClientPackets() {

        // This would get incremented for every new message,
        // but we only have one right now
        int id = -1;

        // Server --> Client
        ClientPlayNetworking.registerGlobalReceiver(SYNCABLE,new S2CSyncAnimationMsg());
    }

    public static void syncAnimation(PlayerEntity target, ISyncable syncable, int id, int state) {
        if (!target.world.isClient) {
            throw new IllegalArgumentException("Only the server can request animation syncs!");
        }
        final String key = syncable.getSyncKey();
        if (!SYNCABLES.containsKey(key)) {
            throw new IllegalArgumentException("Syncable not registered for " + key);
        }

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        S2CSyncAnimationMsg.encode(buf,key,id,state);

        ServerPlayNetworking.send((ServerPlayerEntity)target,SYNCABLE, buf);
    }

    public static ISyncable getSyncable(String key) {
        final Supplier<ISyncable> delegate = SYNCABLES.get(key);
        return delegate == null ? null : delegate.get();
    }

    //there is no forge registry entries
    public static void registerSyncable(ISyncable entry) {
        final String key = entry.getSyncKey();
        if (SYNCABLES.putIfAbsent(key, () -> entry) != null) {
            throw new IllegalArgumentException("Syncable already registered for " + key);
        }
        GeckoLib.LOGGER.debug("Registered syncable for " + key);
    }
}
