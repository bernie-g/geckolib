package com.geckolib;

import net.fabricmc.api.ModInitializer;
import com.geckolib.service.GeckoLibNetworking;

/**
 * Main GeckoLib entrypoint
 */
public final class GeckoLib implements ModInitializer {
    @Override
    public void onInitialize() {
        GeckoLibConstants.init();
        GeckoLibNetworking.init();
    }
}
