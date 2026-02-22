package com.geckolib.service;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.Entity;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/// Loader-agnostic service interface for general loader-specific functions
public interface GeckoLibPlatform {
    /// @return Whether the current runtime is an in-dev (non-production) environment, for running debug-only tasks
    boolean isDevelopmentEnvironment();

    /// @return Whether the current runtime is on the client side regardless of logical context
    boolean isPhysicalClient();

    /// @return The root game directory (./run)
    Path getGameDir();

    /// Helper method to account for Forge/NeoForge's custom fluid implementation in relation to swimming in fluids
    ///
    /// @return Whether the entity is in a swimmable fluid or not
    default boolean isInSwimmableFluid(Entity entity) {
        return entity.isInWater();
    }

    /// Register a [DataComponentType]
    ///
    /// This is mostly just used for storing the animatable ID on ItemStacks
    <T> Supplier<DataComponentType<T>> registerDataComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builder);
}
