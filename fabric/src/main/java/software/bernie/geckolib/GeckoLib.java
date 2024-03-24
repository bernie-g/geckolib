package software.bernie.geckolib;

import net.fabricmc.api.ModInitializer;
import software.bernie.geckolib.service.GeckoLibNetworking;

/**
 * Main GeckoLib entrypoint
 */
public final class GeckoLib implements ModInitializer {
    @Override
    public void onInitialize() {
        GeckoLibNetworking.init();
    }
}
