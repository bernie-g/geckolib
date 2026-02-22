package com.geckolib.loading.math.function.misc;

import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Converts the input value to degrees
public final class ToDegFunction extends MathFunction {
    private final MathValue value;

    public ToDegFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.to_deg";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Math.toDegrees(this.value.get(controllerState));
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
