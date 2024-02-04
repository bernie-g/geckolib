package software.bernie.geckolib;

import software.bernie.geckolib.services.GeckoLibEvents;
import software.bernie.geckolib.services.GeckoLibNetworking;
import software.bernie.geckolib.services.GeckoLibPlatform;
import software.bernie.geckolib.services.GeckoLibRenderProviderHelper;

import java.util.ServiceLoader;

public class GeckoLibServices {

    public static final GeckoLibPlatform PLATFORM = load(GeckoLibPlatform.class);

    public static final GeckoLibNetworking NETWORK = load(GeckoLibNetworking.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        GeckoLibConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

    public static class Client {
        public static final GeckoLibEvents EVENTS = load(GeckoLibEvents.class);
        public static final GeckoLibRenderProviderHelper ITEM_RENDERING = load(GeckoLibRenderProviderHelper.class);
    }
}
