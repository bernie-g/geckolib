package software.bernie.geckolib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import software.bernie.geckolib.cache.GeckoLibResources;

/**
 * Main GeckoLib client entrypoint
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = GeckoLibConstants.MODID)
public final class GeckoLibClient {
    @SubscribeEvent
    public static void registerReloadListeners(final AddClientReloadListenersEvent ev) {
        ev.addListener(GeckoLibResources.RELOAD_LISTENER_ID, new GeckoLibResources());
    }
}
