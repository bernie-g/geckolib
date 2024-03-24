package software.bernie.geckolib.platform;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import software.bernie.geckolib.service.GeckoLibPlatform;

import java.nio.file.Path;

/**
 * NeoForge service for general loader-specific functions
 */
public final class GeckoLibNeoForge implements GeckoLibPlatform {
    /**
     * @return Whether the current runtime is an in-dev (non-production) environment, for running debug-only tasks
     */
    @Override
    public boolean isDevelopmentEnvironment(){
        return !FMLEnvironment.production;
    }

    /**
     * @return Whether the current runtime is on the client side regardless of logical context
     */
    @Override
    public Path getGameDir(){
        return FMLPaths.GAMEDIR.get();
    }

    /**
     * @return The root game directory (./run)
     */
    @Override
    public boolean isPhysicalClient(){
        return FMLEnvironment.dist.isClient();
    }
}
