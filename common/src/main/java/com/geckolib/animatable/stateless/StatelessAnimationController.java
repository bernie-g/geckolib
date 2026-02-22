package com.geckolib.animatable.stateless;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;

/// Stateless wrapper for [AnimationController]
///
/// @see StatelessAnimatable
public class StatelessAnimationController extends AnimationController<GeoAnimatable> {
    protected @Nullable RawAnimation currentAnim = null;

    public StatelessAnimationController(String name) {
        super(name, StatelessAnimationController::overrideStateHandler);
    }

    /// Set the current animation for this controller
    ///
    /// This will be used to handle the [AnimationTest] at each render pass
    public void setCurrentAnimation(@Nullable RawAnimation animation) {
        this.currentAnim = animation;
    }

    @ApiStatus.Internal
    protected static PlayState overrideStateHandler(AnimationTest<GeoAnimatable> test) {
        if (test.controller() instanceof StatelessAnimationController controller && controller.currentAnim != null)
            return test.setAndContinue(controller.currentAnim);

        return PlayState.STOP;
    }
}
