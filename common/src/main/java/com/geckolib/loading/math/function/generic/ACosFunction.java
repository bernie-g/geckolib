package com.geckolib.loading.math.function.generic;

import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;
import org.jspecify.annotations.Nullable;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the arc-cosine of the input value angle (in radians)
public final class ACosFunction extends MathFunction {
    private final MathValue value;

    public ACosFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.acos";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        final double value = this.value.get(controllerState);

        if (value >= 1)
            return 0;

        if (value <= -1)
            return Math.PI;

        return Math.acos(value);
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
