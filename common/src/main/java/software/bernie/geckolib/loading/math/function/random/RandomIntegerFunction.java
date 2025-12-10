package software.bernie.geckolib.loading.math.function.random;

import org.jetbrains.annotations.Nullable;
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
 * Returns a random integer value based on the input values:
 * <ul>
 *     <li>A single input generates a value between 0 and that input (exclusive)</li>
 *     <li>Two inputs generates a random value between the first (inclusive) and second input (inclusive)</li>
 *     <li>Three inputs generates a random value between the first (inclusive) and second input (inclusive), seeded by the third input</li>
 * </ul>
 */
public final class RandomIntegerFunction extends MathFunction {
    private final MathValue valueA;
    @Nullable
    private final MathValue valueB;
    @Nullable
    private final MathValue seed;
    @Nullable
    private final Random random;

    public RandomIntegerFunction(MathValue... values) {
        super(values);

        this.valueA = values[0];
        this.valueB = values.length >= 2 ? values[1] : null;
        this.seed = values.length >= 3 ? values[2] : null;
        this.random = this.seed != null ? new Random() : null;
    }

    @Override
    public String getName() {
        return "math.random_integer";
    }

    @Override
    public double compute(ControllerState controllerState) {
        int result;
        int valueA = (int)Math.round(this.valueA.get(controllerState));
        Random random;

        if (this.random != null) {
            this.random.setSeed((long)this.seed.get(controllerState));
            random = this.random;
        }
        else {
            random = ThreadLocalRandom.current();
        }

        if (this.valueB != null) {
            int valueB = (int)Math.round(this.valueB.get(controllerState));
            int min = Math.min(valueA, valueB);
            int max = Math.max(valueA, valueB);

            result = min + random.nextInt(max + 1 - min);
        }
        else {
            result = random.nextInt(0, valueA + 1);
        }

        return result;
    }

    @Override
    public boolean isMutable(MathValue... values) {
        if (values.length < 3)
            return true;

        return super.isMutable(values);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public MathValue[] getArgs() {
        if (this.seed != null)
            return new MathValue[] {this.valueA, this.valueB, this.seed};

        if (this.valueB != null)
            return new MathValue[] {this.valueA, this.valueB};

        return new MathValue[] {this.valueA};
    }
}
