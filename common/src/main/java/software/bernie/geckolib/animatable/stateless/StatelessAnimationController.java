package software.bernie.geckolib.animatable.stateless;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTest;

/**
 * Stateless wrapper for {@link AnimationController}
 *
 * @see StatelessAnimatable
 */
public class StatelessAnimationController extends AnimationController<GeoAnimatable> {
    protected @Nullable RawAnimation currentAnim = null;

    public StatelessAnimationController(String name) {
        super(name, StatelessAnimationController::overrideStateHandler);
    }

    /**
     * Set the current animation for this controller
     * <p>
     * This will be used to handle the {@link AnimationTest} at each render pass
     */
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
