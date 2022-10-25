package software.bernie.geckolib3.network.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;

public class SyncAnimationMsg {
    private final String key;
    private final int id;
    private final int state;

    public SyncAnimationMsg(String key, int id, int state) {
        this.key = key;
        this.id = id;
        this.state = state;
    }

    public static void register(SimpleChannel channel, int id) {
        channel.registerMessage(
                id,
                SyncAnimationMsg.class,
                SyncAnimationMsg::encode,
                SyncAnimationMsg::decode,
                SyncAnimationMsg::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private static SyncAnimationMsg decode(FriendlyByteBuf buf) {
        final String key = buf.readUtf();
        final int id = buf.readVarInt();
        final int state = buf.readVarInt();
        return new SyncAnimationMsg(key, id, state);
    }

    private void encode(FriendlyByteBuf buf) {
        buf.writeUtf(key);
        buf.writeVarInt(id);
        buf.writeVarInt(state);
    }

    private void handle(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
            final ISyncable syncable = GeckoLibNetwork.getSyncable(key);
            if (syncable != null) {
                syncable.onAnimationSync(id, state);
            } else {
                GeckoLib.LOGGER.warn("Syncable on the server is missing on the client for " + key);
            }
        });
        ctx.setPacketHandled(true);
    }
}
