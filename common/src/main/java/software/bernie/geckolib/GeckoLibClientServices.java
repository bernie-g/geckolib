package software.bernie.geckolib;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.service.GeckoLibClient;
import software.bernie.geckolib.service.GeckoLibEvents;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * Service holder class for access to the various SPI platforms that GeckoLib uses for client-specific functionality
 */
public class GeckoLibClientServices {
    public static final GeckoLibEvents EVENTS = load(GeckoLibEvents.class);
    public static final GeckoLibClient ITEM_RENDERING = load(GeckoLibClient.class);

    private static <T> T load(Class<T> clazz) {
        return load(clazz, null);
    }

    private static <T> T load(Class<T> clazz, @Nullable Supplier<T> defaultInstance) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .or(defaultInstance == null ? Optional::empty : () -> Optional.of(defaultInstance.get()))
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        GeckoLibConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
