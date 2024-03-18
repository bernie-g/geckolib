package software.bernie.mclib.math.function.limit;

import net.minecraft.util.Mth;
import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.function.MathFunction;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the first input value if is larger than the second input value and less than the third input value; or else returns the nearest of the second two input values
 */
public final class ClampFunction extends MathFunction {
    private final MathValue value;
    private final MathValue min;
    private final MathValue max;

    public ClampFunction(String name, MathValue... values) {
        super(name);

        this.value = values[0];
        this.min = values[1];
        this.max = values[2];
    }

    @Override
    public double compute() {
        return Mth.clamp(this.value.get(), this.min.get(), this.max.get());
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
