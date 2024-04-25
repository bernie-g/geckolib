package software.bernie.geckolib.platform;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.service.GeckoLibPlatform;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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
     * @return The root game directory (./run)
     */
    @Override
    public Path getGameDir(){
        return FMLPaths.GAMEDIR.get();
    }

    /**
     * @return Whether the current runtime is on the client side regardless of logical context
     */
    @Override
    public boolean isPhysicalClient(){
        return FMLEnvironment.dist.isClient();
    }

    /**
     * Register a {@link DataComponentType}
     * <p>
     * This is mostly just used for storing the animatable ID on ItemStacks
     */
    @Override
    public <T> Supplier<DataComponentType<T>> registerDataComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return GeckoLib.DATA_COMPONENTS_REGISTER.registerComponentType(id, builder);
    }
}
