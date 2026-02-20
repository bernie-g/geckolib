package software.bernie.geckolib.loading.math.value;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.Set;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// Negated equivalent of the stored value; returning a positive number if the stored value is negative, or a negative value if the stored value is positive
public record Negative(MathValue value) implements MathValue {
    @Override
    public double get(@Nullable ControllerState controllerState) {
        return -this.value.get(controllerState);
    }

    @Override
    public boolean isMutable() {
        return this.value.isMutable();
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return this.value.getUsedVariables();
    }

    @Override
    public String toString() {
        if (this.value instanceof Constant)
            return "-" + this.value;

        return "-" + "(" + this.value + ")";
    }
}
