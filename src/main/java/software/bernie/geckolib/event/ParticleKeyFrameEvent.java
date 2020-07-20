package software.bernie.geckolib.event;

import software.bernie.geckolib.animation.controller.AnimationController;

public class ParticleKeyFrameEvent <T> extends AnimationEvent<T>
{
	public final String effect;
	public final String locator;
	public final String script;

	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *
	 * @param entity        the entity
	 * @param animationTick The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param controller    the controller
	 */
	public ParticleKeyFrameEvent(T entity, double animationTick, String effect, String locator, String script, AnimationController controller)
	{
		super(entity, animationTick, controller);
		this.effect = effect;
		this.locator = locator;
		this.script = script;
	}
}
