/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.state;

import software.bernie.geckolib3.core.animatable.model.GeoBone;

public class BoneSnapshot {
	public final String name;
	private final GeoBone bone;

	public float scaleValueX;
	public float scaleValueY;
	public float scaleValueZ;

	public float positionOffsetX;
	public float positionOffsetY;
	public float positionOffsetZ;

	public float rotationValueX;
	public float rotationValueY;
	public float rotationValueZ;

	public double mostRecentResetRotationTick = 0;
	public double mostRecentResetPositionTick = 0;
	public double mostRecentResetScaleTick = 0;

	public boolean isCurrentlyRunningRotationAnimation = true;
	public boolean isCurrentlyRunningPositionAnimation = true;
	public boolean isCurrentlyRunningScaleAnimation = true;

	public BoneSnapshot(GeoBone bone) {
		this.rotationValueX = bone.getRotationX();
		this.rotationValueY = bone.getRotationY();
		this.rotationValueZ = bone.getRotationZ();

		this.positionOffsetX = bone.getPositionX();
		this.positionOffsetY = bone.getPositionY();
		this.positionOffsetZ = bone.getPositionZ();

		this.scaleValueX = bone.getScaleX();
		this.scaleValueY = bone.getScaleY();
		this.scaleValueZ = bone.getScaleZ();

		this.bone = bone;
		this.name = bone.getName();
	}

	public BoneSnapshot(BoneSnapshot snapshot) {
		this.scaleValueX = snapshot.scaleValueX;
		this.scaleValueY = snapshot.scaleValueY;
		this.scaleValueZ = snapshot.scaleValueZ;

		this.positionOffsetX = snapshot.positionOffsetX;
		this.positionOffsetY = snapshot.positionOffsetY;
		this.positionOffsetZ = snapshot.positionOffsetZ;

		this.rotationValueX = snapshot.rotationValueX;
		this.rotationValueY = snapshot.rotationValueY;
		this.rotationValueZ = snapshot.rotationValueZ;

		this.bone = snapshot.bone;
		this.name = snapshot.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof BoneSnapshot boneSnapshot))
			return false;

		return this.name.equals(boneSnapshot.name);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
