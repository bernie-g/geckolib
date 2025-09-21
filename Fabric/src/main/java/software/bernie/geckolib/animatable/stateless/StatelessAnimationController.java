package software.bernie.geckolib.animatable.stateless;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/**
 * Stateless wrapper for {@link AnimationController}
 *
 * @see StatelessAnimatable
 */
public class StatelessAnimationController extends AnimationController<GeoAnimatable> {
    @Nullable
    protected RawAnimation currentAnim = null;

    public StatelessAnimationController(GeoAnimatable animatable, String name) {
        super(animatable, name, state -> PlayState.STOP);
    }

    /**
     * Set the current animation for this controller
     * <p>
     * This will be used to handle the {@link AnimationState} at each render pass
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
    protected PlayState overrideStateHandler(AnimationState<GeoAnimatable> test) {
        return getCurrentAnim() == null ? PlayState.STOP : test.setAndContinue(getCurrentAnim());
    }
}
