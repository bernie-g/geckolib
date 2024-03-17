package software.bernie.mclib.math.value;

import org.apache.commons.lang3.mutable.MutableDouble;
import software.bernie.mclib.math.MathValue;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the currently stored value, which may be modified at any given time via {@link #set}
 */
public record Variable(String name, MutableDouble value) implements MathValue {
    public Variable(String name, double value) {
        this(name, new MutableDouble(value));
    }

    @Override
    public double get() {
        return this.value.getValue();
    }

    public void set(final double value) {
        this.value.setValue(value);
    }

    @Override
    public String toString() {
        return this.name + "(" + this.value.getValue() + ")";
    }
}
