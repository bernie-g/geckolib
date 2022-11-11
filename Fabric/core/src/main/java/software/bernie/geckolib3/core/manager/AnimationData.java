/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.manager;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AnimationData {
	private Map<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection;
	private Map<String, AnimationController> animationControllers = new Object2ObjectOpenHashMap<>();
	public double tick;
	public boolean isFirstTick = true;
	private double resetTickLength = 1;
	public double startTick = -1;
	public Object ticker;
	public boolean shouldPlayWhilePaused = false;

	/**
	 * Instantiates a new Animation controller collection.
	 */
	public AnimationData() {
		super();
		boneSnapshotCollection = new Object2ObjectOpenHashMap<>();
	}

	/**
	 * This method is how you register animation controllers, without this, your
	 * AnimationPredicate method will never be called
	 *
	 * @param value The value
	 * @return the animation controller
	 */
	public AnimationController addAnimationController(AnimationController value) {
		return this.animationControllers.put(value.getName(), value);
	}

	public Map<String, Pair<IBone, BoneSnapshot>> getBoneSnapshotCollection() {
		return boneSnapshotCollection;
	}

	public void setBoneSnapshotCollection(HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection) {
		this.boneSnapshotCollection = boneSnapshotCollection;
	}

	public void clearSnapshotCache() {
		this.boneSnapshotCollection = new HashMap<>();
	}

	public double getResetSpeed() {
		return resetTickLength;
	}

	/**
	 * This is how long it takes for any bones that don't have an animation to
	 * revert back to their original position
	 *
	 * @param resetTickLength The amount of ticks it takes to reset. Cannot be
	 *                        negative.
	 */
	public void setResetSpeedInTicks(double resetTickLength) {
		this.resetTickLength = resetTickLength < 0 ? 0 : resetTickLength;
	}

	public Map<String, AnimationController> getAnimationControllers() {
		return animationControllers;
	}
}
