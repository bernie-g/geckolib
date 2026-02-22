package com.geckolib.loading.math.function.limit;

import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the first input value if is larger than the second input value and less than the third input value; or else returns the nearest of the second two input values
public final class ClampFunction extends MathFunction {
    private final MathValue value;
    private final MathValue min;
    private final MathValue max;

    public ClampFunction(MathValue... values) {
        super(values);

        this.value = values[0];
        this.min = values[1];
        this.max = values[2];
    }

    @Override
    public String getName() {
        return "math.clamp";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Mth.clamp(this.value.get(controllerState), this.min.get(controllerState), this.max.get(controllerState));
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.value, this.min, this.max};
    }
}
