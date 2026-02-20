package software.bernie.geckolib.loading.math.function.round;

import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the first value plus the difference between the first and second input values multiplied by the third input value
public final class LerpFunction extends MathFunction {
    private final MathValue min;
    private final MathValue max;
    private final MathValue delta;

    public LerpFunction(MathValue... values) {
        super(values);

        this.min = values[0];
        this.max = values[1];
        this.delta = values[2];
    }

    @Override
    public String getName() {
        return "math.lerp";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Mth.lerp(this.delta.get(controllerState), this.min.get(controllerState), this.max.get(controllerState));
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
