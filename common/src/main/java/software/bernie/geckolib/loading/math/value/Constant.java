package software.bernie.geckolib.loading.math.value;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// An immutable double value
public record Constant(double value) implements MathValue {
    @Override
    public double get(@Nullable ControllerState controllerState) {
        return this.value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
