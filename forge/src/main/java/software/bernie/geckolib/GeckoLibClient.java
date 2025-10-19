package software.bernie.geckolib;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.cache.GeckoLibResources;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = GeckoLibConstants.MODID)
public class GeckoLibClient {
    @SubscribeEvent
    public static void registerReloadListeners(final RegisterClientReloadListenersEvent ev) {
        ev.registerReloadListener(GeckoLibResources::reload);
    }
}
