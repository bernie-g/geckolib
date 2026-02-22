package com.geckolib.loading.math;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.value.Variable;
import com.geckolib.renderer.base.GeoRenderState;

import java.util.Set;
import java.util.function.ToDoubleFunction;

/// Base interface for all computational values in the math system
///
/// All mathematical objects are an extension of this interface, allowing for a recursively nestable
/// mathematical system that can be accessed via this one access point
public interface MathValue extends ToDoubleFunction<ControllerState> {
    /// Get computed or stored value based on the current AnimationState
    ///
    /// If this MathValue uses the `controllerState`, it should return `true` from [#isMutable()]
    /// @param controllerState The ControllerState for the current render pass. May be `null` if [#isMutable()] returns false
    double get(@Nullable ControllerState controllerState);

    /// Return whether this type of MathValue should be considered mutable; its value could change.
    /// This is used to cache calculated values, optimising computational work
    ///
    /// By definition, any value that returns true from this method **<u>must not</u>**
    /// utilise the passed [ControllerState], and must instead be a stored constant or pre-computed value
    default boolean isMutable() {
        return true;
    }

    /// Return the [Variable]s that this value uses and/or contains.
    ///
    /// This is used to optimise value retrieval during [creation][GeoRenderState]
    default Set<Variable> getUsedVariables() {
        return Set.of();
    }

    /// Returns the collection of [Variable]s used by all the variables passed in
    ///
    /// Only used internally when building the MathValue instance
    @ApiStatus.Internal
    static Set<Variable> collectUsedVariables(MathValue... values) {
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

    /// Overloaded, use [#get] instead
    @ApiStatus.Internal
    @Deprecated
    @Override
    default double applyAsDouble(ControllerState controllerState) {
        return get(controllerState);
    }
}
