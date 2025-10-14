package software.bernie.geckolib.animatable.stateless;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * Stateless wrapper for {@link AnimationController}
 *
 * @see StatelessAnimatable
 */
public class StatelessAnimationController extends AnimationController<GeoAnimatable> {
    @Nullable
    protected RawAnimation currentAnim = null;

    public StatelessAnimationController(String name) {
        super(name, test -> PlayState.STOP);
    }

    /**
     * Set the current animation for this controller
     * <p>
     * This will be used to handle the {@link AnimationTest} at each render pass
     */
    public void setCurrentAnimation(@Nullable RawAnimation animation) {
        this.currentAnim = animation;
    }

    /**
     * Get the current animation state for this controller
     */
    @Nullable
    public RawAnimation getCurrentAnim() {
        return this.currentAnim;
    }

    @Override
    public AnimationStateHandler<GeoAnimatable> getStateHandler() {
        return this::overrideStateHandler;
    }

    @ApiStatus.Internal
    protected PlayState overrideStateHandler(AnimationTest<GeoAnimatable> test) {
        return getCurrentAnim() == null ? PlayState.STOP : test.setAndContinue(getCurrentAnim());
    }
}
