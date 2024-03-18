package software.bernie.mclib.math.value;

import software.bernie.mclib.math.MathValue;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns <b>1</b> if the contained value is equal to <b>0</b>, otherwise returns <b>0</b>
 */
public record BooleanNegate(MathValue value) implements MathValue {
    @Override
    public double get() {
        return this.value.get() == 0 ? 1 : 0;
    }

    @Override
    public boolean isMutable() {
        return this.value.isMutable();
    }

    @Override
    public String toString() {
        return "!" + this.value.toString();
    }
}
