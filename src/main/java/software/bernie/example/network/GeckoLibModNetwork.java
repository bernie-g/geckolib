package software.bernie.example.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import software.bernie.example.network.messages.TriggerJackInTheBoxMsg;
import software.bernie.geckolib3.GeckoLib;

public class GeckoLibModNetwork {
    private static final String PROTOCOL_VERSION = "0";
    private static SimpleChannel channel;

    public static void register() {
        // We do this here instead of in the field in order to prevent it from
        // being registered out of dev
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(GeckoLib.ModID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals);

        // This would get incremented for every new message,
        // but we only have one right now
        int id = -1;

        // Server --> Client
        TriggerJackInTheBoxMsg.register(channel, ++id);
    }

    public static SimpleChannel getChannel() {
        return channel;
    }
}
