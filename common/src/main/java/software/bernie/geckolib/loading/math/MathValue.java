package software.bernie.geckolib.loading.math;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Set;
import java.util.function.ToDoubleFunction;

/**
 * Base interface for all computational values in the math system
 * <p>
 * All mathematical objects are an extension of this interface, allowing for a recursively nestable
 * mathematical system that can be accessed via this one access point
 */
public interface MathValue extends ToDoubleFunction<ControllerState> {
    /**
     * Get computed or stored value based on the current AnimationState
     */
    double get(ControllerState controllerState);

    /**
     * Return whether this type of MathValue should be considered mutable; its value could change.<br>
     * This is used to cache calculated values, optimising computational work
     * <p>
     * By definition, any value that returns true from this method <b><u>must not</u></b>
     * utilise the passed {@link ControllerState}, and must instead be a stored constant or pre-computed value
     */
    default boolean isMutable() {
        return true;
    }

    /**
     * Return the {@link Variable}s that this value uses and/or contains.
     * <p>
     * This is used to optimise value retrieval during {@link GeoRenderState creation}
     */
    default Set<Variable> getUsedVariables() {
        return Set.of();
    }

    /**
     * Returns the collection of {@link Variable}s used by all the variables passed in
     * <p>
     * Only used internally when building the MathValue instance
     */
    @ApiStatus.Internal
    public static Set<Variable> collectUsedVariables(MathValue... values) {
        if (values.length == 0)
            return Set.of();

        if (values.length == 1)
            return values[0].getUsedVariables();

        Set<Variable> usedVariables = new ReferenceOpenHashSet<>();

        for (MathValue value : values) {
            usedVariables.addAll(value.getUsedVariables());
        }

        return usedVariables;
    }

    /**
     * Overloaded, use {@link #get} instead
     */
    @ApiStatus.Internal
    @Deprecated
    @Override
    default double applyAsDouble(ControllerState controllerState) {
        return get(controllerState);
    }
}
