package software.bernie.geckolib.loading.math.function.generic;

import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the cosine of the input value angle, with the input angle converted to radians
public final class CosFunction extends MathFunction {
    private final MathValue value;

    public CosFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.cos";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Mth.cos((float)this.value.get(controllerState) * Mth.DEG_TO_RAD);
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
