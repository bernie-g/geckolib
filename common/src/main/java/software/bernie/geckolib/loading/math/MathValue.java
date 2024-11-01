package software.bernie.geckolib.loading.math;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.DoubleSupplier;

/**
 * Base interface for all computational values in the math system
 * <p>
 * All mathematical objects are an extension of this interface, allowing for an indefinitely-nestable
 * mathematical system that can be accessed via this one access point
 */
public interface MathValue extends DoubleSupplier {
    /**
     * Get computed or stored value
     */
    double get();

    /**
     * Return whether this type of MathValue should be considered mutable; its value could change.
     * <br>
     * This is used to cache calculated values, optimising computational work
     */
    default boolean isMutable() {
        return true;
    }

    /**
     * Overloaded, use {@link #get} instead
     */
    @ApiStatus.Internal
    @Override
    default double getAsDouble() {
        return get();
    }
}
