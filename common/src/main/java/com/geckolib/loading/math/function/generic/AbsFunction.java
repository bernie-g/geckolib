package com.geckolib.loading.math.function.generic;

import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the absolute (non-negative) equivalent of the input value
public final class AbsFunction extends MathFunction {
    private final MathValue value;

    public AbsFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.abs";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Math.abs(this.value.get(controllerState));
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.value};
    }
}
