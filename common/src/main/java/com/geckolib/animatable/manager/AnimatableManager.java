package com.geckolib.animatable.manager;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.AnimationController;
import com.geckolib.constant.dataticket.DataTicket;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/// The animation data collection for a given animatable instance
///
/// Typically, a single working-instance of a [GeoAnimatable]
/// will have a single instance of `AnimatableManager` associated with it
///
/// @param <T> Animatable class type
public class AnimatableManager<T extends GeoAnimatable> {
	protected final Map<String, AnimationController<T>> animationControllers;
	protected final Supplier<Map<DataTicket<?>, Object>> animatableInstanceData = Suppliers.memoize(Reference2ObjectOpenHashMap::new);
    protected double firstRenderTick = 0;

	/// Instantiates a new AnimatableManager for the given animatable, calling [GeoAnimatable#registerControllers] to define its controllers
	public AnimatableManager(GeoAnimatable animatable) {
		ControllerRegistrar registrar = new ControllerRegistrar(new ObjectArrayList<>(1));

		animatable.registerControllers(registrar);

		this.animationControllers = registrar.build();
	}

    /// Get the controller map for this animatable's manager
    public Map<String, AnimationController<T>> getAnimationControllers() {
        return this.animationControllers;
    }

	/// Add an [AnimationController] to this animatable's manager
	///
	/// Generally speaking, you probably should have added it during [GeoAnimatable#registerControllers]
	public void addController(AnimationController<T> controller) {
		getAnimationControllers().put(controller.getName(), controller);
	}

	/// Removes an [AnimationController] from this manager by the given name, if present.
	public void removeController(String name) {
		getAnimationControllers().remove(name);
	}

	/// Set a custom data point to be used later
	///
	/// @param dataTicket The DataTicket for the data point
	/// @param data The piece of data to store
	public <D> void setAnimatableData(DataTicket<D> dataTicket, D data) {
		this.animatableInstanceData.get().put(dataTicket, data);
	}

	/// Retrieve a custom data point that was stored earlier, or null if it hasn't been stored
	@SuppressWarnings("unchecked")
    public <D> @Nullable D getAnimatableData(DataTicket<D> dataTicket) {
		return (D)this.animatableInstanceData.get().get(dataTicket);
	}

	/// Attempt to trigger an animation from a given controller name and registered triggerable animation name
	///
	/// This pseudo-overloaded method checks each controller in turn until one of them accepts the trigger
	///
	/// This can be sped up by specifying which controller you intend to receive the trigger in [AnimatableManager#tryTriggerAnimation(String, String)]
	///
	/// @param animName The name of animation to trigger. This needs to have been registered with the controller via [AnimationController.triggerableAnim][AnimationController#triggerableAnim]
	public void tryTriggerAnimation(String animName) {
		for (AnimationController<?> controller : getAnimationControllers().values()) {
			if (controller.triggerAnimation(animName))
				return;
		}
	}

	/// Attempt to trigger an animation from a given controller name and registered triggerable animation name
	///
	/// @param controllerName The name of the controller the animation belongs to
	/// @param animName The name of animation to trigger. This needs to have been registered with the controller via [AnimationController.triggerableAnim][AnimationController#triggerableAnim]
	public void tryTriggerAnimation(String controllerName, String animName) {
		AnimationController<?> controller = getAnimationControllers().get(controllerName);

		if (controller != null)
			controller.triggerAnimation(animName);
	}

	/// Stop a triggered animation, or all triggered animations, depending on the current state of animations and the passed argument
	///
	/// @param animName The trigger name of the animation to stop, or null to stop any triggered animation
	public void stopTriggeredAnimation(@Nullable String animName) {
		for (AnimationController<?> controller : getAnimationControllers().values()) {
			if ((animName == null || controller.isTriggeredAnimation(animName)) && controller.stopTriggeredAnimation())
				return;
		}
	}

	/// Stop a triggered animation or all triggered animations on a given controller, depending on the current state of animations and the passed arguments
	///
	/// @param controllerName The name of the controller the triggered animation belongs to
	/// @param animName The trigger name of the animation to stop, or null to stop any triggered animation
	public void stopTriggeredAnimation(String controllerName, @Nullable String animName) {
		AnimationController<?> controller = getAnimationControllers().get(controllerName);

		if (controller != null && (animName == null || controller.isTriggeredAnimation(animName)))
			controller.stopTriggeredAnimation();
	}

    /// Tell this AnimatableManager instance that it was used to render its Animatable at the given tick
    public void markRenderedAt(double animatableTick) {
        if (this.firstRenderTick > animatableTick)
            this.firstRenderTick = animatableTick;
    }

    /// Get the first time this animatable was used for rendering, in terms of its own age
    public double getFirstRenderTick() {
        return this.firstRenderTick;
    }

	/// Helper class for the AnimatableManager to cleanly register controllers in one shot at instantiation for efficiency
	///
	/// @param controllers The registered controllers collection for this registrar
	@SuppressWarnings("unchecked")
    public record ControllerRegistrar(List<AnimationController<? extends GeoAnimatable>> controllers) {
		/// Add multiple [AnimationController]s to this registrar
		public ControllerRegistrar add(AnimationController<?>... controllers) {
			controllers().addAll(Arrays.asList(controllers));

			return this;
		}

		/// Add an [AnimationController] to this registrar
		public ControllerRegistrar add(AnimationController<?> controller) {
			controllers().add(controller);

			return this;
		}

		/// Remove an [AnimationController] from this registrar by name
		///
		/// This is mostly only useful if you're subclassing an existing animatable object and want to modify the super list
		public ControllerRegistrar remove(String name) {
			controllers().removeIf(controller -> controller.getName().equals(name));

			return this;
		}

		@SuppressWarnings("rawtypes")
        @ApiStatus.Internal
		private <T extends GeoAnimatable> Map<String, AnimationController<T>> build() {
			Object2ObjectArrayMap<String, AnimationController<?>> map = new Object2ObjectArrayMap<>(controllers().size());

			controllers().forEach(controller -> map.put(controller.getName(), controller));

			return (Map)map;
		}
	}
}
