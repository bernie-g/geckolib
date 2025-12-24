package software.bernie.geckolib;

import software.bernie.geckolib.service.GeckoLibEvents;
import software.bernie.geckolib.service.GeckoLibNetworking;
import software.bernie.geckolib.service.GeckoLibPlatform;
import software.bernie.geckolib.service.GeckoLibClient;

import java.util.ServiceLoader;

/**
 * Service holder class for access to the various SPI platforms that GeckoLib uses
 */
public final class GeckoLibServices {
    public static final GeckoLibPlatform PLATFORM = load(GeckoLibPlatform.class);
    public static final GeckoLibNetworking NETWORK = load(GeckoLibNetworking.class);

    public static class Client {
        public static final GeckoLibEvents EVENTS = load(GeckoLibEvents.class);
        public static final GeckoLibClient ITEM_RENDERING = load(GeckoLibClient.class);
    }

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        GeckoLibConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
