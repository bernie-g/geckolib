package com.geckolib.loading.math.value;

import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.Operator;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.jspecify.annotations.Nullable;

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
            return compute(controllerState);

        if (this.cachedValue.doubleValue() == Double.MIN_VALUE)
            this.cachedValue.setValue(compute(controllerState));

        return this.cachedValue.doubleValue();
    }

    private double compute(@Nullable ControllerState controllerState) {
        if (this.operator == Operator.OR || this.operator == Operator.AND) {
            if (this.operator.compute(this.argA.get(controllerState), 0) != 0)
                return 1;

            return this.operator.compute(0, this.argB.get(controllerState)) != 0 ? 1 : 0;
        }

        return this.operator.compute(this.argA.get(controllerState), this.argB.get(controllerState));
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
