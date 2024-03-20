package software.bernie.mclib.math.function;

import software.bernie.mclib.math.MathValue;

import java.util.StringJoiner;

/**
 * Computational function wrapping a {@link MathValue}
 * <p>
 * Subclasses of this represent mathematical functions to be performed on a pre-defined number of input variables.
 * <br>
 * Functions should be deterministic - identical input values should result in identical output values, and values should adhere to the {@link MathValue#isMutable() Mutability} contract of {@link MathValue} for determining result-caching.
 */
public abstract class MathFunction implements MathValue {
    private final boolean isMutable;
    private double cachedValue = Double.MIN_VALUE;

    protected MathFunction(MathValue... values) {
        validate(values);

        this.isMutable = isMutable(values);
    }

    /**
     * Return the expression name/symbol for this function.
     * This is the value that would be seen in a mathematical expression string
     */
    public abstract String getName();

    @Override
    public final double get() {
        if (this.isMutable)
            return compute();

        if (this.cachedValue == Double.MIN_VALUE)
            this.cachedValue = compute();

        return this.cachedValue;
    }

    /**
     * Compute the result of this function from its stored arguments
     */
    public abstract double compute();

    /**
     * @return Whether this function should be considered mutable; the value could change
     * <br>
     * This would normally be determined by whether any of the stored args are mutable
     */
    public boolean isMutable(MathValue... values) {
        for (int i = 0; i < getMinArgs(); i++) {
            if (values[i].isMutable())
                return true;
        }

        return false;
    }

    /**
     * @return The minimum number of args required for this function to be computable
     */
    public abstract int getMinArgs();

    /**
     * @return The arguments (in order) stored in this function
     */
    public abstract MathValue[] getArgs();

    /**
     * Validate this function's arguments against its minimum requirements, throwing an exception for invalid argument states
     */
    public void validate(MathValue... inputs) throws IllegalArgumentException {
        final int minArgs = getMinArgs();

        if (inputs.length < minArgs)
            throw new IllegalArgumentException(String.format("Function '%s' at least %s arguments. Only %s given!", getName(), minArgs, inputs.length));
    }

    @Override
    public final boolean isMutable() {
        return this.isMutable;
    }

    @Override
    public String toString() {
        final MathValue[] args = getArgs();
        final StringJoiner joiner = new StringJoiner(", ", "(", ")");

        for (MathValue arg : args) {
            joiner.add(arg.toString());
        }

        return getName() + joiner;
    }

    /**
     * Factory interface for {@link MathFunction}.
     * Functionally equivalent to a <code>Function<MathValue[], MathFunction></code>, but with a more concise user-facing handle
     * @param <T>
     */
    @FunctionalInterface
    public interface Factory<T extends MathFunction> {
        /**
         * Instantiate a new {@link MathFunction} for the given input values
         */
        T create(MathValue... values);
    }
}
