package software.bernie.geckolib.loading.math.function.generic;

import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the arc-sine of the input value angle (in radians)
 */
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
    public double compute() {
        final double value = this.value.get();

        if (value >= 1)
            return Math.PI / 2d;

        if (value == 0)
            return 0;

        if (value <= -1)
            return -Math.PI / 2d;

        return Math.asin(value);
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
