package software.bernie.geckolib.block;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.easing.EasingType;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.event.predicate.SpecialAnimationPredicate;
import software.bernie.geckolib.geo.render.GeoBlockRenderer;
import software.bernie.geckolib.model.IAnimatableModel;
import software.bernie.geckolib.item.AnimatedItemRenderer;
import software.bernie.geckolib.item.armor.AnimatedArmorItem;
import software.bernie.geckolib.util.AnimationUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpecialAnimationController<T extends IAnimatable> extends AnimationController<T>
{
	private static List<Function<Object, IAnimatableModel>> modelFetchers = new ArrayList<>();

	public static void addModelFetcher(Function<Object, IAnimatableModel> fetcher)
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
					return (IAnimatableModel)((GeoBlockRenderer<?>) renderer).getGeoModelProvider();
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
				return (IAnimatableModel) AnimationUtils.getGeoModelForEntity((Entity)object);
			}
			return null;
		});
	}


	/**
	 * The animation predicate, is tested in every process call (i.e. every frame)
	 */
	private ITileAnimationPredicate<T> animationPredicate;

	/**
	 * An AnimationPredicate is run every render frame for ever AnimationController. The "test" method is where you should change animations, stop animations, restart, etc.
	 */
	@FunctionalInterface
	public interface ITileAnimationPredicate<E extends IAnimatable>
	{
		<E extends IAnimatable> boolean test(SpecialAnimationPredicate<E> event);
	}


	public SpecialAnimationController(T entity, String name, float transitionLengthTicks, ITileAnimationPredicate<T> animationPredicate)
	{
		super(entity, name, transitionLengthTicks);
		this.animationPredicate = animationPredicate;
		this.soundPlayer = this::playSound;
	}

	public SpecialAnimationController(T entity, String name, float transitionLengthTicks, ITileAnimationPredicate<T> animationPredicate, EasingType easingtype)
	{
		super(entity, name, transitionLengthTicks, easingtype);
		this.animationPredicate = animationPredicate;
	}

	public SpecialAnimationController(T entity, String name, float transitionLengthTicks, ITileAnimationPredicate<T> animationPredicate, Function<Double, Double> customEasingMethod)
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
		GeckoLib.LOGGER.error("Could not find suitable model for entity of type {}. Did you register a Model Fetcher?", animatable.getClass());
		return null;
	}

	@Override
	protected boolean testAnimationPredicate(AnimationTestPredicate<T> event)
	{
		return this.animationPredicate.test((SpecialAnimationPredicate<T>) event);
	}

	public void playSound(SoundEvent event)
	{
		//TODO add code for tile entity sounds
		//entity.world.playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), event, soundCategory, volume, pitch, distanceSoundDelay);
	}

}
