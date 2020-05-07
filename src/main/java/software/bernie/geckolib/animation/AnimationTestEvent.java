package software.bernie.geckolib.animation;

import net.minecraft.entity.Entity;
import software.bernie.geckolib.model.TransitionState;

public class AnimationTestEvent<T extends Entity>
{
	private final T entity;
	private final float animationTick;
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float partialTick;
	private final TransitionState state;
	private final AnimationController controller;

	public AnimationTestEvent(T entity, float animationTick, float limbSwing, float limbSwingAmount, float partialTick, TransitionState state, AnimationController controller)
	{

		this.entity = entity;
		this.animationTick = animationTick;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.partialTick = partialTick;
		this.state = state;
		this.controller = controller;
	}

	public float getAnimationTick()
	{
		return animationTick;
	}

	public T getEntity()
	{
		return entity;
	}

	public float getLimbSwing()
	{
		return limbSwing;
	}

	public float getLimbSwingAmount()
	{
		return limbSwingAmount;
	}

	public float getPartialTick()
	{
		return partialTick;
	}

	public TransitionState getTransitionState()
	{
		return state;
	}

	public AnimationController getController()
	{
		return controller;
	}
}
