/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.model;

import net.minecraft.entity.Entity;
import org.antlr.v4.runtime.misc.NotNull;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.keyframe.*;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.animation.model.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * The type Animation controller.
 *
 * @param <T> the type parameter
 */
public class AnimationController<T extends Entity & IAnimatedEntity>
{
	/**
	 * The Entity.
	 */
	private T entity;

	/**
	 * The name of the animation controller
	 */
	private String name;

	private AnimationState animationState = AnimationState.Stopped;

	/**
	 * How long it takes to transition between animations
	 */
	public double transitionLength;

	/**
	 * The animation predicate, is tested in every process call (i.e. every frame)
	 */
	private IAnimationPredicate animationPredicate;

	/**
	 * An AnimationPredicate is run every render frame for ever AnimationController. The "test" method is where you should change animations, stop animations, restart, etc.
	 */
	public interface IAnimationPredicate
	{
		/**
		 * An AnimationPredicate is run every render frame for ever AnimationController. The "test" method is where you should change animations, stop animations, restart, etc.
		 *
		 * @return TRUE if the animation should continue, FALSE if it should stop.
		 */
		<ENTITY extends Entity> boolean test(AnimationTestEvent<ENTITY> event);
	}

	private final HashMap<String, BoneAnimationQueue> boneAnimationQueues = new HashMap<>();
	private double tickOffset = 0;
	private Queue<Animation> animationQueue = new LinkedList<>();
	private Animation currentAnimation;
	private AnimationBuilder currentAnimationBuilder = new AnimationBuilder();
	private boolean shouldResetTick = false;
	private HashMap<String, BoneSnapshot> boneSnapshots = new HashMap<>();
	private boolean justStopped = false;
	private boolean justStartedTransition = false;

	/**
	 * Instantiates a new Animation controller. Each animation controller can run one animation at a time. You can have several animation controllers for each entity, i.e. one animation to control the entity's size, one to control movement, attacks, etc.
	 *
	 * @param entity             The entity
	 * @param name               Name of the animation controller (move_controller, size_controller, attack_controller, etc.)
	 * @param transitionLength   How long it takes to transition between animations (IN TICKS!!)
	 * @param animationPredicate The animation predicate that decides if the animation should stop, continue, or keep running. You should switch animations in this method most of the time.
	 */
	public AnimationController(T entity, String name, float transitionLength, IAnimationPredicate animationPredicate)
	{
		this.entity = entity;
		this.name = name;
		this.transitionLength = transitionLength;
		this.animationPredicate = animationPredicate;
	}

	/**
	 * Gets the controller's name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the current animation. Can be null
	 *
	 * @return the current animation
	 */
	@Nullable
	public Animation getCurrentAnimation()
	{
		return currentAnimation;
	}

	/**
	 * Returns the current state of this animation controller.
	 */
	public AnimationState getAnimationState()
	{
		return animationState;
	}


	/**
	 * Gets the current animation's bone animation queues.
	 *
	 * @return the bone animation queues
	 */
	public HashMap<String, BoneAnimationQueue> getBoneAnimationQueues()
	{
		return boneAnimationQueues;
	}

	/**
	 * This method sets the current animation with an animation builder. You can run this method every frame, if you pass in the same animation builder every time, it won't restart. Additionally, it smoothly transitions between animation states.
	 */
	public void setAnimation(@Nullable AnimationBuilder builder)
	{
		AnimatedEntityModel model = AnimationUtils.getModelForEntity(entity);
		if (model != null)
		{
			if (builder == null || builder.getRawAnimationList().size() == 0)
			{
				animationState = AnimationState.Stopped;
			}
			if (!builder.getRawAnimationList().equals(currentAnimationBuilder.getRawAnimationList()))
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
							if (rawAnimation.loop != null)
							{
								animation.loop = rawAnimation.loop;
							}
							return animation;
						}).collect(Collectors.toList()));

				if(encounteredError.get())
				{
					return;
				}
				else {
					animationQueue = animations;
				}
				currentAnimationBuilder = builder;

				// Reset the adjusted tick to 0 on next animation process call
				shouldResetTick = true;
				this.animationState = AnimationState.Transitioning;
				justStartedTransition = true;
			}
		}
	}


	/**
	 * This method is called every frame in order to populate the animation point queues, and process animation state logic.
	 *
	 * @param tick                   The current tick + partial tick
	 * @param animationTestEvent     The animation test event
	 * @param modelRendererList      The list of all AnimatedModelRender's
	 * @param boneSnapshotCollection The bone snapshot collection
	 */
	public void process(double tick, AnimationTestEvent animationTestEvent, List<AnimatedModelRenderer> modelRendererList, BoneSnapshotCollection boneSnapshotCollection)
	{
		createInitialQueues(modelRendererList);

		double actualTick = tick;
		tick = adjustTick(tick);

		// Transition period has ended, reset the tick and set the animation to running
		if (getAnimationState() == AnimationState.Transitioning && tick >= transitionLength)
		{
			this.shouldResetTick = true;
			animationState = AnimationState.Running;
			tick = adjustTick(actualTick);
		}

		assert tick >= 0 : "GeckoLib: Tick was less than zero";

		// This tests the animation predicate
		boolean shouldStop = !this.animationPredicate.test(
				animationTestEvent);
		if (shouldStop || (currentAnimation == null && animationQueue.size() == 0))
		{
			// The animation should transition to the model's initial state
			animationState = AnimationState.Stopped;
			justStopped = true;
			return;
		}
		if (justStartedTransition && (shouldResetTick || justStopped))
		{
			justStopped = false;
			tick = adjustTick(actualTick);
		}
		else
		{
			if(getAnimationState() != AnimationState.Transitioning)
			{
				animationState = AnimationState.Running;
			}
		}
		// Handle transitioning to a different animation (or just starting one)
		if (getAnimationState() == AnimationState.Transitioning)
		{
			// Just started transitioning, so set the current animation to the first one
			if (tick == 0)
			{
				justStartedTransition = false;
				this.currentAnimation = animationQueue.poll();
				saveSnapshotsForAnimation(currentAnimation, boneSnapshotCollection);
			}
			for (BoneAnimation boneAnimation : currentAnimation.boneAnimations)
			{
				BoneAnimationQueue boneAnimationQueue = boneAnimationQueues.get(boneAnimation.boneName);
				BoneSnapshot boneSnapshot = this.boneSnapshots.get(boneAnimation.boneName);
				BoneSnapshot initialSnapshot = modelRendererList.stream().filter(x -> x.name.equals(boneAnimation.boneName)).findFirst().get().getInitialSnapshot();
				assert boneSnapshot != null : "Bone snapshot was null";

				VectorKeyFrameList<KeyFrame<Double>> rotationKeyFrames = boneAnimation.rotationKeyFrames;
				VectorKeyFrameList<KeyFrame<Double>> positionKeyFrames = boneAnimation.positionKeyFrames;
				VectorKeyFrameList<KeyFrame<Double>> scaleKeyFrames = boneAnimation.scaleKeyFrames;

				// Adding the initial positions of the upcoming animation, so the model transitions to the initial state of the new animation
				if (!rotationKeyFrames.xKeyFrames.isEmpty())
				{
					boneAnimationQueue.rotationXQueue.add(
							new AnimationPoint(tick, transitionLength, boneSnapshot.rotationValueX - initialSnapshot.rotationValueX,
									rotationKeyFrames.xKeyFrames.get(0).getStartValue()));
					boneAnimationQueue.rotationYQueue.add(
							new AnimationPoint(tick, transitionLength, boneSnapshot.rotationValueY - initialSnapshot.rotationValueY,
									rotationKeyFrames.yKeyFrames.get(0).getStartValue()));
					boneAnimationQueue.rotationZQueue.add(
							new AnimationPoint(tick, transitionLength, boneSnapshot.rotationValueZ - initialSnapshot.rotationValueZ,
									rotationKeyFrames.zKeyFrames.get(0).getStartValue()));
				}

				if (!positionKeyFrames.xKeyFrames.isEmpty())
				{
					boneAnimationQueue.positionXQueue.add(
							new AnimationPoint(tick, transitionLength, boneSnapshot.positionOffsetX,
									positionKeyFrames.xKeyFrames.get(0).getStartValue()));
					boneAnimationQueue.positionYQueue.add(
							new AnimationPoint(tick, transitionLength, boneSnapshot.positionOffsetY,
									positionKeyFrames.yKeyFrames.get(0).getStartValue()));
					boneAnimationQueue.positionZQueue.add(
							new AnimationPoint(tick, transitionLength, boneSnapshot.positionOffsetZ,
									positionKeyFrames.zKeyFrames.get(0).getStartValue()));
				}

				if (!scaleKeyFrames.xKeyFrames.isEmpty())
				{
					boneAnimationQueue.scaleXQueue.add(
							new AnimationPoint(tick, transitionLength, boneSnapshot.scaleValueX,
									scaleKeyFrames.xKeyFrames.get(0).getStartValue()));
					boneAnimationQueue.scaleYQueue.add(
							new AnimationPoint(tick, transitionLength, boneSnapshot.scaleValueY,
									scaleKeyFrames.yKeyFrames.get(0).getStartValue()));
					boneAnimationQueue.scaleZQueue.add(
							new AnimationPoint(tick, transitionLength, boneSnapshot.scaleValueZ,
									scaleKeyFrames.zKeyFrames.get(0).getStartValue()));
				}
			}
		}
		else if (getAnimationState() == AnimationState.Running)
		{
			// Actually run the animation
			processCurrentAnimation(tick, actualTick);
		}
	}

	// At the beginning of a new transition, save a snapshot of the model's rotation, position, and scale values as the initial value to lerp from
	private void saveSnapshotsForAnimation(@NotNull Animation animation, BoneSnapshotCollection boneSnapshotCollection)
	{
		for (BoneSnapshot snapshot : boneSnapshotCollection.values())
		{
			if(animation != null && animation.boneAnimations != null)
			{
				if (animation.boneAnimations.stream().anyMatch(x -> x.boneName.equals(snapshot.name)))
				{
					this.boneSnapshots.put(snapshot.name, new BoneSnapshot(snapshot));
				}
			}
		}
	}

	private void processCurrentAnimation(double tick, double actualTick)
	{
		assert currentAnimation != null;
		// Animation has ended
		if (tick >= currentAnimation.animationLength)
		{
			// If the current animation is set to loop, keep it as the current animation and just start over
			if (!currentAnimation.loop)
			{
				// Pull the next animation from the queue
				Animation peek = animationQueue.peek();
				if (peek == null)
				{
					// No more animations left, stop the animation controller
					this.animationState = AnimationState.Stopped;
					return;
				}
				else
				{
					// Otherwise, set the state to transitioning and start transitioning to the next animation next frame
					this.animationState = AnimationState.Transitioning;
					shouldResetTick = true;
				}
			}
			else {
				// Reset the adjusted tick so the next animation starts at tick 0
				shouldResetTick = true;
				tick = adjustTick(actualTick);
			}
		}

		// Loop through every boneanimation in the current animation and process the values
		List<BoneAnimation> boneAnimations = currentAnimation.boneAnimations;
		for (BoneAnimation boneAnimation : boneAnimations)
		{
			BoneAnimationQueue boneAnimationQueue = boneAnimationQueues.get(boneAnimation.boneName);

			VectorKeyFrameList<KeyFrame<Double>> rotationKeyFrames = boneAnimation.rotationKeyFrames;
			VectorKeyFrameList<KeyFrame<Double>> positionKeyFrames = boneAnimation.positionKeyFrames;
			VectorKeyFrameList<KeyFrame<Double>> scaleKeyFrames = boneAnimation.scaleKeyFrames;

			if (!rotationKeyFrames.xKeyFrames.isEmpty())
			{
				boneAnimationQueue.rotationXQueue.add(getAnimationPointAtTick(rotationKeyFrames.xKeyFrames, tick));
				boneAnimationQueue.rotationYQueue.add(getAnimationPointAtTick(rotationKeyFrames.yKeyFrames, tick));
				boneAnimationQueue.rotationZQueue.add(getAnimationPointAtTick(rotationKeyFrames.zKeyFrames, tick));
			}

			if (!positionKeyFrames.xKeyFrames.isEmpty())
			{
				boneAnimationQueue.positionXQueue.add(getAnimationPointAtTick(positionKeyFrames.xKeyFrames, tick));
				boneAnimationQueue.positionYQueue.add(getAnimationPointAtTick(positionKeyFrames.yKeyFrames, tick));
				boneAnimationQueue.positionZQueue.add(getAnimationPointAtTick(positionKeyFrames.zKeyFrames, tick));
			}

			if (!scaleKeyFrames.xKeyFrames.isEmpty())
			{
				boneAnimationQueue.scaleXQueue.add(getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, tick));
				boneAnimationQueue.scaleYQueue.add(getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, tick));
				boneAnimationQueue.scaleZQueue.add(getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, tick));
			}
		}
	}

	//Helper method to populate all the initial animation point queues
	private void createInitialQueues(List<AnimatedModelRenderer> modelRendererList)
	{
		if (boneAnimationQueues.size() == 0)
		{
			for (AnimatedModelRenderer modelRenderer : modelRendererList)
			{
				boneAnimationQueues.put(modelRenderer.name, new BoneAnimationQueue(modelRenderer));
			}
		}
	}

	// Used to reset the "tick" everytime a new animation starts, a transition starts, or something else of importance happens
	private double adjustTick(double tick)
	{
		if (shouldResetTick)
		{
			this.tickOffset = tick;
			shouldResetTick = false;
			return 0;
		}
		//assert tick - this.tickOffset >= 0;
		return (tick - this.tickOffset < 0 ? 0 : tick - this.tickOffset);
	}

	//Helper method to transform a KeyFrameLocation to an AnimationPoint
	private AnimationPoint getAnimationPointAtTick(List<KeyFrame<Double>> frames, double tick)
	{
		KeyFrameLocation<KeyFrame<Double>> location = getCurrentKeyFrameLocation(frames, tick);
		KeyFrame<Double> currentFrame = location.CurrentFrame;
		return new AnimationPoint(location.CurrentAnimationTick, currentFrame.getLength(), currentFrame.getStartValue(),
				currentFrame.getEndValue());
	}

	/*
	Returns the current keyframe object, plus how long the previous keyframes have taken (aka elapsed animation time)
    */
	private static KeyFrameLocation<KeyFrame<Double>> getCurrentKeyFrameLocation(List<KeyFrame<Double>> frames, double ageInTicks)
	{
		double totalTimeTracker = 0;
		for (int i = 0; i < frames.size(); i++)
		{
			KeyFrame frame = frames.get(i);
			totalTimeTracker += frame.getLength();
			if (totalTimeTracker >= ageInTicks)
			{
				double tick = (ageInTicks - (totalTimeTracker - frame.getLength()));
				return new KeyFrameLocation<>(frame, tick);
			}
		}
		return new KeyFrameLocation(frames.get(frames.size() - 1), ageInTicks);
	}
}