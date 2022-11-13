/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.object.DataTicket;
import software.bernie.geckolib3.core.state.BoneSnapshot;

import java.util.Map;

/**
 * The animation data collection for a given animatable instance.<br>
 * Generally speaking, a single working-instance of an {@link GeoAnimatable Animatable}
 * will have a single instance of {@code AnimatableManager} associated with it.<br>
 *
 */
public class AnimatableManager<T extends GeoAnimatable> {
	private final Map<String, BoneSnapshot> boneSnapshotCollection = new Object2ObjectOpenHashMap<>();
	private final Map<String, AnimationController<T>> animationControllers = new Object2ObjectOpenHashMap<>();
	private Map<DataTicket<?>, Object> extraData;

	private double lastUpdateTime;
	private boolean isFirstTick = true;
	private double firstTickTime = -1;

	/**
	 * Add an {@link AnimationController} to this animatable's data.<br>
	 * Only controllers added via this method will be acted upon.
	 */
	public void addAnimationController(AnimationController controller) {
		this.animationControllers.put(controller.getName(), controller);
	}

	public Map<String, AnimationController<T>> getAnimationControllers() {
		return animationControllers;
	}

	public Map<String, BoneSnapshot> getBoneSnapshotCollection() {
		return boneSnapshotCollection;
	}

	public void clearSnapshotCache() {
		this.boneSnapshotCollection.clear();
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

	protected boolean isFirstTick() {
		return this.isFirstTick;
	}

	protected void finishFirstTick() {
		this.isFirstTick = false;
	}

	/**
	 * Set a custom data point to be used later
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
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link software.bernie.geckolib3.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	public void tryTriggerAnimation(String controllerName, String animName) {
		if (controllerName != null) {
			AnimationController<?> controller = getAnimationControllers().get(controllerName);

			if (controller != null)
				controller.tryTriggerAnimation(animName);
		}
		else {
			for (AnimationController<?> controller : getAnimationControllers().values()) {
				if (controller.tryTriggerAnimation(animName))
					return;
			}
		}
	}
}
