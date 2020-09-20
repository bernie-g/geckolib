package software.bernie.geckolib.core.controller;

import software.bernie.geckolib.core.AnimationState;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.builder.Animation;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.easing.EasingType;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.core.IAnimatableModel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnimationController<T extends IAnimatable> extends BaseAnimationController<T>
{
	private static List<Function<Object, IAnimatableModel>> modelFetchers = new ArrayList<>();

	public static void addModelFetcher(Function<Object, IAnimatableModel> fetcher)
	{
		modelFetchers.add(fetcher);
	}

	/**
	 * The animation predicate, is tested in every process call (i.e. every frame)
	 */
	private IAnimationPredicate<T> animationPredicate;



	public AnimationController(T entity, String name, float transitionLengthTicks, IAnimationPredicate<T> animationPredicate)
	{
		super(entity, name, transitionLengthTicks);
		this.animationPredicate = animationPredicate;
	}

	public AnimationController(T entity, String name, float transitionLengthTicks, IAnimationPredicate<T> animationPredicate, EasingType easingtype)
	{
		super(entity, name, transitionLengthTicks, easingtype);
		this.animationPredicate = animationPredicate;
	}

	public AnimationController(T entity, String name, float transitionLengthTicks, IAnimationPredicate<T> animationPredicate, Function<Double, Double> customEasingMethod)
	{
		super(entity, name, transitionLengthTicks, customEasingMethod);
		this.animationPredicate = animationPredicate;
	}

	/**
	 * This method sets the current animation with an animation builder. You can run this method every frame, if you pass in the same animation builder every time, it won't restart. Additionally, it smoothly transitions between animation states.
	 */
	public void setAnimation(@Nullable AnimationBuilder builder)
	{
		IAnimatableModel model = getModel(this.animatable);

		if (model != null)
		{
			if (builder == null || builder.getRawAnimationList().size() == 0)
			{
				animationState = AnimationState.Stopped;
			}
			else if (!builder.getRawAnimationList().equals(currentAnimationBuilder.getRawAnimationList()) || needsAnimationReload)
			{
				AtomicBoolean encounteredError = new AtomicBoolean(false);

				// Convert the list of animation names to the actual list, keeping track of the loop boolean along the way
				IAnimatableModel finalModel = model;
				LinkedList<Animation> animations = new LinkedList<>(
						builder.getRawAnimationList().stream().map((rawAnimation) ->
						{
							Animation animation = finalModel.getAnimation(rawAnimation.animationName, animatable);
							if (animation == null)
							{
								System.out.println(
										"Could not load animation: " + rawAnimation.animationName + ". Is it missing?");
								encounteredError.set(true);
							}
							if (animation != null && rawAnimation.loop != null)
							{
								animation.loop = rawAnimation.loop;
							}
							return animation;
						}).collect(Collectors.toList()));

				if (encounteredError.get())
				{
					return;
				}
				else
				{
					animationQueue = animations;
				}
				currentAnimationBuilder = builder;

				// Reset the adjusted tick to 0 on next animation process call
				shouldResetTick = true;
				this.animationState = AnimationState.Transitioning;
				justStartedTransition = true;
				needsAnimationReload = false;
			}
		}
	}

	private IAnimatableModel getModel(T animatable)
	{
		for (Function<Object, IAnimatableModel> modelGetter : modelFetchers)
		{
			IAnimatableModel model = modelGetter.apply(animatable);
			if(model != null)
			{
				return model;
			}
		}
		System.out.println(String.format("Could not find suitable model for entity of type %s. Did you register a Model Fetcher?", animatable.getClass()));
		return null;
	}

	@Override
	protected boolean testAnimationPredicate(AnimationTestPredicate<T> event)
	{
		return this.animationPredicate.test(event);
	}
}
