package com.geckolib.loading.math.baked;

import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.value.Variable;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/// Mutable half of the [BakedMathValue] bimorphic pair
public record BakedMutableMathValue(MathValue value) implements BakedMathValue {
    @Override
    public double get(@Nullable ControllerState controllerState) {
        return this.value.get(controllerState);
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return this.value.getUsedVariables();
    }
}
