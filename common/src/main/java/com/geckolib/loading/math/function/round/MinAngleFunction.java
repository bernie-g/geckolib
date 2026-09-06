package com.geckolib.loading.math.function.round;

import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Minimizes the magnitude of the input value angle (in degrees), clamped between -180 and 180 (inclusive)
public final class MinAngleFunction extends MathFunction {
    private final MathValue value;

    public MinAngleFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.min_angle";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Mth.wrapDegrees(this.value.get(controllerState));
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
