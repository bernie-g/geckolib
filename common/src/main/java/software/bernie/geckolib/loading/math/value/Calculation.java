package software.bernie.geckolib.loading.math.value;

import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.Operator;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * A computed value of argA and argB defined by the contract of the {@link Operator}
 */
public final class Calculation implements MathValue {
    private final Operator operator;
    private final MathValue argA;
    private final MathValue argB;
    private final boolean isMutable;

    private double cachedValue = Double.MIN_VALUE;

    public Calculation(Operator operator, MathValue argA, MathValue argB) {
        this.operator = operator;
        this.argA = argA;
        this.argB = argB;
        this.isMutable = this.argA.isMutable() || this.argB.isMutable();
    }

    public Operator operator() {
        return this.operator;
    }

    public MathValue argA() {
        return this.argA;
    }

    public MathValue argB() {
        return this.argB;
    }

    @Override
    public double get() {
        if (this.isMutable)
            return compute();

        if (this.cachedValue == Double.MIN_VALUE)
            this.cachedValue = compute();

        return this.cachedValue;
    }

    private double compute() {
        if (this.operator == Operator.OR) {
            return this.operator.compute(this.argA.get(), 0) != 0 ||
                    this.operator.compute(0, this.argB.get()) != 0 ?
                    1 : 0;
        }

        if (this.operator == Operator.AND) {
            return this.operator.compute(this.argA.get(), 1) != 0 &&
                    this.operator.compute(1, this.argB.get()) != 0 ?
                    1 : 0;
        }

        return this.operator.compute(this.argA.get(), this.argB.get());
    }

    @Override
    public boolean isMutable() {
        return this.isMutable;
    }

    @Override
    public String toString() {
        return this.argA.toString() + " " + this.operator.symbol() + " " + this.argB.toString();
    }
}
