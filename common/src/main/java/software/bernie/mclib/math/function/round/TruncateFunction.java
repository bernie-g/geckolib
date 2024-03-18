package software.bernie.mclib.math.function.round;

import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.function.MathFunction;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the closest value that is equal to the input value or closer to zero, and is equal to an integer
 */
public final class TruncateFunction extends MathFunction {
    private final MathValue value;

    public TruncateFunction(String name, MathValue... values) {
        super(name);

        this.value = values[0];
    }

    @Override
    public double compute() {
        return (long)this.value.get();
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
