package anightdazingzoroark.riftlib.message;

import anightdazingzoroark.riftlib.RiftLib;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class RiftLibMessage {
    public static SimpleNetworkWrapper WRAPPER;

    public static void registerMessages() {
        WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(RiftLib.ModID);

        int id = 0;
        WRAPPER.registerMessage(RiftLibUpdateHitboxPos.Handler.class, RiftLibUpdateHitboxPos.class, id++, Side.SERVER);
        WRAPPER.registerMessage(RiftLibUpdateHitboxPos.Handler.class, RiftLibUpdateHitboxPos.class, id++, Side.CLIENT);
        WRAPPER.registerMessage(RiftLibUpdateRiderPos.Handler.class, RiftLibUpdateRiderPos.class, id++, Side.SERVER);
        WRAPPER.registerMessage(RiftLibUpdateRiderPos.Handler.class, RiftLibUpdateRiderPos.class, id++, Side.CLIENT);
        WRAPPER.registerMessage(RiftLibUpdateHitboxSize.Handler.class, RiftLibUpdateHitboxSize.class, id++, Side.SERVER);
        WRAPPER.registerMessage(RiftLibUpdateHitboxSize.Handler.class, RiftLibUpdateHitboxSize.class, id++, Side.CLIENT);
        WRAPPER.registerMessage(RiftLibOpenUI.Handler.class, RiftLibOpenUI.class, id++, Side.CLIENT);
    }
}
