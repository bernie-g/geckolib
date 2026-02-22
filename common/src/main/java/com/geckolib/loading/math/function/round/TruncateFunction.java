package com.geckolib.loading.math.function.round;

import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the closest value that is equal to the input value or closer to zero, and is equal to an integer
public final class TruncateFunction extends MathFunction {
    private final MathValue value;

    public TruncateFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.trunc";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return (long)this.value.get(controllerState);
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
