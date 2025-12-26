package software.bernie.geckolib.animatable.stateless;

import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * Special handler for GeoAnimatables, to allow for an alternate handling of animations.
 * <p>
 * This handler is less efficient than the recommended animation handling system; but offers an alternative
 * for those who prefer this style of animation handling.
 * <p>
 * To keep things simple, this handler assumes each animation should be handled on a separate controller.
 * This leaves state handling to the user, requiring that 'conflicting' animations be stopped as necessary.
 * <p>
 * Additionally, all methods can be called on both server and client side unless otherwise noted, for ease-of-use.
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Stateless-Animatable-Handling-(Geckolib5)">Stateless Animatables - GeckoLib Wiki</a>
 * @see StatelessGeoEntity
 * @see StatelessGeoBlockEntity
 * @see StatelessGeoSingletonAnimatable
 * @see StatelessGeoObject
 */
public sealed interface StatelessAnimatable permits StatelessGeoEntity, StatelessGeoBlockEntity, StatelessGeoSingletonAnimatable, StatelessGeoObject {
    /**
     * Start or continue an animation, letting its pre-defined loop type determine whether it should loop or not
     */
    default void playAnimation(String animation) {
        playAnimation(RawAnimation.begin().thenPlay(animation));
    }

    /**
     * Start or continue an animation, forcing it to loop continuously until stopped
     */
    default void playLoopingAnimation(String animation) {
        playAnimation(RawAnimation.begin().thenLoop(animation));
    }

    /**
     * Start or continue an animation, then hold the pose at the end of the animation until otherwise stopped
     */
    default void playAndHoldAnimation(String animation) {
        playAnimation(RawAnimation.begin().thenPlayAndHold(animation));
    }

    /**
     * Stop an already-playing animation
     */
    default void stopAnimation(RawAnimation animation) {
        stopAnimation(animation.getStageCount() == 1 ? animation.getAnimationStages().getFirst().animationName() : animation.toString());
    }

    /**
     * Start or continue a pre-defined animation
     */
    void playAnimation(RawAnimation animation);

    /**
     * Stop an already-playing animation
     */
    void stopAnimation(String animation);

    @ApiStatus.Internal
    default void handleClientAnimationPlay(GeoAnimatable animatable, long animatableId, RawAnimation animation) {
        AnimatableManager<GeoAnimatable> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(animatableId);
        String animKey = animation.getStageCount() == 1 ? animation.getAnimationStages().getFirst().animationName() : animation.toString();
        AnimationController<?> controller = animatableManager.getAnimationControllers().computeIfAbsent(animKey, StatelessAnimationController::new);

        if (controller instanceof StatelessAnimationController statelessController)
            statelessController.setCurrentAnimation(animation);
    }

    @ApiStatus.Internal
    default void handleClientAnimationStop(GeoAnimatable animatable, long animatableId, String animName) {
        AnimatableManager<GeoAnimatable> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(animatableId);

        if (animatableManager.getAnimationControllers().get(animName) instanceof StatelessAnimationController statelessController)
            statelessController.setCurrentAnimation(null);
    }
}
