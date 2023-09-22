package software.bernie.geckolib;

import net.minecraft.resources.ResourceLocation;

import java.io.Serial;

/**
 * Generic {@link Exception} wrapper for GeckoLib.<br>
 * Mostly just serves as a marker for internal error handling.
 */
public class GeckoLibException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	public GeckoLibException(ResourceLocation fileLocation, String message) {
		super(fileLocation + ": " + message);
	}

	public GeckoLibException(ResourceLocation fileLocation, String message, Throwable cause) {
		super(fileLocation + ": " + message, cause);
	}
}
