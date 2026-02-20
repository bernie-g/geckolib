package software.bernie.geckolib.loading.math.function.round;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the closest integer value to the input value
public final class RoundFunction extends MathFunction {
    private final MathValue value;

    public RoundFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.round";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Math.round(this.value.get(controllerState));
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
