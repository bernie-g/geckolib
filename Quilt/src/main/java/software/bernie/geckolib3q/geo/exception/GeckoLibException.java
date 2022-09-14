package software.bernie.geckolib3q.geo.exception;

import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("serial")
public class GeckoLibException extends RuntimeException {
	public GeckoLibException(ResourceLocation fileLocation, String message) {
		super(fileLocation + ": " + message);
	}

	public GeckoLibException(ResourceLocation fileLocation, String message, Throwable cause) {
		super(fileLocation + ": " + message, cause);
	}
}