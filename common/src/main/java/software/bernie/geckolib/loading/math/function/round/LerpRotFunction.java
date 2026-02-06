package software.bernie.geckolib.loading.math.function.round;

import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;
import software.bernie.geckolib.util.MiscUtil;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the first value plus the difference between the first and second input values multiplied by the third input value, wrapping the end result as a degrees value
public final class LerpRotFunction extends MathFunction {
    private final MathValue min;
    private final MathValue max;
    private final MathValue delta;

    public LerpRotFunction(MathValue... values) {
        super(values);

        this.min = values[0];
        this.max = values[1];
        this.delta = values[2];
    }

    @Override
    public String getName() {
        return "math.lerprotate";
    }

    @Override
    public double compute(ControllerState controllerState) {
        return MiscUtil.lerpYaw(this.delta.get(controllerState), this.min.get(controllerState), this.max.get(controllerState));
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.min, this.max, this.delta};
    }
}
