package software.bernie.geckolib3.geo.exception;

import net.minecraft.util.ResourceLocation;

@SuppressWarnings("serial")
public class GeoModelException extends RuntimeException {
	public GeoModelException(ResourceLocation fileLocation, String message) {
		super(fileLocation + ": " + message);
	}

	public GeoModelException(ResourceLocation fileLocation, String message, Throwable cause) {
		super(fileLocation + ": " + message, cause);
	}
}
