package com.geckolib.loading.math.function.generic;

import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the square root of the input value
public final class SqrtFunction extends MathFunction {
    private final MathValue value;

    public SqrtFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.sqrt";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Math.sqrt(this.value.get(controllerState));
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
