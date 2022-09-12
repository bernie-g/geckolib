package software.bernie.geckolib3q.geo.exception;

import net.minecraft.util.Identifier;

@SuppressWarnings("serial")
public class GeckoLibException extends RuntimeException {
	public GeckoLibException(Identifier fileLocation, String message) {
		super(fileLocation + ": " + message);
	}

	public GeckoLibException(Identifier fileLocation, String message, Throwable cause) {
		super(fileLocation + ": " + message, cause);
	}
}