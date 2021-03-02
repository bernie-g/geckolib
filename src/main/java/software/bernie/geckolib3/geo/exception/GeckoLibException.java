package software.bernie.geckolib3.geo.exception;

import net.minecraft.util.ResourceLocation;

public class GeckoLibException extends RuntimeException {
	public GeckoLibException(ResourceLocation fileLocation, String message) {
		super(fileLocation + ": " + message);
	}

	public GeckoLibException(ResourceLocation fileLocation, String message, Throwable cause) {
		super(fileLocation + ": " + message, cause);
	}
}
