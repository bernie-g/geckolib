package software.bernie.geckolib.animation;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.constant.dataticket.DataTicket;

import java.util.Map;

/**
 * Context-aware wrapper for {@link AnimatableManager}
 * <p>
 * This can be used for things like perspective-dependent animation handling and other similar functionality
 * <p>
 * This relies entirely on data present in {@link AnimatableManager#extraData} saved to this manager to determine context
 */
public abstract class ContextAwareAnimatableManager<T extends GeoAnimatable, C> extends AnimatableManager<T> {
	private final Map<C, AnimatableManager<T>> managers;

	/**
	 * Instantiates a new AnimatableManager for the given animatable, calling {@link GeoAnimatable#registerControllers} to define its controllers
	 *
	 * @param animatable
	 */
	public ContextAwareAnimatableManager(GeoAnimatable animatable) {
		super(animatable);

		this.managers = buildContextOptions(animatable);
	}

	/**
	 * Build the context-manager map for this manager
	 * <p>
	 * The resulting map <u>MUST</u> contain all possible contexts.
	 *
	 * @param animatable
	 */
	protected abstract Map<C, AnimatableManager<T>> buildContextOptions(GeoAnimatable animatable);

	/**
	 * Get the current context for the manager, to determine which sub-manager to retrieve
	 */
	public abstract C getCurrentContext();

	/**
	 * Get the AnimatableManager for the given context
	 */
	public AnimatableManager<T> getManagerForContext(C context) {
		return this.managers.get(context);
	}

	/**
	 * Add an {@link AnimationController} to this animatable's manager
	 * <p>
	 * Generally speaking you probably should have added it during {@link GeoAnimatable#registerControllers}
	 */
	public void addController(AnimationController controller) {
		getManagerForContext(getCurrentContext()).addController(controller);
	}

	/**
	 * Removes an {@link AnimationController} from this manager by the given name, if present.
	 */
	public void removeController(String name) {
		getManagerForContext(getCurrentContext()).removeController(name);
	}

	public Map<String, AnimationController<T>> getAnimationControllers() {
		return getManagerForContext(getCurrentContext()).getAnimationControllers();
	}

	public Map<String, BoneSnapshot> getBoneSnapshotCollection() {
		return getManagerForContext(getCurrentContext()).getBoneSnapshotCollection();
	}

	public void clearSnapshotCache() {
		getManagerForContext(getCurrentContext()).clearSnapshotCache();
	}

	public double getLastUpdateTime() {
		return getManagerForContext(getCurrentContext()).getLastUpdateTime();
	}

	public void updatedAt(double updateTime) {
		getManagerForContext(getCurrentContext()).updatedAt(updateTime);
	}

	public double getFirstTickTime() {
		return getManagerForContext(getCurrentContext()).getFirstTickTime();
	}

	public void startedAt(double time) {
		getManagerForContext(getCurrentContext()).startedAt(time);
	}

	public boolean isFirstTick() {
		return getManagerForContext(getCurrentContext()).isFirstTick();
	}

	protected void finishFirstTick() {
		getManagerForContext(getCurrentContext()).finishFirstTick();
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
	 * Set a custom data point to be used later
	 * <p>
	 * Sub-managers do not have their data set, and instead it is all kept in this parent manager
	 *
	 * @param dataTicket The DataTicket for the data point
	 * @param data The piece of data to store
	 */
	public <D> void setData(DataTicket<D> dataTicket, D data) {
		super.setData(dataTicket, data);
	}

	/**
	 * Retrieve a custom data point that was stored earlier, or null if it hasn't been stored
	 * <p>
	 * Sub-managers do not have their data set, and instead it is all kept in this parent manager
	 */
	public <D> D getData(DataTicket<D> dataTicket) {
		return super.getData(dataTicket);
	}
}
