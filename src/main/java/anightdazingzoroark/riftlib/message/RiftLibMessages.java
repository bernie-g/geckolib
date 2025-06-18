package anightdazingzoroark.riftlib.message;

import anightdazingzoroark.riftlib.RiftLib;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class RiftLibMessages {
    public static SimpleNetworkWrapper WRAPPER;

    public static void registerMessages() {
        WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(RiftLib.ModID);

        int id = 0;
        WRAPPER.registerMessage(RiftLibCreateHitboxes.Handler.class, RiftLibCreateHitboxes.class, id++, Side.SERVER);
        WRAPPER.registerMessage(RiftLibCreateHitboxes.Handler.class, RiftLibCreateHitboxes.class, id++, Side.CLIENT);
    }
}
