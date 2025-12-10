package software.bernie.geckolib.object;

import java.util.function.Function;

/**
 * Functional object that acts as a two-stage memoizing function of sorts.
 * <p>
 * Takes an input object and a mapping function. Up until the mapping function is called
 *
 * @param <I> The input object type
 * @param <O> The output object type
 */
public class DeferredCache<I, O> {
    private I input;
    private O output = null;
    private boolean computed = false;
    private final Function<I, O> mappingFunction;

    public DeferredCache(I input, Function<I, O> mappingFunction) {
        this.input = input;
        this.mappingFunction = mappingFunction;
    }

    /**
     * Retrieve the input object for this cache.
     * <p>
     * Can only be accessed until the output has been computed
     */
    public I getInput() {
        if (this.computed)
            throw new IllegalStateException("Attempting to access input after output of deferred cache has been calculated!");

        return this.input;
    }

    /**
     * Retrieve the output object for this cache.
     * <p>
     * Can only be accessed once the output has been computed
     */
    public O getOutput() {
        if (!this.computed)
            throw new IllegalStateException("Attempting to access output before it has been calculated!");

        return this.output;
    }

    /**
     * Compute the output object for this cache and invalidate the input
     *
     * @return The computed output
     */
    public O compute() {
        if (!this.computed) {
            this.output = this.mappingFunction.apply(this.input);
            this.input = null;
            this.computed = true;
        }

        return getOutput();
    }
}
