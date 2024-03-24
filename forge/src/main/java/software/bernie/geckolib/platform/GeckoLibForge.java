package software.bernie.geckolib.platform;

import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import software.bernie.geckolib.service.GeckoLibPlatform;

import java.nio.file.Path;

/**
 * Forge service for general loader-specific functions
 */
public class GeckoLibForge implements GeckoLibPlatform {
    /**
     * @return Whether the current runtime is an in-dev (non-production) environment, for running debug-only tasks
     */
    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    /**
     * @return Whether the current runtime is on the client side regardless of logical context
     */
    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    /**
     * @return The root game directory (./run)
     */
    @Override
    public boolean isPhysicalClient() {
        return FMLEnvironment.dist.isClient();
    }
}
