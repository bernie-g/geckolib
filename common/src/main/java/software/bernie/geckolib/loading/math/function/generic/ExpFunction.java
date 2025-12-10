package software.bernie.geckolib.loading.math.function.generic;

import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns euler's number raised to the power of the input value
 */
public final class ExpFunction extends MathFunction {
    private final MathValue value;

    public ExpFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.exp";
    }

    @Override
    public double compute(ControllerState controllerState) {
        return Math.exp((float)this.value.get(controllerState));
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
