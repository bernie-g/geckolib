package software.bernie.geckolib.loading.math.value;

import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.loading.math.MathValue;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Assigns a variable to the given value, then returns 0
 */
public record VariableAssignment(Variable variable, MathValue value) implements MathValue {
    @Override
    public double get(AnimationState<?> animationState) {
        this.variable.set(this.value.get(animationState));

        return 0;
    }

    @Override
    public String toString() {
        return this.variable.name() + "=" + this.value.toString();
    }
}
