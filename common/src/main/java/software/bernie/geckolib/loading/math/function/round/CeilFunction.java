package software.bernie.geckolib.loading.math.function.round;

import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.loading.math.function.MathFunction;
import software.bernie.geckolib.loading.math.MathValue;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the smallest value that is greater than or equal to the input value and is equal to an integer
 */
public final class CeilFunction extends MathFunction {
    private final MathValue value;

    public CeilFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.ceil";
    }

    @Override
    public double compute(AnimationState<?> animationState) {
        return Math.ceil(this.value.get(animationState));
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
