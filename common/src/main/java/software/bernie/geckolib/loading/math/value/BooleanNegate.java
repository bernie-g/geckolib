package software.bernie.geckolib.loading.math.value;

import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.Set;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns <b>1</b> if the contained value is equal to <b>0</b>, otherwise returns <b>0</b>
 */
public record BooleanNegate(MathValue value) implements MathValue {
    @Override
    public double get(AnimationState<?> animationState) {
        return this.value.get(animationState) == 0 ? 1 : 0;
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
        return "!" + this.value.toString();
    }
}
