package software.bernie.geckolib.loading.math.value;

import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleSupplier;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the currently stored value, which may be modified at any given time via {@link #set}. Values may be lazily evaluated to eliminate wasteful usage
 */
public record Variable(String name, AtomicReference<DoubleSupplier> value) implements MathValue {
    public Variable(String name, double value) {
        this(name, () -> value);
    }

    public Variable(String name, DoubleSupplier value) {
        this(name, new AtomicReference<>(value));
    }

    @Override
    public double get() {
        try {
            return this.value.get().getAsDouble();
        }
        catch (Exception ex) {
            GeckoLibConstants.LOGGER.error("Attempted to use Molang variable for incompatible animatable type (" + this.name + "). An animation json needs to be fixed", ex.getMessage());

            return 0;
        }
    }

    public void set(final double value) {
        this.value.set(() -> value);
    }

    public void set(final DoubleSupplier value) {
        this.value.set(value);
    }

    @Override
    public String toString() {
        return this.name + "(" + this.value.get().getAsDouble() + ")";
    }
}
