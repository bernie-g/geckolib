package com.geckolib.loading.math.value;

import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;

import java.util.Set;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// An unaltered return of the stored MathValue
public record Group(MathValue contents) implements MathValue {
    @Override
    public double get(@Nullable ControllerState controllerState) {
        return this.contents.get(controllerState);
    }

    @Override
    public boolean isMutable() {
        return this.contents.isMutable();
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return this.contents.getUsedVariables();
    }

    @Override
    public String toString() {
        return "(" + this.contents + ")";
    }
}
