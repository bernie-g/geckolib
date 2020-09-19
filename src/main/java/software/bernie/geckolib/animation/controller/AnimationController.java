package software.bernie.geckolib.animation.controller;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.renderers.legacy.AnimatedBlockRenderer;
import software.bernie.geckolib.animation.easing.EasingType;
import software.bernie.geckolib.animation.IAnimatable;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.renderers.geo.GeoBlockRenderer;
import software.bernie.geckolib.renderers.legacy.AnimatedItemRenderer;
import software.bernie.geckolib.item.AnimatedArmorItem;
import software.bernie.geckolib.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib.util.AnimationUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnimationController<T extends IAnimatable> extends BaseAnimationController<T>
{
	private static List<Function<Object, IAnimatableModelProvider>> modelFetchers = new ArrayList<>();

	public static void addModelFetcher(Function<Object, IAnimatableModelProvider> fetcher)
	{
		modelFetchers.add(fetcher);
	}

	static
	{
		addModelFetcher((Object object) ->
		{
			if (object instanceof Item)
			{
				Item item = (Item) object;
				ItemStackTileEntityRenderer renderer = item.getItemStackTileEntityRenderer();
				if (renderer instanceof AnimatedItemRenderer)
				{
					return ((AnimatedItemRenderer<?, ?>) renderer).getEntityModel();
				}
			}
			return null;
		});

		addModelFetcher((Object object) ->
		{
			if (object instanceof TileEntity)
			{
				TileEntity tile = (TileEntity) object;
				TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(tile);
				if (renderer instanceof AnimatedBlockRenderer)
				{
					AnimatedBlockRenderer<?, ?> animatedRenderer = (AnimatedBlockRenderer<?, ?>) renderer;
					return animatedRenderer.getEntityModel();
				}
				else if(renderer instanceof GeoBlockRenderer)
				{
					return (IAnimatableModelProvider)((GeoBlockRenderer<?>) renderer).getGeoModelProvider();
				}
			}
			return null;
		});

		addModelFetcher((Object object) ->
		{
			if (object instanceof AnimatedArmorItem)
			{
				AnimatedArmorItem armorItem = (AnimatedArmorItem) object;
				return armorItem.getModel();
			}
			return null;
		});

		addModelFetcher((Object object) ->
		{
			if (object instanceof Entity)
			{
				return (IAnimatableModelProvider) AnimationUtils.getGeoModelForEntity((Entity)object);
			}
			return null;
		});

		addModelFetcher((Object object) ->
		{
			if (object instanceof Entity)
			{
				return AnimationUtils.getModelForEntity((Entity)object);
			}
			return null;
		});
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
		IAnimatableModelProvider model = getModel(this.animatable);

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
				IAnimatableModelProvider finalModel = model;
				LinkedList<Animation> animations = new LinkedList<>(
						builder.getRawAnimationList().stream().map((rawAnimation) ->
						{
							Animation animation = finalModel.getAnimation(rawAnimation.animationName, finalModel.getAnimationFileLocation(animatable));
							if (animation == null)
							{
								GeckoLib.LOGGER.error(
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

	private IAnimatableModelProvider getModel(T animatable)
	{
		for (Function<Object, IAnimatableModelProvider> modelGetter : modelFetchers)
		{
			IAnimatableModelProvider model = modelGetter.apply(animatable);
			if(model != null)
			{
				return model;
			}
		}
		GeckoLib.LOGGER.error("Could not find suitable model for entity of type {}. Did you register a Model Fetcher?", animatable.getClass());
		return null;
	}

	@Override
	protected boolean testAnimationPredicate(AnimationTestPredicate<T> event)
	{
		return this.animationPredicate.test(event);
	}
}
