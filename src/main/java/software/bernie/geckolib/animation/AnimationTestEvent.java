package software.bernie.geckolib.animation;

import net.minecraft.entity.Entity;
import software.bernie.geckolib.model.AnimationState;

public class AnimationTestEvent<T extends Entity>
{
	private final T entity;
	private final double animationTick;
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float partialTick;
	private final AnimationController controller;

	public AnimationTestEvent(T entity, double animationTick, float limbSwing, float limbSwingAmount, float partialTick, AnimationController controller)
	{
		this.entity = entity;
		this.animationTick = animationTick;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.partialTick = partialTick;
		this.controller = controller;
	}

	public double getAnimationTick()
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

	public AnimationController getController()
	{
		return controller;
	}
}
