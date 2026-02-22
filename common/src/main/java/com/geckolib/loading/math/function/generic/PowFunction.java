package com.geckolib.loading.math.function.generic;

import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the input value raised to the power of the second input value
public final class PowFunction extends MathFunction {
    private final MathValue value;
    private final MathValue power;

    public PowFunction(MathValue... values) {
        super(values);

        this.value = values[0];
        this.power = values[1];
    }

    @Override
    public String getName() {
        return "math.pow";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Math.pow(this.value.get(controllerState), this.power.get(controllerState));
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.value, this.power};
    }
}
