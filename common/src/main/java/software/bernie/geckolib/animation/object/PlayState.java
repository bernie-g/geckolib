package software.bernie.geckolib.animation.object;

import software.bernie.geckolib.animation.AnimationController;

/**
 * State enum to define whether an {@link AnimationController} should continue or stop
 *
 * <table>
 *     <caption>States</caption>
 *     <tr><th>State</th><th>Description</th></tr>
 *     <tr><td>{@link #CONTINUE}</td><td>Play the currently playing animation</td></tr>
 *     <tr><td>{@link #PAUSE}</td><td>Pause the currently playing animation, freezing its position in the timeline</td></tr>
 *     <tr><td>{@link #STOP}</td><td>Stop the currently playing animation, resetting the animation time to 0</td></tr>
 * </table>
 */
public enum PlayState {
	CONTINUE,
    PAUSE,
	STOP
}
