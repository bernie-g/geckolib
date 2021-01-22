/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import software.bernie.geckolib3.core.processor.IBone;

public class BoneAnimationQueue
{
	public final IBone bone;
	public AnimationPointQueue rotationXQueue = new AnimationPointQueue();
	public AnimationPointQueue rotationYQueue = new AnimationPointQueue();
	public AnimationPointQueue rotationZQueue = new AnimationPointQueue();
	public AnimationPointQueue positionXQueue = new AnimationPointQueue();
	public AnimationPointQueue positionYQueue = new AnimationPointQueue();
	public AnimationPointQueue positionZQueue = new AnimationPointQueue();
	public AnimationPointQueue scaleXQueue = new AnimationPointQueue();
	public AnimationPointQueue scaleYQueue = new AnimationPointQueue();
	public AnimationPointQueue scaleZQueue = new AnimationPointQueue();

	public BoneAnimationQueue(IBone bone)
	{
		this.bone = bone;
	}
}
