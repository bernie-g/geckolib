package software.bernie.geckolib.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.util.JSONException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.keyframe.BoneAnimation;
import software.bernie.geckolib.json.JSONAnimationUtils;
import software.bernie.geckolib.model.AnimatedEntityModel;
import software.bernie.geckolib.model.TransitionState;

import java.util.Random;
import java.util.function.Predicate;

public class AnimationController<T extends Entity>
{

	private T entity;
	public String name;
	private Animation animation;
	private Animation transitioningAnimation;
	public float transitionLength;
	public TransitionState transitionState = TransitionState.NotTransitioning;
//	public float transitionStartTick;
	private IAnimationPredicate animationPredicate;
	public float tickOffset = 0;

	@FunctionalInterface
	public interface IAnimationPredicate {
		<C extends Entity> boolean test(C entity, float limbSwing, float limbSwingAmount, float partialTick, TransitionState state, AnimationController controller);
	}

	public AnimationController(T entity, String name, float transitionLength, IAnimationPredicate animationPredicate)
	{
		this.entity = entity;
		this.name = name;
		this.transitionLength = transitionLength;
		this.animationPredicate = animationPredicate;
	}


	public Animation getAnimation()
	{
		return animation;
	}

	public void setAnimation(String animationName)
	{
		EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
		EntityRenderer<? super T> entityRenderer = renderManager.getRenderer(entity);
		if(entityRenderer instanceof IEntityRenderer)
		{
			LivingRenderer renderer = (LivingRenderer) entityRenderer;
			EntityModel entityModel = renderer.getEntityModel();
			if(entityModel instanceof AnimatedEntityModel)
			{
				Animation animation = null;
				AnimatedEntityModel model = (AnimatedEntityModel) entityModel;
				animation = model.getAnimation(animationName);
				if (this.animation == null)
				{
					this.animation = animation;
				}
				else if (transitioningAnimation == null)
				{
					replaceAnimation(model, animation);
				}
				else if (animation.animationName != transitioningAnimation.animationName)
				{
					replaceAnimation(model, animation);
				}
			}
		}
	}

	public Animation getTransitioningAnimation()
	{
		return transitioningAnimation;
	}


	public void replaceAnimation(AnimatedEntityModel entityModel, Animation animation)
	{
		this.transitioningAnimation = animation;
		this.transitionState = TransitionState.JustStarted;
		for(BoneAnimation boneAnimation : animation.boneAnimations)
		{
			entityModel.getBone(boneAnimation.boneName).transitionState = TransitionState.JustStarted;
		}
	}


	public void manuallyReplaceAnimation()
	{
		this.animation = this.transitioningAnimation;
	}

	public IAnimationPredicate getAnimationPredicate()
	{
		return animationPredicate;
	}

	public void replaceAnimationPredicate(IAnimationPredicate animationPredicate)
	{
		this.animationPredicate = animationPredicate;
	}
}