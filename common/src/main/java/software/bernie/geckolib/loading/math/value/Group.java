package software.bernie.geckolib.loading.math.value;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.Set;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// An unaltered return of the stored MathValue
public record Group(MathValue contents) implements MathValue {
    @Override
    public double get(@Nullable ControllerState controllerState) {
        return this.contents.get(controllerState);
    }

    @Override
    public boolean isMutable() {
        return this.contents.isMutable();
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return this.contents.getUsedVariables();
    }

    @Override
    public String toString() {
        return "(" + this.contents + ")";
    }
}
