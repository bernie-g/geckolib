package com.geckolib.animation.state;

import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import org.jspecify.annotations.Nullable;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.EasingType;
import com.geckolib.loading.math.value.Variable;
import com.geckolib.renderer.base.GeoRenderState;

/// Extracted data for an [AnimationController] for an animation pass
///
/// Effectively an animation equivalent of a RenderState.
/// The AnimationController should solely rely on the data in this state to handle its animation state for the purpose of rendering
///
/// @param animationPoint The animation point for this render pass
/// @param prevAnimationPoint The animation point for the last animation played on this controller, if transitioning from a previous animation
/// @param transitionTime The transition-relative time a transition has been running (in seconds)
/// @param transitionTicks The length in ticks that transitions should take, as defined by the [AnimationController]
/// @param additive Whether this controller is applying animations additively
/// @param easingOverride The optional easing override for this render pass
/// @param renderState The RenderState instance for this render pass
/// @param queryValues The pre-computed Molang query values for this render pass, for the contained AnimationPoints
public record ControllerState(AnimationPoint animationPoint, @Nullable AnimationPoint prevAnimationPoint, double transitionTime, int transitionTicks, boolean additive,
                              @Nullable EasingType easingOverride, GeoRenderState renderState, Reference2DoubleMap<Variable> queryValues) {
    /// Get the partial tick value from the [GeoRenderState]
    public float partialTick() {
        return this.renderState.getPartialTick();
    }

    /// Helper method for retrieving the computed query value for a given variable
    ///
    /// Returns 0 if the query value has not been computed. This should never happen in normal usage;
    /// and is considered a misuse of the ControllerState to occur.
    public double getQueryValue(Variable variable) {
        return this.queryValues.getDouble(variable);
    }
}
