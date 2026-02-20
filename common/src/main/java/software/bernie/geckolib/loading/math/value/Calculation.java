package software.bernie.geckolib.loading.math.value;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.Operator;

import java.util.Set;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// A computed value of argA and argB defined by the contract of the [Operator]
public record Calculation(Operator operator, MathValue argA, MathValue argB, boolean isMutable, Set<Variable> usedVariables, MutableDouble cachedValue) implements MathValue {
    public Calculation(Operator operator, MathValue argA, MathValue argB) {
        this(operator, argA, argB, argA.isMutable() || argB.isMutable(), MathValue.collectUsedVariables(argA, argB), new MutableDouble(Double.MIN_VALUE));
    }

    @Override
    public double get(@Nullable ControllerState controllerState) {
        if (this.isMutable)
            return this.operator.compute(this.argA.get(controllerState), this.argB.get(controllerState));

        if (this.cachedValue.doubleValue() == Double.MIN_VALUE)
            this.cachedValue.setValue(this.operator.compute(this.argA.get(controllerState), this.argB.get(controllerState)));

        return this.cachedValue.doubleValue();
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return this.usedVariables;
    }

    @Override
    public String toString() {
        return this.argA + " " + this.operator.symbol() + " " + this.argB;
    }
}
