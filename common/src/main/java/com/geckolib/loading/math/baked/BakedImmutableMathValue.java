package com.geckolib.loading.math.baked;

import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.value.Constant;
import com.geckolib.loading.math.value.Variable;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/// Immutable half of the [BakedMathValue] bimorphic pair
public record BakedImmutableMathValue(Constant constant) implements BakedMathValue {
    @Override
    public double get(@Nullable ControllerState controllerState) {
        return this.constant.value();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return Collections.emptySet();
    }
}
