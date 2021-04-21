package software.bernie.example.network.messages;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

import software.bernie.example.registry.ItemRegistry;

public class TriggerJackInTheBoxMsg {
    private final int id;

    public TriggerJackInTheBoxMsg(int id) {
        this.id = id;
    }

    public static void register(SimpleChannel channel, int id) {
        channel.registerMessage(
                id,
                TriggerJackInTheBoxMsg.class,
                TriggerJackInTheBoxMsg::encode,
                TriggerJackInTheBoxMsg::decode,
                TriggerJackInTheBoxMsg::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private static TriggerJackInTheBoxMsg decode(PacketBuffer buf) {
        final int id = buf.readVarInt();
        return new TriggerJackInTheBoxMsg(id);
    }

    private void encode(PacketBuffer buf) {
        buf.writeVarInt(id);
    }

    private void handle(Supplier<NetworkEvent.Context> sup) {
        final NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> ItemRegistry.JACK_IN_THE_BOX.get().doClientAnimation(this.id));
        ctx.setPacketHandled(true);
    }
}
