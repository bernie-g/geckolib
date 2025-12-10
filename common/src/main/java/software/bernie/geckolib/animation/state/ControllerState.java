package software.bernie.geckolib.animation.state;

import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.object.EasingType;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Extracted data for an {@link AnimationController} for an animation pass
 * <p>
 * Effectively an animation equivalent of a RenderState.<br>
 * The AnimationController should solely rely on the data in this state to handle its animation state for the purpose of rendering
 */
public record ControllerState(AnimationPoint animationPoint, @Nullable AnimationPoint prevAnimationPoint, double transitionTime, int transitionTicks, boolean additive,
                              @Nullable EasingType easingOverride, GeoRenderState renderState, Reference2DoubleMap<Variable> queryValues) {
    /**
     * Get the partial tick value from the {@link GeoRenderState}
     */
    public float partialTick() {
        return this.renderState.getPartialTick();
    }

    /**
     * Helper method for retrieving the computed query value for a given variable
     * <p>
     * Returns 0 if the query value has not been computed. This should never happen in normal usage;
     * and is considered a misuse of the ControllerState to occur.
     */
    public double getQueryValue(Variable variable) {
        return this.queryValues.getDouble(variable);
    }
}
