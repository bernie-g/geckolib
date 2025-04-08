package software.bernie.geckolib.loading.math.value;

import org.apache.commons.lang3.mutable.MutableDouble;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.Operator;

import java.util.Set;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * A computed value of argA and argB defined by the contract of the {@link Operator}
 */
public record Calculation(Operator operator, MathValue argA, MathValue argB, boolean isMutable, Set<Variable> usedVariables, MutableDouble cachedValue) implements MathValue {
    public Calculation(Operator operator, MathValue argA, MathValue argB) {
        this(operator, argA, argB, argA.isMutable() || argB.isMutable(), MathValue.collectUsedVariables(argA, argB), new MutableDouble(Double.MIN_VALUE));
    }

    @Override
    public double get(AnimationState<?> animationState) {
        if (this.isMutable)
            return this.operator.compute(this.argA.get(animationState), this.argB.get(animationState));

        if (this.cachedValue.getValue() == Double.MIN_VALUE)
            this.cachedValue.setValue(this.operator.compute(this.argA.get(animationState), this.argB.get(animationState)));

        return this.cachedValue.getValue();
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return this.usedVariables;
    }

    @Override
    public String toString() {
        return this.argA.toString() + " " + this.operator.symbol() + " " + this.argB.toString();
    }
}
