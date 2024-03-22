package software.bernie.geckolib.loading.math.value;

import software.bernie.geckolib.loading.math.MathValue;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Negated equivalent of the stored value; returning a positive number if the stored value is negative, or a negative value if the stored value is positive
 */
public record Negative(MathValue value) implements MathValue {
    @Override
    public double get() {
        return -this.value.get();
    }

    @Override
    public boolean isMutable() {
        return this.value.isMutable();
    }

    @Override
    public String toString() {
        return "-" + this.value.toString();
    }
}
