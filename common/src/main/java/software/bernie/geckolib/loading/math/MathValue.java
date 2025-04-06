package software.bernie.geckolib.loading.math;

import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * Base interface for all computational values in the math system
 * <p>
 * All mathematical objects are an extension of this interface, allowing for a recursively nestable
 * mathematical system that can be accessed via this one access point
 */
public interface MathValue extends ToDoubleFunction<AnimationState<?>> {
    /**
     * Get computed or stored value based on the current AnimationState
     */
    double get(AnimationState<?> animationState);

    /**
     * Return whether this type of MathValue should be considered mutable; its value could change.<br>
     * This is used to cache calculated values, optimising computational work
     * <p>
     * By definition, any value that returns true from this method <b><u>must not</u></b>
     * utilise the passed {@link AnimationState}, and must instead be a stored constant or pre-computed value
     */
    default boolean isMutable() {
        return true;
    }

    /**
     * Return the {@link Variable}s that this value uses and/or contains.
     * <p>
     * This is used to optimise value retrieval during {@link GeoRenderState creation}
     */
    default List<Variable> getUsedVariables() {
        return List.of();
    }

    /**
     * Overloaded, use {@link #get} instead
     */
    @ApiStatus.Internal
    @Override
    default double applyAsDouble(AnimationState<?> animationState) {
        return get(animationState);
    }
}
