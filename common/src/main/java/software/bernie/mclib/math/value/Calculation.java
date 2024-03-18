package software.bernie.mclib.math.value;

import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.operator.Operator;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * A computed value of argA and argB defined by the contract of the {@link Operator}
 */
public record Calculation(Operator operator, MathValue argA, MathValue argB) implements MathValue {
    @Override
    public double get() {
        return this.operator.compute(this.argA.get(), this.argB.get());
    }

    @Override
    public boolean isMutable() {
        return this.argA.isMutable() || this.argB.isMutable();
    }

    @Override
    public String toString() {
        return this.argA.toString() + " " + this.operator.symbol() + " " + this.argB.toString();
    }
}
