package software.bernie.geckolib.loading.math.function.random;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns a random value based on the input values:
 * <ul>
 *     <li>Three inputs: Generates the sum of <i>n</i> (first input) random values between the second (inclusive) and third input (exclusive)</li>
 *     <li>Four inputs: Generates the sum of <i>n</i> (first input) random values between the second (inclusive) and third input (exclusive), seeded by the fourth input</li>
 * </ul>
 */
public final class DieRollFunction extends MathFunction {
    private final MathValue rolls;
    private final MathValue min;
    private final MathValue max;
    private final @Nullable MathValue seed;
    private final @Nullable Random random;

    public DieRollFunction(MathValue... values) {
        super(values);

        this.rolls = values[0];
        this.min = values[1];
        this.max = values[2];
        this.seed = values.length >= 4 ? values[3] : null;
        this.random = this.seed != null ? new Random() : null;
    }

    @Override
    public String getName() {
        return "math.die_roll";
    }

    @Override
    public double compute(ControllerState controllerState) {
        final int rolls = (int)(Math.floor(this.rolls.get(controllerState)));
        final double min = this.min.get(controllerState);
        final double max = this.max.get(controllerState);
        double sum = 0;
        Random random;

        if (this.random != null && this.seed != null) {
            random = this.random;
            random.setSeed((long)this.seed.get(controllerState));
        }
        else {
            random = ThreadLocalRandom.current();
        }

        for (int i = 0; i < rolls; i++) {
            sum += min + random.nextDouble() * (max - min);
        }

        return sum;
    }

    @Override
    public boolean isMutable(MathValue... values) {
        if (values.length < 4)
            return true;

        return super.isMutable(values);
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public MathValue[] getArgs() {
        if (this.seed != null)
            return new MathValue[] {this.rolls, this.min, this.max, this.seed};

        return new MathValue[] {this.rolls, this.min, this.max};
    }
}
