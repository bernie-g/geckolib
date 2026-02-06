package software.bernie.geckolib.loading.math.value;

import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.Set;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// Assigns a variable to the given value, then returns 0
public record VariableAssignment(Variable variable, MathValue value) implements MathValue {
    @Override
    public double get(ControllerState controllerState) {
        this.variable.set(this.value.get(controllerState));

        return 0;
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return this.value.getUsedVariables();
    }

    @Override
    public String toString() {
        return this.variable.name() + "=" + this.value;
    }
}
