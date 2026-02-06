package software.bernie.geckolib;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.service.*;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/// Service holder class for access to the various SPI platforms that GeckoLib uses
public final class GeckoLibServices {
    public static final GeckoLibPlatform PLATFORM = load(GeckoLibPlatform.class);
    public static final GeckoLibNetworking NETWORK = load(GeckoLibNetworking.class);
    //private static final GeckoLibLoader LOADER = load(GeckoLibLoader.class, null);

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
