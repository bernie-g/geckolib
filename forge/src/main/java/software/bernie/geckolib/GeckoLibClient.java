package software.bernie.geckolib;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.cache.GeckoLibCache;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = GeckoLibConstants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeckoLibClient {
    @SubscribeEvent
    public static void registerReloadListeners(final RegisterClientReloadListenersEvent ev) {
        ev.registerReloadListener(GeckoLibCache::reload);
    }
}
