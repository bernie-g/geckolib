package software.bernie.geckolib;

import software.bernie.geckolib.services.GeckoLibItemHelper;
import software.bernie.geckolib.services.GeckoLibPlatform;

import java.util.ServiceLoader;

public class GeckoLibServices {

    public static GeckoLibPlatform PLATFORM = load(GeckoLibPlatform.class);

    public static final GeckoLibItemHelper ITEM_HELPER = load(GeckoLibItemHelper.class);

    private static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        GeckoLibConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
