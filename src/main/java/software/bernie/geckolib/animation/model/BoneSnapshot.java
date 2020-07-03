/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.model;

public class BoneSnapshot
{
	public BoneSnapshot(AnimatedModelRenderer modelRenderer)
	{
		scaleValueX = modelRenderer.scaleValueX;
		scaleValueY = modelRenderer.scaleValueY;
		scaleValueZ = modelRenderer.scaleValueZ;

		positionOffsetX = modelRenderer.positionOffsetX;
		positionOffsetY = modelRenderer.positionOffsetY;
		positionOffsetZ = modelRenderer.positionOffsetZ;

		rotationValueX = modelRenderer.pitch;
		rotationValueY = modelRenderer.yaw;
		rotationValueZ = modelRenderer.roll;
		this.modelRenderer = modelRenderer;
		this.name = modelRenderer.name;
	}

	public BoneSnapshot(BoneSnapshot snapshot)
	{
		scaleValueX = snapshot.scaleValueX;
		scaleValueY = snapshot.scaleValueY;
		scaleValueZ = snapshot.scaleValueZ;

		positionOffsetX = snapshot.positionOffsetX;
		positionOffsetY = snapshot.positionOffsetY;
		positionOffsetZ = snapshot.positionOffsetZ;

		rotationValueX = snapshot.rotationValueX;
		rotationValueY = snapshot.rotationValueY;
		rotationValueZ = snapshot.rotationValueZ;
		this.modelRenderer = snapshot.modelRenderer;
		this.name = snapshot.name;
	}


	public String name;
	private AnimatedModelRenderer modelRenderer;

	public float scaleValueX;
	public float scaleValueY;
	public float scaleValueZ;

	public float positionOffsetX;
	public float positionOffsetY;
	public float positionOffsetZ;

	public float rotationValueX;
	public float rotationValueY;
	public float rotationValueZ;
}
