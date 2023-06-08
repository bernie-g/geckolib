package software.bernie.geckolib.core.animation;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.core.state.BoneSnapshot;

import java.util.Map;

/**
 * Context-aware wrapper for {@link AnimatableManager}.<br>
 * This can be used for things like perspective-dependent animation handling and other similar functionality.<br>
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
	 * Build the context-manager map for this manager.<br>
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
	 * Add an {@link AnimationController} to this animatable's manager.<br>
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
	 * Attempt to trigger an animation from a given controller name and registered triggerable animation name.<br>
	 * This pseudo-overloaded method checks each controller in turn until one of them accepts the trigger.<br>
	 * This can be sped up by specifying which controller you intend to receive the trigger in {@link AnimatableManager#tryTriggerAnimation(String, String)}
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link software.bernie.geckolib.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	public void tryTriggerAnimation(String animName) {
		for (AnimatableManager<T> manager : this.managers.values()) {
			manager.tryTriggerAnimation(animName);
		}
	}

	/**
	 * Attempt to trigger an animation from a given controller name and registered triggerable animation name
	 * @param controllerName The name of the controller name the animation belongs to
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link software.bernie.geckolib.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	public void tryTriggerAnimation(String controllerName, String animName) {
		for (AnimatableManager<T> manager : this.managers.values()) {
			manager.tryTriggerAnimation(controllerName, animName);
		}
	}

	/**
	 * Set a custom data point to be used later.<br>
	 * Sub-managers do not have their data set, and instead it is all kept in this parent manager
	 * @param dataTicket The DataTicket for the data point
	 * @param data The piece of data to store
	 */
	public <D> void setData(DataTicket<D> dataTicket, D data) {
		super.setData(dataTicket, data);
	}

	/**
	 * Retrieve a custom data point that was stored earlier, or null if it hasn't been stored.<br>
	 * Sub-managers do not have their data set, and instead it is all kept in this parent manager
	 */
	public <D> D getData(DataTicket<D> dataTicket) {
		return super.getData(dataTicket);
	}
}
