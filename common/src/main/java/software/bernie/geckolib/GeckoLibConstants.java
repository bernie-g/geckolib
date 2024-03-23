package software.bernie.geckolib;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Holder class for several properties and/or handlers inherent to GeckoLib
 */
public final class GeckoLibConstants {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "geckolib";

    /**
     * Helper method to create a ResourceLocation predefined with GeckoLib's {@link #MODID}
     */
    public static ResourceLocation resourceLocation(String path) {
        return new ResourceLocation(GeckoLibConstants.MODID, path);
    }

    /**
     * Throw an exception pertaining to a specific resource
     * <p>
     * This mostly serves as a helper for consistent formatting of exceptions
     *
     * @param resource The location or id of the resource the error pertains to
     * @param message The error message to display
     */
    public static RuntimeException exception(ResourceLocation resource, String message) {
        return new RuntimeException(resource + ": " + message);
    }

    /**
     * Throw an exception pertaining to a specific resource
     * <p>
     * This mostly serves as a helper for consistent formatting of exceptions
     *
     * @param resource The location or id of the resource the error pertains to
     * @param message The error message to display
     * @param exception The exception to throw
     */
    public static RuntimeException exception(ResourceLocation resource, String message, Throwable exception) {
        return new RuntimeException(resource + ": " + message, exception);
    }
}
