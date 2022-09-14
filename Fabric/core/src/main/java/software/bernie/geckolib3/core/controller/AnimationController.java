/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.eliotlash.mclib.math.IValue;
import com.eliotlash.molang.MolangParser;

import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.ConstantValue;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.AnimationPoint;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.BoneAnimationQueue;
import software.bernie.geckolib3.core.keyframe.EventKeyFrame;
import software.bernie.geckolib3.core.keyframe.KeyFrame;
import software.bernie.geckolib3.core.keyframe.KeyFrameLocation;
import software.bernie.geckolib3.core.keyframe.ParticleEventKeyFrame;
import software.bernie.geckolib3.core.keyframe.VectorKeyFrameList;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.core.util.Axis;

/**
 * The type Animation controller.
 *
 * @param <T> the type parameter
 */
public class AnimationController<T extends IAnimatable> {
	static List<ModelFetcher<?>> modelFetchers = new ArrayList<>();
	/**
	 * The Entity.
	 */
	protected T animatable;
	/**
	 * The animation predicate, is tested in every process call (i.e. every frame)
	 */
	protected IAnimationPredicate<T> animationPredicate;

	/**
	 * The name of the animation controller
	 */
	private final String name;

	protected AnimationState animationState = AnimationState.Stopped;

	/**
	 * How long it takes to transition between animations
	 */
	public double transitionLengthTicks;

	/**
	 * The sound listener is called every time a sound keyframe is encountered (i.e.
	 * every frame)
	 */
	private ISoundListener<T> soundListener;

	/**
	 * The particle listener is called every time a particle keyframe is encountered
	 * (i.e. every frame)
	 */
	private IParticleListener<T> particleListener;

	/**
	 * The custom instruction listener is called every time a custom instruction
	 * keyframe is encountered (i.e. every frame)
	 */
	private ICustomInstructionListener<T> customInstructionListener;

	public boolean isJustStarting = false;

	public static void addModelFetcher(ModelFetcher<?> fetcher) {
		modelFetchers.add(fetcher);
	}
	
	public static void removeModelFetcher(ModelFetcher<?> fetcher) {
		Objects.requireNonNull(fetcher);
		modelFetchers.remove(fetcher);
	}

	/**
	 * An AnimationPredicate is run every render frame for ever AnimationController.
	 * The "test" method is where you should change animations, stop animations,
	 * restart, etc.
	 */
	@FunctionalInterface
	public interface IAnimationPredicate<P extends IAnimatable> {
		/**
		 * An AnimationPredicate is run every render frame for ever AnimationController.
		 * The "test" method is where you should change animations, stop animations,
		 * restart, etc.
		 *
		 * @return CONTINUE if the animation should continue, STOP if it should stop.
		 */
		PlayState test(AnimationEvent<P> event);
	}

	/**
	 * Sound Listeners are run when a sound keyframe is hit. You can either return
	 * the SoundEvent and geckolib will play the sound for you, or return null and
	 * handle the sounds yourself.
	 */
	@FunctionalInterface
	public interface ISoundListener<A extends IAnimatable> {
		/**
		 * Sound Listeners are run when a sound keyframe is hit. You can either return
		 * the SoundEvent and geckolib will play the sound for you, or return null and
		 * handle the sounds yourself.
		 */
		void playSound(SoundKeyframeEvent<A> event);
	}

	/**
	 * Particle Listeners are run when a sound keyframe is hit. You need to handle
	 * the actual playing of the particle yourself.
	 */
	@FunctionalInterface
	public interface IParticleListener<A extends IAnimatable> {
		/**
		 * Particle Listeners are run when a sound keyframe is hit. You need to handle
		 * the actual playing of the particle yourself.
		 */
		void summonParticle(ParticleKeyFrameEvent<A> event);
	}

	/**
	 * Custom instructions can be added in blockbench by enabling animation effects
	 * in Animation - Animate Effects. You can then add custom instruction keyframes
	 * and use them as timecodes/events to handle in code.
	 */
	@FunctionalInterface
	public interface ICustomInstructionListener<A extends IAnimatable> {
		/**
		 * Custom instructions can be added in blockbench by enabling animation effects
		 * in Animation - Animate Effects. You can then add custom instruction keyframes
		 * and use them as timecodes/events to handle in code.
		 */
		void executeInstruction(CustomInstructionKeyframeEvent<A> event);
	}

	private final HashMap<String, BoneAnimationQueue> boneAnimationQueues = new HashMap<>();
	public double tickOffset;
	protected Queue<Animation> animationQueue = new LinkedList<>();
	protected Animation currentAnimation;
	protected AnimationBuilder currentAnimationBuilder = new AnimationBuilder();
	protected boolean shouldResetTick = false;
	private final HashMap<String, BoneSnapshot> boneSnapshots = new HashMap<>();
	private boolean justStopped = false;
	protected boolean justStartedTransition = false;
	public Function<Double, Double> customEasingMethod;
	protected boolean needsAnimationReload = false;
	public double animationSpeed = 1D;
	private final Set<EventKeyFrame<?>> executedKeyFrames = new HashSet<>();

	/**
	 * This method sets the current animation with an animation builder. You can run
	 * this method every frame, if you pass in the same animation builder every
	 * time, it won't restart. Additionally, it smoothly transitions between
	 * animation states.
	 */
	public void setAnimation(AnimationBuilder builder) {
		IAnimatableModel<T> model = getModel(this.animatable);
		if (model != null) {
			if (builder == null || builder.getRawAnimationList().size() == 0) {
				animationState = AnimationState.Stopped;
			} else if (!builder.getRawAnimationList().equals(currentAnimationBuilder.getRawAnimationList())
					|| needsAnimationReload) {
				AtomicBoolean encounteredError = new AtomicBoolean(false);
				// Convert the list of animation names to the actual list, keeping track of the
				// loop boolean along the way
				LinkedList<Animation> animations = builder.getRawAnimationList().stream().map((rawAnimation) -> {
					Animation animation = model.getAnimation(rawAnimation.animationName, animatable);
					if (animation == null) {
						System.out.printf("Could not load animation: %s. Is it missing?", rawAnimation.animationName);
						encounteredError.set(true);
					}
					if (animation != null && rawAnimation.loopType != null) {
						animation.loop = rawAnimation.loopType;
					}
					return animation;
				}).collect(Collectors.toCollection(LinkedList::new));

				if (encounteredError.get()) {
					return;
				} else {
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

	/**
	 * By default Geckolib uses the easing types of every keyframe. If you want to
	 * override that for an entire AnimationController, change this value.
	 */
	public EasingType easingType = EasingType.NONE;

	/**
	 * Instantiates a new Animation controller. Each animation controller can run
	 * one animation at a time. You can have several animation controllers for each
	 * entity, i.e. one animation to control the entity's size, one to control
	 * movement, attacks, etc.
	 *
	 * @param animatable            The entity
	 * @param name                  Name of the animation controller
	 *                              (move_controller, size_controller,
	 *                              attack_controller, etc.)
	 * @param transitionLengthTicks How long it takes to transition between
	 *                              animations (IN TICKS!!)
	 */
	public AnimationController(T animatable, String name, float transitionLengthTicks,
			IAnimationPredicate<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLengthTicks = transitionLengthTicks;
		this.animationPredicate = animationPredicate;
		tickOffset = 0.0d;
	}

	/**
	 * Instantiates a new Animation controller. Each animation controller can run
	 * one animation at a time. You can have several animation controllers for each
	 * entity, i.e. one animation to control the entity's size, one to control
	 * movement, attacks, etc.
	 *
	 * @param animatable            The entity
	 * @param name                  Name of the animation controller
	 *                              (move_controller, size_controller,
	 *                              attack_controller, etc.)
	 * @param transitionLengthTicks How long it takes to transition between
	 *                              animations (IN TICKS!!)
	 * @param easingtype            The method of easing to use. The other
	 *                              constructor defaults to no easing.
	 */
	public AnimationController(T animatable, String name, float transitionLengthTicks, EasingType easingtype,
			IAnimationPredicate<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLengthTicks = transitionLengthTicks;
		this.easingType = easingtype;
		this.animationPredicate = animationPredicate;
		tickOffset = 0.0d;
	}

	/**
	 * Instantiates a new Animation controller. Each animation controller can run
	 * one animation at a time. You can have several animation controllers for each
	 * entity, i.e. one animation to control the entity's size, one to control
	 * movement, attacks, etc.
	 *
	 * @param animatable            The entity
	 * @param name                  Name of the animation controller
	 *                              (move_controller, size_controller,
	 *                              attack_controller, etc.)
	 * @param transitionLengthTicks How long it takes to transition between
	 *                              animations (IN TICKS!!)
	 * @param customEasingMethod    If you want to use an easing method that's not
	 *                              included in the EasingType enum, pass your
	 *                              method into here. The parameter that's passed in
	 *                              will be a number between 0 and 1. Return a
	 *                              number also within 0 and 1. Take a look at
	 *                              {@link software.bernie.geckolib3.core.easing.EasingManager}
	 */
	public AnimationController(T animatable, String name, float transitionLengthTicks,
			Function<Double, Double> customEasingMethod, IAnimationPredicate<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLengthTicks = transitionLengthTicks;
		this.customEasingMethod = customEasingMethod;
		this.easingType = EasingType.CUSTOM;
		this.animationPredicate = animationPredicate;
		tickOffset = 0.0d;
	}

	/**
	 * Gets the controller's name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the current animation. Can be null
	 *
	 * @return the current animation
	 */

	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	/**
	 * Returns the current state of this animation controller.
	 */
	public AnimationState getAnimationState() {
		return animationState;
	}

	/**
	 * Gets the current animation's bone animation queues.
	 *
	 * @return the bone animation queues
	 */
	public HashMap<String, BoneAnimationQueue> getBoneAnimationQueues() {
		return boneAnimationQueues;
	}

	/**
	 * Registers a sound listener.
	 */
	public void registerSoundListener(ISoundListener<T> soundListener) {
		this.soundListener = soundListener;
	}

	/**
	 * Registers a particle listener.
	 */
	public void registerParticleListener(IParticleListener<T> particleListener) {
		this.particleListener = particleListener;
	}

	/**
	 * Registers a custom instruction listener.
	 */
	public void registerCustomInstructionListener(ICustomInstructionListener<T> customInstructionListener) {
		this.customInstructionListener = customInstructionListener;
	}

	/**
	 * This method is called every frame in order to populate the animation point
	 * queues, and process animation state logic.
	 *
	 * @param tick                   The current tick + partial tick
	 * @param event                  The animation test event
	 * @param modelRendererList      The list of all AnimatedModelRender's
	 * @param boneSnapshotCollection The bone snapshot collection
	 */
	public void process(double tick, AnimationEvent<T> event, List<IBone> modelRendererList,
			HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection, MolangParser parser,
			boolean crashWhenCantFindBone) {
		parser.setValue("query.life_time", tick / 20);
		if (currentAnimation != null) {
			IAnimatableModel<T> model = getModel(this.animatable);
			if (model != null) {
				Animation animation = model.getAnimation(currentAnimation.animationName, this.animatable);
				if (animation != null) {
					ILoopType loop = currentAnimation.loop;
					currentAnimation = animation;
					currentAnimation.loop = loop;
				}
			}
		}

		createInitialQueues(modelRendererList);

		double actualTick = tick;
		tick = adjustTick(tick);

		// Transition period has ended, reset the tick and set the animation to running
		if (animationState == AnimationState.Transitioning && tick >= transitionLengthTicks) {
			this.shouldResetTick = true;
			animationState = AnimationState.Running;
			tick = adjustTick(actualTick);
		}

		assert tick >= 0 : "GeckoLib: Tick was less than zero";

		// This tests the animation predicate
		PlayState playState = this.testAnimationPredicate(event);
		if (playState == PlayState.STOP || (currentAnimation == null && animationQueue.size() == 0)) {
			// The animation should transition to the model's initial state
			animationState = AnimationState.Stopped;
			justStopped = true;
			return;
		}
		if (justStartedTransition && (shouldResetTick || justStopped)) {
			justStopped = false;
			tick = adjustTick(actualTick);
		} else if (currentAnimation == null && this.animationQueue.size() != 0) {
			this.shouldResetTick = true;
			this.animationState = AnimationState.Transitioning;
			justStartedTransition = true;
			needsAnimationReload = false;
			tick = adjustTick(actualTick);
		} else {
			if (animationState != AnimationState.Transitioning) {
				animationState = AnimationState.Running;
			}
		}

		// Handle transitioning to a different animation (or just starting one)
		if (animationState == AnimationState.Transitioning) {
			// Just started transitioning, so set the current animation to the first one
			if (tick == 0 || isJustStarting) {
				justStartedTransition = false;
				this.currentAnimation = animationQueue.poll();
				resetEventKeyFrames();
				saveSnapshotsForAnimation(currentAnimation, boneSnapshotCollection);
			}
			if (currentAnimation != null) {
				setAnimTime(parser, 0);
				for (BoneAnimation boneAnimation : currentAnimation.boneAnimations) {
					BoneAnimationQueue boneAnimationQueue = boneAnimationQueues.get(boneAnimation.boneName);
					BoneSnapshot boneSnapshot = this.boneSnapshots.get(boneAnimation.boneName);
					Optional<IBone> first = modelRendererList.stream()
							.filter(x -> x.getName().equals(boneAnimation.boneName)).findFirst();
					if (!first.isPresent()) {
						if (crashWhenCantFindBone) {
							throw new RuntimeException("Could not find bone: " + boneAnimation.boneName);
						} else {
							continue;
						}
					}
					BoneSnapshot initialSnapshot = first.get().getInitialSnapshot();
					assert boneSnapshot != null : "Bone snapshot was null";

					VectorKeyFrameList<KeyFrame<IValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames;
					VectorKeyFrameList<KeyFrame<IValue>> positionKeyFrames = boneAnimation.positionKeyFrames;
					VectorKeyFrameList<KeyFrame<IValue>> scaleKeyFrames = boneAnimation.scaleKeyFrames;

					// Adding the initial positions of the upcoming animation, so the model
					// transitions to the initial state of the new animation
					if (!rotationKeyFrames.xKeyFrames.isEmpty()) {
						AnimationPoint xPoint = getAnimationPointAtTick(rotationKeyFrames.xKeyFrames, 0, true, Axis.X);
						AnimationPoint yPoint = getAnimationPointAtTick(rotationKeyFrames.yKeyFrames, 0, true, Axis.Y);
						AnimationPoint zPoint = getAnimationPointAtTick(rotationKeyFrames.zKeyFrames, 0, true, Axis.Z);
						boneAnimationQueue.rotationXQueue.add(new AnimationPoint(null, tick, transitionLengthTicks,
								boneSnapshot.rotationValueX - initialSnapshot.rotationValueX,
								xPoint.animationStartValue));
						boneAnimationQueue.rotationYQueue.add(new AnimationPoint(null, tick, transitionLengthTicks,
								boneSnapshot.rotationValueY - initialSnapshot.rotationValueY,
								yPoint.animationStartValue));
						boneAnimationQueue.rotationZQueue.add(new AnimationPoint(null, tick, transitionLengthTicks,
								boneSnapshot.rotationValueZ - initialSnapshot.rotationValueZ,
								zPoint.animationStartValue));
					}

					if (!positionKeyFrames.xKeyFrames.isEmpty()) {
						AnimationPoint xPoint = getAnimationPointAtTick(positionKeyFrames.xKeyFrames, 0, false, Axis.X);
						AnimationPoint yPoint = getAnimationPointAtTick(positionKeyFrames.yKeyFrames, 0, false, Axis.Y);
						AnimationPoint zPoint = getAnimationPointAtTick(positionKeyFrames.zKeyFrames, 0, false, Axis.Z);
						boneAnimationQueue.positionXQueue.add(new AnimationPoint(null, tick, transitionLengthTicks,
								boneSnapshot.positionOffsetX, xPoint.animationStartValue));
						boneAnimationQueue.positionYQueue.add(new AnimationPoint(null, tick, transitionLengthTicks,
								boneSnapshot.positionOffsetY, yPoint.animationStartValue));
						boneAnimationQueue.positionZQueue.add(new AnimationPoint(null, tick, transitionLengthTicks,
								boneSnapshot.positionOffsetZ, zPoint.animationStartValue));
					}

					if (!scaleKeyFrames.xKeyFrames.isEmpty()) {
						AnimationPoint xPoint = getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, 0, false, Axis.X);
						AnimationPoint yPoint = getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, 0, false, Axis.Y);
						AnimationPoint zPoint = getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, 0, false, Axis.Z);
						boneAnimationQueue.scaleXQueue.add(new AnimationPoint(null, tick, transitionLengthTicks,
								boneSnapshot.scaleValueX, xPoint.animationStartValue));
						boneAnimationQueue.scaleYQueue.add(new AnimationPoint(null, tick, transitionLengthTicks,
								boneSnapshot.scaleValueY, yPoint.animationStartValue));
						boneAnimationQueue.scaleZQueue.add(new AnimationPoint(null, tick, transitionLengthTicks,
								boneSnapshot.scaleValueZ, zPoint.animationStartValue));
					}
				}
			}
		} else if (getAnimationState() == AnimationState.Running) {
			// Actually run the animation
			processCurrentAnimation(tick, actualTick, parser, crashWhenCantFindBone);
		}
	}

	private void setAnimTime(MolangParser parser, double tick) {
		parser.setValue("query.anim_time", tick / 20);
	}

	private IAnimatableModel<T> getModel(T animatable) {
		for (ModelFetcher<?> modelFetcher : modelFetchers) {
			IAnimatableModel<T> model = (IAnimatableModel<T>) modelFetcher.apply(animatable);
			if (model != null) {
				return model;
			}
		}
		System.out.printf(
				"Could not find suitable model for animatable of type %s. Did you register a Model Fetcher?%n",
				animatable.getClass());
		return null;
	}

	protected PlayState testAnimationPredicate(AnimationEvent<T> event) {
		return this.animationPredicate.test(event);
	}

	// At the beginning of a new transition, save a snapshot of the model's
	// rotation, position, and scale values as the initial value to lerp from
	private void saveSnapshotsForAnimation(Animation animation,
			HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection) {
		for (Pair<IBone, BoneSnapshot> snapshot : boneSnapshotCollection.values()) {
			if (animation != null && animation.boneAnimations != null) {
				if (animation.boneAnimations.stream().anyMatch(x -> x.boneName.equals(snapshot.getLeft().getName()))) {
					this.boneSnapshots.put(snapshot.getLeft().getName(), new BoneSnapshot(snapshot.getRight()));
				}
			}
		}
	}

	private void processCurrentAnimation(double tick, double actualTick, MolangParser parser,
			boolean crashWhenCantFindBone) {
		assert currentAnimation != null;
		// Animation has ended
		if (tick >= currentAnimation.animationLength) {
			resetEventKeyFrames();
			// If the current animation is set to loop, keep it as the current animation and
			// just start over
			if (!currentAnimation.loop.isRepeatingAfterEnd()) {
				// Pull the next animation from the queue
				Animation peek = animationQueue.peek();
				if (peek == null) {
					// No more animations left, stop the animation controller
					this.animationState = AnimationState.Stopped;
					return;
				} else {
					// Otherwise, set the state to transitioning and start transitioning to the next
					// animation next frame
					this.animationState = AnimationState.Transitioning;
					shouldResetTick = true;
					currentAnimation = this.animationQueue.peek();
				}
			} else {
				// Reset the adjusted tick so the next animation starts at tick 0
				shouldResetTick = true;
				tick = adjustTick(actualTick);
			}
		}
		setAnimTime(parser, tick);

		// Loop through every boneanimation in the current animation and process the
		// values
		List<BoneAnimation> boneAnimations = currentAnimation.boneAnimations;
		for (BoneAnimation boneAnimation : boneAnimations) {
			BoneAnimationQueue boneAnimationQueue = boneAnimationQueues.get(boneAnimation.boneName);
			if (boneAnimationQueue == null) {
				if (crashWhenCantFindBone) {
					throw new RuntimeException("Could not find bone: " + boneAnimation.boneName);
				} else {
					continue;
				}
			}

			VectorKeyFrameList<KeyFrame<IValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames;
			VectorKeyFrameList<KeyFrame<IValue>> positionKeyFrames = boneAnimation.positionKeyFrames;
			VectorKeyFrameList<KeyFrame<IValue>> scaleKeyFrames = boneAnimation.scaleKeyFrames;

			if (!rotationKeyFrames.xKeyFrames.isEmpty()) {
				boneAnimationQueue.rotationXQueue
						.add(getAnimationPointAtTick(rotationKeyFrames.xKeyFrames, tick, true, Axis.X));
				boneAnimationQueue.rotationYQueue
						.add(getAnimationPointAtTick(rotationKeyFrames.yKeyFrames, tick, true, Axis.Y));
				boneAnimationQueue.rotationZQueue
						.add(getAnimationPointAtTick(rotationKeyFrames.zKeyFrames, tick, true, Axis.Z));
			}

			if (!positionKeyFrames.xKeyFrames.isEmpty()) {
				boneAnimationQueue.positionXQueue
						.add(getAnimationPointAtTick(positionKeyFrames.xKeyFrames, tick, false, Axis.X));
				boneAnimationQueue.positionYQueue
						.add(getAnimationPointAtTick(positionKeyFrames.yKeyFrames, tick, false, Axis.Y));
				boneAnimationQueue.positionZQueue
						.add(getAnimationPointAtTick(positionKeyFrames.zKeyFrames, tick, false, Axis.Z));
			}

			if (!scaleKeyFrames.xKeyFrames.isEmpty()) {
				boneAnimationQueue.scaleXQueue
						.add(getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, tick, false, Axis.X));
				boneAnimationQueue.scaleYQueue
						.add(getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, tick, false, Axis.Y));
				boneAnimationQueue.scaleZQueue
						.add(getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, tick, false, Axis.Z));
			}
		}

		if (soundListener != null || particleListener != null || customInstructionListener != null) {
			for (EventKeyFrame<String> soundKeyFrame : currentAnimation.soundKeyFrames) {
				if (!this.executedKeyFrames.contains(soundKeyFrame) && tick >= soundKeyFrame.getStartTick()) {
					SoundKeyframeEvent<T> event = new SoundKeyframeEvent<>(this.animatable, tick,
							soundKeyFrame.getEventData(), this);
					soundListener.playSound(event);

					this.executedKeyFrames.add(soundKeyFrame);
				}
			}

			for (ParticleEventKeyFrame particleEventKeyFrame : currentAnimation.particleKeyFrames) {
				if (!this.executedKeyFrames.contains(particleEventKeyFrame)
						&& tick >= particleEventKeyFrame.getStartTick()) {
					ParticleKeyFrameEvent<T> event = new ParticleKeyFrameEvent<>(this.animatable, tick,
							particleEventKeyFrame.effect, particleEventKeyFrame.locator, particleEventKeyFrame.script,
							this);
					particleListener.summonParticle(event);

					this.executedKeyFrames.add(particleEventKeyFrame);
				}
			}

			for (EventKeyFrame<String> customInstructionKeyFrame : currentAnimation.customInstructionKeyframes) {
				if (!this.executedKeyFrames.contains(customInstructionKeyFrame)
						&& tick >= customInstructionKeyFrame.getStartTick()) {
					CustomInstructionKeyframeEvent<T> event = new CustomInstructionKeyframeEvent<>(this.animatable,
							tick, customInstructionKeyFrame.getEventData(), this);
					customInstructionListener.executeInstruction(event);

					this.executedKeyFrames.add(customInstructionKeyFrame);
				}
			}
		}

		if (this.transitionLengthTicks == 0 && shouldResetTick && this.animationState == AnimationState.Transitioning) {
			this.currentAnimation = animationQueue.poll();
		}
	}

	// Helper method to populate all the initial animation point queues
	private void createInitialQueues(List<IBone> modelRendererList) {
		boneAnimationQueues.clear();
		for (IBone modelRenderer : modelRendererList) {
			boneAnimationQueues.put(modelRenderer.getName(), new BoneAnimationQueue(modelRenderer));
		}
	}

	// Used to reset the "tick" everytime a new animation starts, a transition
	// starts, or something else of importance happens
	protected double adjustTick(double tick) {
		if (shouldResetTick) {
			if (getAnimationState() == AnimationState.Transitioning) {
				this.tickOffset = tick;
			} else if (getAnimationState() == AnimationState.Running) {
				this.tickOffset = tick;
			}
			shouldResetTick = false;
			return 0;
		} else {
			// assert tick - this.tickOffset >= 0;
			return animationSpeed * Math.max(tick - tickOffset, 0.0D);
		}
	}

	// Helper method to transform a KeyFrameLocation to an AnimationPoint
	private AnimationPoint getAnimationPointAtTick(List<KeyFrame<IValue>> frames, double tick, boolean isRotation,
			Axis axis) {
		KeyFrameLocation<KeyFrame<IValue>> location = getCurrentKeyFrameLocation(frames, tick);
		KeyFrame<IValue> currentFrame = location.currentFrame;
		double startValue = currentFrame.getStartValue().get();
		double endValue = currentFrame.getEndValue().get();

		if (isRotation) {
			if (!(currentFrame.getStartValue() instanceof ConstantValue)) {
				startValue = Math.toRadians(startValue);
				if (axis == Axis.X || axis == Axis.Y) {
					startValue *= -1;
				}
			}
			if (!(currentFrame.getEndValue() instanceof ConstantValue)) {
				endValue = Math.toRadians(endValue);
				if (axis == Axis.X || axis == Axis.Y) {
					endValue *= -1;
				}
			}
		}

		return new AnimationPoint(currentFrame, location.currentTick, currentFrame.getLength(), startValue, endValue);
	}

	/**
	 * Returns the current keyframe object, plus how long the previous keyframes
	 * have taken (aka elapsed animation time)
	 **/
	private KeyFrameLocation<KeyFrame<IValue>> getCurrentKeyFrameLocation(List<KeyFrame<IValue>> frames,
			double ageInTicks) {
		double totalTimeTracker = 0;
		for (KeyFrame<IValue> frame : frames) {
			totalTimeTracker += frame.getLength();
			if (totalTimeTracker > ageInTicks) {
				double tick = (ageInTicks - (totalTimeTracker - frame.getLength()));
				return new KeyFrameLocation<>(frame, tick);
			}
		}
		return new KeyFrameLocation<>(frames.get(frames.size() - 1), ageInTicks);
	}

	private void resetEventKeyFrames() {
		this.executedKeyFrames.clear();
	}

	public void markNeedsReload() {
		this.needsAnimationReload = true;
	}

	public void clearAnimationCache() {
		this.currentAnimationBuilder = new AnimationBuilder();
	}

	public double getAnimationSpeed() {
		return animationSpeed;
	}

	public void setAnimationSpeed(double animationSpeed) {
		this.animationSpeed = animationSpeed;
	}

	@FunctionalInterface
	public interface ModelFetcher<T> extends Function<IAnimatable, IAnimatableModel<T>> {
	}
}