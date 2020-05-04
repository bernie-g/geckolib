package software.bernie.geckolib.animation;

import software.bernie.geckolib.model.AnimatedModelRenderer;
import software.bernie.geckolib.model.BoneSnapshot;
import software.bernie.geckolib.model.TransitionState;

import java.util.List;

public class AnimationCategory
{
	public String name;
	private Animation animation;
	private Animation transitioningAnimation;
	public float transitionSpeed;
	public TransitionState transitionState = TransitionState.NotTransitioning;
	public float transitionStartTick;


	public AnimationCategory(String name, float transitionSpeed)
	{
		this.name = name;
		this.transitionSpeed = transitionSpeed;
	}


	public Animation getAnimation()
	{
		return animation;
	}

	public void setAnimation(Animation animation)
	{
		this.animation = animation;
	}

	public Animation getTransitioningAnimation()
	{
		return transitioningAnimation;
	}

	public void setTransitioningAnimation(Animation transitioningAnimation)
	{
		this.transitioningAnimation = transitioningAnimation;
	}
}