package software.bernie.geckolib.animation;

import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import software.bernie.geckolib.model.AnimatedEntityModel;
import software.bernie.geckolib.model.BoneSnapshotCollection;
import software.bernie.geckolib.model.TransitionState;

import java.util.*;

public class AnimationController<T extends Entity>
{
	private T entity;
	public String name;
	private Animation animation;
	private Animation transitioningAnimation;
	private float transitionLength;
	public TransitionState transitionState = TransitionState.NotTransitioning;
	private IAnimationPredicate animationPredicate;
	public float tickOffset = 0;
	public BoneSnapshotCollection modelRendererSnapshots;
	private boolean rotationEnabled;
	private boolean positionEnabled;
	private boolean scaleEnabled;
	private float speedModifier = 1;
	private Queue<Pair<String, Boolean>> animationQueue = new LinkedList<>();
	public boolean animationStoppedFlag = false;

	public void clearTransitioningAnimation()
	{
		this.transitioningAnimation = null;
	}


	public boolean isRotationAnimationEnabled()
	{
		return rotationEnabled;
	}

	public void enableRotationAnimation()
	{
		this.rotationEnabled = true;
	}
	public void disableRotationAnimation()
	{
		this.rotationEnabled = false;
	}

	public boolean isPositionAnimationEnabled()
	{
		return positionEnabled;
	}

	public void enablePositionAnimation()
	{
		this.positionEnabled = true;
	}

	public void disablePositionAnimation()
	{
		this.positionEnabled = false;
	}

	public boolean isScaleAnimationEnabled()
	{
		return scaleEnabled;
	}

	public void enableScaleAnimation()
	{
		this.scaleEnabled = true;
	}
	public void disableScaleAnimation()
	{
		this.scaleEnabled = false;
	}

	public float getSpeedModifier()
	{
		return speedModifier;
	}

	public void setSpeedModifier(float speedModifier)
	{
		this.speedModifier = speedModifier;
	}

	public Queue<Pair<String, Boolean>> getAnimationQueue()
	{
		return animationQueue;
	}


	/**
	 * Adds an animation to the queue.
	 *
	 * @param animation The name of the animation
	 * @param loop      If the animation should loop. You should probably only set this to true if it's the last animation you're adding to the queue
	 * @return The same object, so you can call multiple of these in the same statement.
	 */
	public AnimationController addAnimationToQueue(String animation, Boolean loop)
	{
		this.animationQueue.add(new Pair<>(animation, loop));
		return this;
	}

	public void clearAnimationQueue()
	{
		this.animationQueue.clear();
	}

	public float getTransitionSpeed()
	{
		return transitionLength;
	}

	public void setTransitionSpeed(float transitionLength)
	{
		this.transitionLength = transitionLength;
	}

	public interface IAnimationPredicate {
		<ENTITY extends Entity> boolean test(AnimationTestEvent<ENTITY> event);
	}

	public AnimationController(T entity, String name, float transitionLength, IAnimationPredicate animationPredicate)
	{
		this.entity = entity;
		this.name = name;
		this.transitionLength = transitionLength;
		this.animationPredicate = animationPredicate;
		modelRendererSnapshots = new BoneSnapshotCollection();
	}


	public Animation getAnimation()
	{
		return animation;
	}

	public void setAnimation(String animationName)
	{
		setAnimation(animationName, null);
	}

	public void setAnimation(String animationName, Boolean loop)
	{
		EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
		EntityRenderer<? super T> entityRenderer = renderManager.getRenderer(entity);
		if(entityRenderer instanceof IEntityRenderer)
		{
			LivingRenderer renderer = (LivingRenderer) entityRenderer;
			EntityModel entityModel = renderer.getEntityModel();
			if(entityModel instanceof AnimatedEntityModel)
			{
				Animation loadingAnimation = null;
				AnimatedEntityModel model = (AnimatedEntityModel) entityModel;
				loadingAnimation = model.getAnimation(animationName);
				if(loadingAnimation == null)
				{
					this.animation = null;
					return;
				}
				if(loop != null)
				{
					loadingAnimation.loop = loop;
				}
				if (this.animation == null)
				{
					this.animation = loadingAnimation;
				}
				else if(this.animation.animationName.equals(animationName))
				{
					return;
				}
				else if (transitioningAnimation == null || loadingAnimation.animationName != transitioningAnimation.animationName)
				{
					replaceAnimation(model, loadingAnimation);
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