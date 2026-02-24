package com.geckolib.platform;

import com.geckolib.GeckoLibConstants;
import com.geckolib.service.GeckoLibPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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
     * @return Whether the current runtime is on the client side regardless of logical context
     */
    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    /**
     * Register a {@link DataComponentType}
     * <p>
     * This is mostly just used for storing the animatable ID on ItemStacks
     */
    @Override
    public <T> Supplier<DataComponentType<T>> registerDataComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
        final DataComponentType<T> componentType = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, GeckoLibConstants.id(id).toString(), builder.apply(DataComponentType.builder()).build());

        return () -> componentType;
    }
}
