package software.bernie.geckolib3.network.messages;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;

public class S2CSyncAnimationMsg implements ClientPlayNetworking.PlayChannelHandler {


    public S2CSyncAnimationMsg() {

    }

    public static void encode(PacketByteBuf buf, String key, int id, int state) {
        buf.writeString(key);
        buf.writeVarInt(id);
        buf.writeVarInt(state);
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        final String key = buf.readString(32767); // The max length here can be removed in 1.17+
        final int id = buf.readVarInt();
        final int state = buf.readVarInt();

        final ISyncable syncable = GeckoLibNetwork.getSyncable(key);
        if (syncable != null) {
            syncable.onAnimationSync(id, state);
        } else {
            GeckoLib.LOGGER.warn("Syncable on the server is missing on the client for " + key);
        }
    }
}
