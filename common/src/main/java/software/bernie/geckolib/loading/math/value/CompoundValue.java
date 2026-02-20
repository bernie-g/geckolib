package software.bernie.geckolib.loading.math.value;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.Set;
import java.util.StringJoiner;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// Contains a collection of sub-expressions that evaluate before returning the last expression, or 0 if no return is defined.
/// Sub-expressions have no bearing on the final return with exception for where they may be setting variable values
public record CompoundValue(MathValue[] subValues, Set<Variable> usedVariables) implements MathValue {
    public CompoundValue(MathValue[] subValues) {
        this(subValues, MathValue.collectUsedVariables(subValues));
    }

    @Override
    public double get(@Nullable ControllerState controllerState) {
        for (int i = 0; i < this.subValues.length - 1; i++) {
            this.subValues[i].get(controllerState);
        }

        return this.subValues[this.subValues.length - 1].get(controllerState);
    }

    @Override
    public boolean isMutable() {
        if (!this.usedVariables.isEmpty())
            return true;

        for (MathValue subValue : this.subValues) {
            if (subValue.isMutable())
                return true;
        }

        return false;
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner("; ");

        for (MathValue subValue : this.subValues) {
            joiner.add(subValue.toString());
        }

        return joiner.toString();
    }
}
