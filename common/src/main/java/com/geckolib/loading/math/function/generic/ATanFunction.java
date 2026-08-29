package com.geckolib.loading.math.function.generic;

import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;
import org.jspecify.annotations.Nullable;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the arc-tangent of the input value angle, with the input angle converted to radians
public final class ATanFunction extends MathFunction {
    private final MathValue value;

    public ATanFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.atan";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        final double value = this.value.get(controllerState);

        if (value == 0)
            return 0;

        return Math.atan(value);
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
