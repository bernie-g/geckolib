package software.bernie.geckolib.animatable.manager;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.dataticket.DataTicket;

import java.util.Map;

/**
 * Context-aware wrapper for {@link AnimatableManager}
 * <p>
 * This can be used for things like perspective-dependent animation handling and other similar functionality
 * <p>
 * This relies entirely on data present in {@link AnimatableManager#getAnimatableData} saved to this manager to determine context
 */
public abstract class ContextAwareAnimatableManager<T extends GeoAnimatable, C> extends AnimatableManager<T> {
	private final Map<C, AnimatableManager<T>> managers;

    @ApiStatus.Internal
	public ContextAwareAnimatableManager(GeoAnimatable animatable) {
		super(animatable);

		this.managers = buildContextOptions(animatable);
	}

	/**
	 * Build the context-manager map for this manager
	 * <p>
	 * The resulting map <u>MUST</u> contain all possible contexts.
	 */
	protected abstract Map<C, AnimatableManager<T>> buildContextOptions(GeoAnimatable animatable);

	/**
	 * Get the current context for the manager; to determine which submanager to retrieve
	 */
	public abstract C getCurrentContext();

	/**
	 * Get the AnimatableManager for the given context
	 */
	public AnimatableManager<T> getManagerForContext(C context) {
		return this.managers.get(context);
	}

    /**
     * Get the controller map for this animatable's manager
     */
    public Map<String, AnimationController<T>> getAnimationControllers() {
        return getManagerForContext(getCurrentContext()).getAnimationControllers();
    }

	/**
	 * Add an {@link AnimationController} to this animatable's manager
	 * <p>
	 * You probably should have added it during {@link GeoAnimatable#registerControllers} instead
	 */
	public void addController(AnimationController<T> controller) {
		getManagerForContext(getCurrentContext()).addController(controller);
	}

	/**
	 * Removes an {@link AnimationController} from this manager by the given name, if present.
	 */
	public void removeController(String name) {
		getManagerForContext(getCurrentContext()).removeController(name);
	}

	/**
	 * Attempt to trigger an animation from a given controller name and registered triggerable animation name
	 * <p>
	 * This pseudo-overloaded method checks each controller in turn until one of them accepts the trigger
	 * <p>
	 * This can be sped up by specifying which controller you intend to receive the trigger in {@link AnimatableManager#tryTriggerAnimation(String, String)}
	 *
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	public void tryTriggerAnimation(String animName) {
		for (AnimatableManager<T> manager : this.managers.values()) {
			manager.tryTriggerAnimation(animName);
		}
	}

	/**
	 * Attempt to trigger an animation from a given controller name and registered triggerable animation name
	 *
	 * @param controllerName The name of the controller name the animation belongs to
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	public void tryTriggerAnimation(String controllerName, String animName) {
		for (AnimatableManager<T> manager : this.managers.values()) {
			manager.tryTriggerAnimation(controllerName, animName);
		}
	}

	/**
	 * Stop a triggered animation, or all triggered animations, depending on the current state of animations and the passed argument
	 *
	 * @param animName The trigger name of the animation to stop, or null to stop any triggered animation
	 */
	public void stopTriggeredAnimation(@Nullable String animName) {
		for (AnimatableManager<T> manager : this.managers.values()) {
			manager.stopTriggeredAnimation(animName);
		}
	}

	/**
	 * Stop a triggered animation or all triggered animations on a given controller, depending on the current state of animations and the passed arguments
	 *
	 * @param controllerName The name of the controller the triggered animation belongs to
	 * @param animName The trigger name of the animation to stop, or null to stop any triggered animation
	 */
	public void stopTriggeredAnimation(String controllerName, @Nullable String animName) {
		for (AnimatableManager<T> manager : this.managers.values()) {
			manager.stopTriggeredAnimation(controllerName, animName);
		}
	}

    /**
     * Tell this AnimatableManager instance that it was used to render its Animatable at the given tick
     * <p>
     * Marks all contexts equally, for consistent rendering/animating
     */
    @Override
    public void markRenderedAt(double animatableTick) {
        super.markRenderedAt(animatableTick);

        for (AnimatableManager<T> manager : this.managers.values()) {
            manager.markRenderedAt(animatableTick);
        }
    }

    /**
	 * Set a custom data point to be used later
	 * <p>
	 * Submanagers do not have their data set, and instead it is all kept in this parent manager
	 *
	 * @param dataTicket The DataTicket for the data point
	 * @param data The piece of data to store
	 */
	public <D> void setAnimatableData(DataTicket<D> dataTicket, D data) {
		super.setAnimatableData(dataTicket, data);
	}

	/**
	 * Retrieve a custom data point that was stored earlier, or null if it hasn't been stored
	 * <p>
	 * Submanagers do not have their data set, and instead it is all kept in this parent manager
	 */
	public <D> D getAnimatableData(DataTicket<D> dataTicket) {
		return super.getAnimatableData(dataTicket);
	}
}
