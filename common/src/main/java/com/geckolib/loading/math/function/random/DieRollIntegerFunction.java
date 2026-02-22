package com.geckolib.loading.math.function.random;

import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns a random integer value based on the input values:
///
///   - Three inputs: Generates the sum of _n_ (first input) random values between the second (inclusive) and third input (inclusive)
///   - Four inputs: Generates the sum of _n_ (first input) random values between the second (inclusive) and third input (inclusive), seeded by the fourth input
///
public final class DieRollIntegerFunction extends MathFunction {
    private final MathValue rolls;
    private final MathValue min;
    private final MathValue max;
    private final @Nullable MathValue seed;
    private final @Nullable Random random;

    public DieRollIntegerFunction(MathValue... values) {
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
    public double compute(@Nullable ControllerState controllerState) {
        final int rolls = (int)(Math.floor(this.rolls.get(controllerState)));
        final int min = Mth.floor(this.min.get(controllerState));
        final int max = Mth.ceil(this.max.get(controllerState));
        int sum = 0;
        Random random;

        if (this.random != null && this.seed != null) {
            random = this.random;
            random.setSeed((long)this.seed.get(controllerState));
        }
        else {
            random = ThreadLocalRandom.current();
        }

        for (int i = 0; i < rolls; i++) {
            sum += min + random.nextInt(max + 1 - min);
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
