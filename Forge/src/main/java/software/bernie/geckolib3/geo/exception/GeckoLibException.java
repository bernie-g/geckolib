package software.bernie.geckolib3.geo.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("serial")
public class GeckoLibException extends RuntimeException {

	public static final Logger LOGGER = LogManager.getLogger();

	public GeckoLibException(ResourceLocation fileLocation, String message) {
		super(fileLocation + ": " + message);
	}

	public GeckoLibException(ResourceLocation fileLocation, String message, Throwable cause) {
		super(fileLocation + ": " + message, cause);
	}
}
