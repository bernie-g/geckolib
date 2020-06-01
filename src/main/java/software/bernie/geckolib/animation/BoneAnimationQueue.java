package software.bernie.geckolib.animation;

import software.bernie.geckolib.model.AnimatedModelRenderer;

public class BoneAnimationQueue
{
	public final AnimatedModelRenderer bone;
	public AnimationPointQueue rotationXQueue = new AnimationPointQueue();
	public AnimationPointQueue rotationYQueue = new AnimationPointQueue();
	public AnimationPointQueue rotationZQueue = new AnimationPointQueue();
	public AnimationPointQueue positionXQueue = new AnimationPointQueue();
	public AnimationPointQueue positionYQueue = new AnimationPointQueue();
	public AnimationPointQueue positionZQueue = new AnimationPointQueue();
	public AnimationPointQueue scaleXQueue = new AnimationPointQueue();
	public AnimationPointQueue scaleYQueue = new AnimationPointQueue();
	public AnimationPointQueue scaleZQueue = new AnimationPointQueue();

	public BoneAnimationQueue(AnimatedModelRenderer bone)
	{
		this.bone = bone;
	}
}
