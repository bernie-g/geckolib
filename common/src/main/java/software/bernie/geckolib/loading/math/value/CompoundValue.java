package software.bernie.geckolib.loading.math.value;

import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.Set;
import java.util.StringJoiner;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Contains a collection of sub-expressions that evaluate before returning the last expression, or 0 if no return is defined.
 * Sub-expressions have no bearing on the final return with exception for where they may be setting variable values
 */
public record CompoundValue(MathValue[] subValues, Set<Variable> usedVariables) implements MathValue {
    public CompoundValue(MathValue[] subValues) {
        this(subValues, MathValue.collectUsedVariables(subValues));
    }

    @Override
    public double get(AnimationState<?> animationState) {
        for (int i = 0; i < this.subValues.length - 1; i++) {
            this.subValues[i].get(animationState);
        }

        return this.subValues[this.subValues.length - 1].get(animationState);
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
