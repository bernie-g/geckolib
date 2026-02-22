package com.geckolib;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/// Holder class for several properties and/or handlers inherent to GeckoLib
public final class GeckoLibConstants {
    public static final Logger LOGGER = LogManager.getLogger("GeckoLib");
    public static final String MODID = "geckolib";

    public static final Supplier<DataComponentType<Long>> STACK_ANIMATABLE_ID_COMPONENT = GeckoLibServices.PLATFORM.registerDataComponent("stack_animatable_id", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

    public static void init() {}

    /// Helper method to create an Identifier predefined with GeckoLib's [#MODID]
    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(GeckoLibConstants.MODID, path);
    }

    /// Throw an exception pertaining to a specific resource
    ///
    /// This mostly serves as a helper for consistent formatting of exceptions
    ///
    /// @param resource The location or id of the resource the error pertains to
    /// @param message The error message to display
    public static RuntimeException exception(Identifier resource, String message) {
        return new RuntimeException(resource + ": " + message);
    }

    /// Throw an exception pertaining to a specific resource
    ///
    /// This mostly serves as a helper for consistent formatting of exceptions
    ///
    /// @param resource The location or id of the resource the error pertains to
    /// @param message The error message to display
    /// @param exception The exception to throw
    public static RuntimeException exception(Identifier resource, String message, Throwable exception) {
        return new RuntimeException(resource + ": " + message, exception);
    }
}
