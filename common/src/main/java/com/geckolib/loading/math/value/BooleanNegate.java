package com.geckolib.loading.math.value;

import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;

import java.util.Set;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// Returns **1** if the contained value is equal to **0**, otherwise returns **0**
public record BooleanNegate(MathValue value) implements MathValue {
    @Override
    public double get(@Nullable ControllerState controllerState) {
        return this.value.get(controllerState) == 0 ? 1 : 0;
    }

    @Override
    public boolean isMutable() {
        return this.value.isMutable();
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return this.value.getUsedVariables();
    }

    @Override
    public String toString() {
        return "!" + this.value;
    }
}
