package software.bernie.mclib.math.value;

import software.bernie.mclib.math.MathValue;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * A fixed, static double value
 */
public record Constant(double value) implements MathValue {
    @Override
    public double get() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
