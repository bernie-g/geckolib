package software.bernie.geckolib.service;

import net.minecraft.core.component.DataComponentType;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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

    /**
     * Register a {@link DataComponentType}
     * <p>
     * This is mostly just used for storing the animatable ID on ItemStacks
     */
    <T> Supplier<DataComponentType<T>> registerDataComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builder);
}
