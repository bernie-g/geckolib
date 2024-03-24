package software.bernie.geckolib.service;

import java.nio.file.Path;

/**
 * Loader-agnostic service interface for general loader-specific functions
 */
public interface GeckoLibPlatform {
    /**
     * @return Whether the current runtime is an in-dev (non-production) environment, for running debug-only tasks
     */
    boolean isDevelopmentEnvironment();

    /**
     * @return Whether the current runtime is on the client side regardless of logical context
     */
    boolean isPhysicalClient();

    /**
     * @return The root game directory (./run)
     */
    Path getGameDir();
}
