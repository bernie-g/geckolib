package com.geckolib.platform;

import com.geckolib.GeckoLib;
import com.geckolib.service.GeckoLibPlatform;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.loading.FMLEnvironment;

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
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }

    /**
     * @return Whether the current runtime is on the client side regardless of logical context
     */
    @Override
    public boolean isPhysicalClient() {
        return FMLEnvironment.getDist().isClient();
    }

    /**
     * Helper method to account for Forge/NeoForge's custom fluid implementation in relation to swimming in fluids
     *
     * @return Whether the entity is in a swimmable fluid or not
     */
    @Override
    public boolean isInSwimmableFluid(Entity entity) {
        return GeckoLibPlatform.super.isInSwimmableFluid(entity) || entity.isInFluidType((fluidType, height) -> entity.canSwimInFluidType(fluidType));
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
