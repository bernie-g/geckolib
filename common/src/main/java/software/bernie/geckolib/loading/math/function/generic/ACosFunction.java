package software.bernie.geckolib.loading.math.function.generic;

import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

/**
 * {@link MathFunction} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the arc-cosine of the input value angle, with the input angle converted to radians
 */
public final class ACosFunction extends MathFunction {
    private final MathValue value;

    public ACosFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.acos";
    }

    @Override
    public double compute(ControllerState controllerState) {
        return Math.acos((float)this.value.get(controllerState) * Mth.DEG_TO_RAD);
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
