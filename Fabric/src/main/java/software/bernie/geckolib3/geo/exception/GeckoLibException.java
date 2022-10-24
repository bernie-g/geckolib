package software.bernie.geckolib3.geo.exception;

import net.minecraft.util.Identifier;

public class GeckoLibException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GeckoLibException(Identifier fileLocation, String message) {
		super(fileLocation + ": " + message);
	}

	public GeckoLibException(Identifier fileLocation, String message, Throwable cause) {
		super(fileLocation + ": " + message, cause);
	}
}