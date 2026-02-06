package software.bernie.geckolib.loading.math.function.generic;

import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the arc-tangent theta of the input rectangular coordinate values (y,x), with the output converted to degrees
public final class ATan2Function extends MathFunction {
    private final MathValue y;
    private final MathValue x;

    public ATan2Function(MathValue... values) {
        super(values);

        this.y = values[0];
        this.x = values[1];
    }

    @Override
    public String getName() {
        return "math.atan2";
    }

    @Override
    public double compute(ControllerState controllerState) {
        return Math.atan2(this.y.get(controllerState), this.x.get(controllerState)) * Mth.RAD_TO_DEG;
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.y, this.x};
    }
}
