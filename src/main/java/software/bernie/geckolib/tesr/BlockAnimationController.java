package software.bernie.geckolib.tesr;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.model.AnimatedBlockModel;
import software.bernie.geckolib.easing.EasingType;
import software.bernie.geckolib.event.AnimationTestPredicate;
import software.bernie.geckolib.event.TileAnimationPredicate;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockAnimationController<T extends TileEntity & ITileAnimatable> extends AnimationController<T>
{
	/**
	 * The animation predicate, is tested in every process call (i.e. every frame)
	 */
	private ITileAnimationPredicate<T> animationPredicate;

	/**
	 * An AnimationPredicate is run every render frame for ever AnimationController. The "test" method is where you should change animations, stop animations, restart, etc.
	 */
	@FunctionalInterface
	public interface ITileAnimationPredicate<E extends TileEntity & ITileAnimatable>
	{
		<E extends TileEntity & ITileAnimatable> boolean test(TileAnimationPredicate<E> event);
	}


	public BlockAnimationController(T entity, String name, float transitionLengthTicks, ITileAnimationPredicate<T> animationPredicate)
	{
		super(entity, name, transitionLengthTicks);
		this.animationPredicate = animationPredicate;
		this.soundPlayer = this::playSound;
	}

	public BlockAnimationController(T entity, String name, float transitionLengthTicks, ITileAnimationPredicate<T> animationPredicate, EasingType easingtype)
	{
		super(entity, name, transitionLengthTicks, easingtype);
		this.animationPredicate = animationPredicate;
	}

	public BlockAnimationController(T entity, String name, float transitionLengthTicks, ITileAnimationPredicate<T> animationPredicate, Function<Double, Double> customEasingMethod)
	{
		super(entity, name, transitionLengthTicks, customEasingMethod);
		this.animationPredicate = animationPredicate;
	}

	/**
	 * This method sets the current animation with an animation builder. You can run this method every frame, if you pass in the same animation builder every time, it won't restart. Additionally, it smoothly transitions between animation states.
	 */
	public void setAnimation(@Nullable AnimationBuilder builder)
	{
		AnimatedBlockRenderer<?, ?> renderer = (AnimatedBlockRenderer<?, ?>) TileEntityRendererDispatcher.instance.getRenderer(entity);
		AnimatedBlockModel model = renderer.getEntityModel();

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
				LinkedList<Animation> animations = new LinkedList<>(
						builder.getRawAnimationList().stream().map((rawAnimation) ->
						{
							Animation animation = model.getAnimation(rawAnimation.animationName);
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

	@Override
	protected boolean testAnimationPredicate(AnimationTestPredicate<T> event)
	{
		return this.animationPredicate.test((TileAnimationPredicate<T>) event);
	}

	public void playSound(SoundEvent event)
	{
		//TODO add code for tile entity sounds
		//entity.world.playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), event, soundCategory, volume, pitch, distanceSoundDelay);
	}

}
