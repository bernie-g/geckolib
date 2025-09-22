package software.bernie.geckolib.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.constant.dataticket.DataTicket;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The animation data collection for a given animatable instance
 * <p>
 * Generally speaking, a single working-instance of an {@link GeoAnimatable Animatable}
 * will have a single instance of {@code AnimatableManager} associated with it
 */
public class AnimatableManager<T extends GeoAnimatable> {
	private final Map<String, BoneSnapshot> boneSnapshotCollection = new Object2ObjectOpenHashMap<>();
	private final Map<String, AnimationController<T>> animationControllers;
	private Map<DataTicket<?>, Object> extraData;

	private double lastUpdateTime;
	private boolean isFirstTick = true;
	private double firstTickTime = -1;

	/**
	 * Instantiates a new AnimatableManager for the given animatable, calling {@link GeoAnimatable#registerControllers} to define its controllers
	 */
	public AnimatableManager(GeoAnimatable animatable) {
		ControllerRegistrar registrar = new ControllerRegistrar(new ObjectArrayList<>(2));

		animatable.registerControllers(registrar);

		this.animationControllers = registrar.build();
	}

	/**
	 * Add an {@link AnimationController} to this animatable's manager
	 * <p>
	 * Generally speaking, you probably should have added it during {@link GeoAnimatable#registerControllers}
	 */
	public void addController(AnimationController controller) {
		getAnimationControllers().put(controller.getName(), controller);
	}

	/**
	 * Removes an {@link AnimationController} from this manager by the given name, if present.
	 */
	public void removeController(String name) {
		getAnimationControllers().remove(name);
	}

	public Map<String, AnimationController<T>> getAnimationControllers() {
		return this.animationControllers;
	}

	public Map<String, BoneSnapshot> getBoneSnapshotCollection() {
		return this.boneSnapshotCollection;
	}

	public void clearSnapshotCache() {
		getBoneSnapshotCollection().clear();
	}

	public double getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void updatedAt(double updateTime) {
		this.lastUpdateTime = updateTime;
	}

	public double getFirstTickTime() {
		return this.firstTickTime;
	}

	public void startedAt(double time) {
		this.firstTickTime = time;
	}

	public boolean isFirstTick() {
		return this.isFirstTick;
	}

	protected void finishFirstTick() {
		this.isFirstTick = false;
	}

	/**
	 * Set a custom data point to be used later
	 *
	 * @param dataTicket The DataTicket for the data point
	 * @param data The piece of data to store
	 */
	public <D> void setData(DataTicket<D> dataTicket, D data) {
		if (this.extraData == null)
			this.extraData = new Object2ObjectOpenHashMap<>();

		this.extraData.put(dataTicket, data);
	}

	/**
	 * Retrieve a custom data point that was stored earlier, or null if it hasn't been stored
	 */
	public <D> D getData(DataTicket<D> dataTicket) {
		return this.extraData != null ? dataTicket.getData(this.extraData) : null;
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
		for (AnimationController<?> controller : getAnimationControllers().values()) {
			if (controller.tryTriggerAnimation(animName))
				return;
		}
	}

	/**
	 * Attempt to trigger an animation from a given controller name and registered triggerable animation name
	 *
	 * @param controllerName The name of the controller the animation belongs to
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	public void tryTriggerAnimation(String controllerName, String animName) {
		AnimationController<?> controller = getAnimationControllers().get(controllerName);

		if (controller != null)
			controller.tryTriggerAnimation(animName);
	}

	/**
	 * Stop a triggered animation, or all triggered animations, depending on the current state of animations and the passed argument
	 *
	 * @param animName The trigger name of the animation to stop, or null to stop any triggered animation
	 */
	public void stopTriggeredAnimation(@Nullable String animName) {
		for (AnimationController<?> controller : getAnimationControllers().values()) {
			if ((animName == null || controller.triggerableAnimations.get(animName) == controller.getTriggeredAnimation()) && controller.stopTriggeredAnimation())
				return;
		}
	}

	/**
	 * Stop a triggered animation or all triggered animations on a given controller, depending on the current state of animations and the passed arguments
	 *
	 * @param controllerName The name of the controller the triggered animation belongs to
	 * @param animName The trigger name of the animation to stop, or null to stop any triggered animation
	 */
	public void stopTriggeredAnimation(String controllerName, @Nullable String animName) {
		AnimationController<?> controller = getAnimationControllers().get(controllerName);

		if (controller != null && (animName == null || controller.triggerableAnimations.get(animName) == controller.getTriggeredAnimation()))
			controller.stopTriggeredAnimation();
	}

	/**
	 * Helper class for the AnimatableManager to cleanly register controllers in one shot at instantiation for efficiency
	 */
	public record ControllerRegistrar(List<AnimationController<? extends GeoAnimatable>> controllers) {
		/**
		 * Add multiple {@link AnimationController}s to this registrar
		 */
		public ControllerRegistrar add(AnimationController<?>... controllers) {
			controllers().addAll(Arrays.asList(controllers));

			return this;
		}

		/**
		 * Add an {@link AnimationController} to this registrar
		 */
		public ControllerRegistrar add(AnimationController<?> controller) {
			controllers().add(controller);

			return this;
		}

		/**
		 * Remove an {@link AnimationController} from this registrar by name
		 * <p>
		 * This is mostly only useful if you're sub-classing an existing animatable object and want to modify the super list
		 */
		public ControllerRegistrar remove(String name) {
			controllers().removeIf(controller -> controller.getName().equals(name));

			return this;
		}

		@ApiStatus.Internal
		private <T extends GeoAnimatable> Object2ObjectArrayMap<String, AnimationController<T>> build() {
			Object2ObjectArrayMap<String, AnimationController<?>> map = new Object2ObjectArrayMap<>(controllers().size());

			controllers().forEach(controller -> map.put(controller.getName(), controller));

			return (Object2ObjectArrayMap)map;
		}
	}
}
