/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.manager;

import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;

import java.util.HashMap;

public class AnimationData {
    public float tick;
    public boolean isFirstTick = true;
    public Float startTick;
    private HashMap<IBone, BoneSnapshot> boneSnapshotCollection;
    private final HashMap<String, AnimationController> animationControllers = new HashMap<>();
    private double resetTickLength = 30;

    /**
     * Instantiates a new Animation controller collection.
     */
    public AnimationData() {
        super();
        boneSnapshotCollection = new HashMap<>();
    }

    /**
     * This method is how you register animation controllers, without this, your AnimationPredicate method will never be called
     *
     * @param value The value
     * @return the animation controller
     */
    public AnimationController addAnimationController(AnimationController value) {
        return this.animationControllers.put(value.getName(), value);
    }

    public HashMap<IBone, BoneSnapshot> getBoneSnapshotCollection() {
        return boneSnapshotCollection;
    }

    public void setBoneSnapshotCollection(HashMap<IBone, BoneSnapshot> boneSnapshotCollection) {
        this.boneSnapshotCollection = boneSnapshotCollection;
    }

    public void clearSnapshotCache() {
        this.boneSnapshotCollection = new HashMap<>();
    }

    public double getResetSpeed() {
        return resetTickLength;
    }

    /**
     * This is how long it takes for any bones that don't have an animation to revert back to their original position
     *
     * @param resetTickLength The amount of ticks it takes to reset. Cannot be negative.
     */
    public void setResetSpeedInTicks(double resetTickLength) {
        this.resetTickLength = resetTickLength < 0 ? 0 : resetTickLength;
    }

    public HashMap<String, AnimationController> getAnimationControllers() {
        return animationControllers;
    }
}
