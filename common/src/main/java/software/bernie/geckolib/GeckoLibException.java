package software.bernie.geckolib;

import net.minecraft.resources.ResourceLocation;

/**
 * Generic {@link Exception} wrapper for GeckoLib
 * <p>
 * Mostly just serves as a marker for internal error handling.
 */
public class GeckoLibException extends RuntimeException {
	public GeckoLibException(ResourceLocation fileLocation, String message) {
		super(fileLocation + ": " + message);
	}

	public GeckoLibException(ResourceLocation fileLocation, String message, Throwable cause) {
		super(fileLocation + ": " + message, cause);
	}
}
