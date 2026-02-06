package software.bernie.geckolib.loading.math.function.round;

import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the largest value that is less than or equal to the input value and is equal to an integer
public final class FloorFunction extends MathFunction {
    private final MathValue value;

    public FloorFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.floor";
    }

    @Override
    public double compute(ControllerState controllerState) {
        return Math.floor(this.value.get(controllerState));
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
