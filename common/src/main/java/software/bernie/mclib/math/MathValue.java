package software.bernie.mclib.math;

import java.util.function.DoubleSupplier;

/**
 * Base interface for all computational values in the math system
 * <p>
 * All mathematical objects are an extension of this interface, allowing for an indefinitely-nestable
 * mathematical system that can be accessed via this one access point
 */
@FunctionalInterface
public interface MathValue extends DoubleSupplier {
    /**
     * Get computed or stored value
     */
    double get();

    @Override
    default double getAsDouble() {
        return get();
    }
}
