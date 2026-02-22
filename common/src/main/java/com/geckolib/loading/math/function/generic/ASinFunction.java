package com.geckolib.loading.math.function.generic;

import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the arc-sine of the input value angle, with the input angle converted to radians
public final class ASinFunction extends MathFunction {
    private final MathValue value;

    public ASinFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.asin";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Math.asin(this.value.get(controllerState) * Mth.DEG_TO_RAD);
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
