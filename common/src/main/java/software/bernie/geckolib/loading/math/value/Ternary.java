package software.bernie.geckolib.loading.math.value;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.Set;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// Returns one of two stored values dependent on the result of the stored condition value.
/// This returns such that a non-zero result from the condition will return the **true** stored value, otherwise returning the **false** stored value
public record Ternary(MathValue condition, MathValue trueValue, MathValue falseValue, Set<Variable> usedVariables) implements MathValue {
    public Ternary(MathValue condition, MathValue trueValue, MathValue falseValue) {
        this(condition, trueValue, falseValue, MathValue.collectUsedVariables(condition, trueValue, falseValue));
    }

    @Override
    public double get(@Nullable ControllerState controllerState) {
        return this.condition.get(controllerState) != 0 ? this.trueValue.get(controllerState) : this.falseValue.get(controllerState);
    }

    @Override
    public boolean isMutable() {
        return this.condition.isMutable() || this.trueValue.isMutable() || this.falseValue.isMutable();
    }

    @Override
    public String toString() {
        return this.condition + " ? " + this.trueValue + " : " + this.falseValue;
    }
}
