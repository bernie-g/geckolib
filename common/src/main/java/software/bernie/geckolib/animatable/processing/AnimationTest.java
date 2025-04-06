package software.bernie.geckolib.animatable.processing;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Objects;

/**
 * Animation pass handler for end-users' animatables
 * <p>
 * This is where users would set their selected animation to play,
 * stop the controller, or any number of other animation-related actions.
 */
public record AnimationTest<T extends GeoAnimatable>(T animatable, GeoRenderState renderState, AnimatableManager<T> manager, AnimationController<T> controller) {
    /**
     * Gets whether the current {@link GeoAnimatable} is considered to be moving for animation purposes
     * <p>
     * Note that this is a best-case approximation of movement, and you should ideally be using Molang
     *
     */
    public boolean isMoving() {
        return this.renderState.getGeckolibData(DataTickets.IS_MOVING);
    }

    /**
     * Sets the animation for the controller to start/continue playing
     * <p>
     * Basically just a shortcut for <pre>{@code controller().setAnimation()}</pre>
     *
     * @param animation The animation to play
     */
    public void setAnimation(RawAnimation animation) {
        this.controller.setAnimation(animation);
    }

    /**
     * Helper method to set an animation to start/continue playing, and return {@link PlayState#CONTINUE}
     */
    public PlayState setAndContinue(RawAnimation animation) {
        this.controller.setAnimation(animation);

        return PlayState.CONTINUE;
    }

    /**
     * Checks whether the current {@link AnimationController}'s last animation was the one provided
     * <p>
     * This allows for multi-stage animation shifting where the next animation to play may depend on the previous one
     *
     * @param animation The animation to check
     * @return Whether the controller's last animation is the one provided
     */
    public boolean isCurrentAnimation(RawAnimation animation) {
        return Objects.equals(this.controller.getCurrentRawAnimation(), animation);
    }

    /**
     * Similar to {@link #isCurrentAnimation}, but additionally checks the current stage of the animation by name
     * <p>
     * This can be used to check if a multi-stage animation has reached a given stage (if it is running at all)
     * <p>
     * Note that this will still return true even if the animation has finished, matching with the last animation stage in the {@link RawAnimation} last provided
     *
     * @param name The name of the animation stage to check (I.E. "move.walk")
     * @return Whether the controller's current stage is the one provided
     */
    public boolean isCurrentAnimationStage(String name) {
        return this.controller.getCurrentAnimation() != null && this.controller.getCurrentAnimation().animation().name().equals(name);
    }

    /**
     * Helper method for {@link AnimationController#forceAnimationReset()}
     * <p>
     * This should be used in controllers when stopping a non-looping animation, so that it is reset to the start for the next time it starts
     */
    public void resetCurrentAnimation() {
        this.controller.forceAnimationReset();
    }

    /**
     * Helper method for {@link AnimationController#setAnimationSpeed}
     *
     * @param speed The speed modifier for the controller (2 = twice as fast, 0.5 = half as fast, etc)
     */
    public void setControllerSpeed(float speed) {
        this.controller.setAnimationSpeed(speed);
    }

    /**
     * @return Whether the RenderState has data associated with the given {@link DataTicket}
     */
    public boolean hasData(DataTicket<?> dataTicket) {
        return this.renderState.hasGeckolibData(dataTicket);
    }

    /**
     * Get previously set data on the RenderState by its associated {@link DataTicket}.
     * <p>
     * Note that you should <b><u>NOT</u></b> be attempting to retrieve data you don't know exists.<br>
     * Use {@link #hasData(DataTicket)} if unsure
     *
     * @param dataTicket The DataTicket associated with the data
     * @return The data contained on this RenderState, null if the data is set to null, or an exception if the data doesn't exist
     */
    @Nullable
    public <D> D getData(DataTicket<D> dataTicket) {
        return this.renderState.getGeckolibData(dataTicket);
    }

    /**
     * Get previously set data on the RenderState by its associated {@link DataTicket},
     * or a default value if the data does not exist
     *
     * @param dataTicket The DataTicket associated with the data
     * @param defaultValue The fallback value if no data has been set for the given DataTicket
     * @return The data contained on this RenderState, null if the data is set to null, or {@code defaultValue} if not present
     */
    @Nullable
    public <D> D getDataOrDefault(DataTicket<D> dataTicket, @Nullable D defaultValue) {
        D data = getData(dataTicket);

        return data != null || this.renderState.hasGeckolibData(dataTicket) ? data : defaultValue;
    }
}
