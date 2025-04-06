package software.bernie.geckolib.animation.keyframe.event;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.animation.keyframe.event.data.KeyFrameData;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Keyframe event object generated when a keyframe marker is encountered for the current render pass by an
 * {@link AnimationController}
 *
 * @param animationState The {@link AnimationState} for the current render pass
 * @param controller The {@link AnimationController} responsible for the currently playing animation
 * @param keyframeData The {@link KeyFrameData} relevant to the encountered {@link Keyframe}
 *
 * @see AnimationController.KeyframeEventHandler
 */
public record KeyFrameEvent<T extends GeoAnimatable, E extends KeyFrameData>(AnimationState<T> animationState, AnimationController<T> controller, E keyframeData) {
	/**
	 * Gets the {@link GeoRenderState} for the current render pass responsible for triggering this keyframe
	 */
	public GeoRenderState getRenderState() {
		return this.animationState.renderState();
	}

	/**
	 * Gets the amount of time (in ticks) that have passed in either the current transition or
	 * animation, depending on the controller's AnimationState.
	 */
	public double getAnimationTick() {
		return this.animationState.renderState().getGeckolibData(DataTickets.ANIMATION_TICKS);
	}
}
