package software.bernie.geckolib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import software.bernie.geckolib.cache.GeckoLibCache;

/**
 * Main GeckoLib client entrypoint
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = GeckoLibConstants.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class GeckoLibClient {
    @SubscribeEvent
    public static void registerReloadListeners(final RegisterClientReloadListenersEvent ev) {
        ev.registerReloadListener(GeckoLibCache::reload);
    }
}
