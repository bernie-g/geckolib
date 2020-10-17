package software.bernie.geckolib.geo.exception;

import net.minecraft.util.Identifier;

public class GeoModelException extends RuntimeException {
    public GeoModelException(Identifier fileLocation, String message) {
        super(fileLocation + ": " + message);
    }

    public GeoModelException(Identifier fileLocation, String message, Throwable cause) {
        super(fileLocation + ": " + message, cause);
    }
}