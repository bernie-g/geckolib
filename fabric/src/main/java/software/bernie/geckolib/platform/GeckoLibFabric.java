package software.bernie.geckolib.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import software.bernie.geckolib.service.GeckoLibPlatform;

import java.nio.file.Path;

/**
 * Fabric service for general loader-specific functions
 */
public final class GeckoLibFabric implements GeckoLibPlatform {
    /**
     * @return Whether the current runtime is an in-dev (non-production) environment, for running debug-only tasks
     */
    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    /**
     * @return The root game directory (./run)
     */
    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    /**
     * @return Whether the current runtime is on the client side regardless of logical context
     */
    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
