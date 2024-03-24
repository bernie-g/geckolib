package software.bernie.geckolib;

import software.bernie.geckolib.cache.GeckoLibCache;

/**
 * Main GeckoLib client entrypoint
 */
public final class GeckoLibClient {
    public static void init() {
        GeckoLibCache.registerReloadListener();
    }
}
