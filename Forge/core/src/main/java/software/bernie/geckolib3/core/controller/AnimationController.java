/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.controller;

import com.eliotlash.mclib.math.IValue;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.ConstantValue;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.Animation;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.*;
import software.bernie.geckolib3.core.model.GeoBone;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.core.util.Axis;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * The actual controller that handles the playing and usage of animations, including their various keyframes and instruction markers.
 * Each controller can only play a single animation at a time.
 */
public class AnimationController<T extends GeoAnimatable> {
	protected final T animatable;
	protected final String name;
	protected final AnimationStateHandler<T> stateHandler;
	protected final double transitionLength;
	protected final Map<String, BoneAnimationQueue> boneAnimationQueues = new Object2ObjectOpenHashMap<>();
	protected final Queue<Animation> animationQueue = new LinkedList<>();
	protected final Map<String, BoneSnapshot> boneSnapshots = new Object2ObjectOpenHashMap<>();

	protected boolean isJustStarting = false;
	protected boolean needsAnimationReload = false;
	protected boolean shouldResetTick = false;
	private boolean justStopped = false;
	protected boolean justStartedTransition = false;

	protected SoundKeyframeHandler<T> soundKeyframeHandler;
	protected ParticleKeyframeHandler<T> particleKeyframeHandler;
	protected CustomKeyframeHandler<T> customKeyframeHandler;

	protected Animation currentAnimation;
	protected AnimationState animationState = AnimationState.STOPPED;
	protected double tickOffset;
	protected double animationSpeed = 1;
	protected EasingType easingType = EasingType.NONE;




	protected AnimationBuilder currentAnimationBuilder = new AnimationBuilder();
	public Double2DoubleFunction customEasingMethod;
	private final Set<EventKeyFrame<?>> executedKeyFrames = new ObjectOpenHashSet<>();



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
							   AnimationStateHandler<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLength = transitionLengthTicks;
		this.stateHandler = animationPredicate;
		this.tickOffset = 0.0d;
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
							   AnimationStateHandler<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLength = transitionLengthTicks;
		this.easingType = easingtype;
		this.stateHandler = animationPredicate;
		this.tickOffset = 0.0d;
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
							   Double2DoubleFunction customEasingMethod, AnimationStateHandler<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLength = transitionLengthTicks;
		this.customEasingMethod = customEasingMethod;
		this.easingType = EasingType.CUSTOM;
		this.stateHandler = animationPredicate;
		this.tickOffset = 0.0d;
	}

	/**
	 * Gets the controller's name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the current animation. Can be null
	 *
	 * @return the current animation
	 */

	public Animation getCurrentAnimation() {
		return this.currentAnimation;
	}

	/**
	 * Returns the current state of this animation controller.
	 */
	public AnimationState getAnimationState() {
		return this.animationState;
	}

	/**
	 * Gets the current animation's bone animation queues.
	 *
	 * @return the bone animation queues
	 */
	public HashMap<String, BoneAnimationQueue> getBoneAnimationQueues() {
		return this.boneAnimationQueues;
	}

	/**
	 * Registers a sound listener.
	 */
	public void registerSoundListener(SoundKeyframeHandler<T> soundListener) {
		this.soundKeyframeHandler = soundListener;
	}

	/**
	 * Registers a particle listener.
	 */
	public void registerParticleListener(ParticleKeyframeHandler<T> particleListener) {
		this.particleKeyframeHandler = particleListener;
	}

	/**
	 * Registers a custom instruction listener.
	 */
	public void registerCustomInstructionListener(CustomKeyframeHandler<T> customInstructionListener) {
		this.customKeyframeHandler = customInstructionListener;
	}

	public void markNeedsReload() {
		this.needsAnimationReload = true;
	}

	public void clearAnimationCache() {
		this.currentAnimationBuilder = new AnimationBuilder();
	}

	public double getAnimationSpeed() {
		return this.animationSpeed;
	}

	public void setAnimationSpeed(double animationSpeed) {
		this.animationSpeed = animationSpeed;
	}

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
				this.animationState = AnimationState.STOPPED;
			}
			else if (!builder.getRawAnimationList().equals(this.currentAnimationBuilder.getRawAnimationList())
					|| this.needsAnimationReload) {
				AtomicBoolean encounteredError = new AtomicBoolean(false);
				// Convert the list of animation names to the actual list, keeping track of the
				// loop boolean along the way
				LinkedList<Animation> animations = builder.getRawAnimationList().stream().map((rawAnimation) -> {
					Animation animation = model.getAnimation(rawAnimation.animationName, animatable);

					if (animation == null) {
						System.out.printf("Could not load animation: %s. Is it missing?", rawAnimation.animationName);
						encounteredError.set(true);
					}

					if (animation != null && rawAnimation.loopType != null)
						animation.loop = rawAnimation.loopType;

					return animation;
				}).collect(Collectors.toCollection(LinkedList::new));

				if (encounteredError.get())
					return;

				this.animationQueue = animations;
				this.currentAnimationBuilder = builder;
				this.shouldResetTick = true; // Reset the adjusted tick to 0 on next animation process call
				this.animationState = AnimationState.TRANSITIONING;
				this.justStartedTransition = true;
				this.needsAnimationReload = false;
			}
		}
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
	public void process(final double tick, AnimationEvent<T> event, List<GeoBone> modelRendererList,
						Map<String, Pair<GeoBone, BoneSnapshot>> boneSnapshotCollection, MolangParser parser,
						boolean crashWhenCantFindBone) {
		parser.setValue("query.life_time", () -> tick / 20);

		if (this.currentAnimation != null) {
			IAnimatableModel<T> model = getModel(this.animatable);

			if (model != null) {
				Animation animation = model.getAnimation(currentAnimation.animationName, this.animatable);

				if (animation != null) {
					ILoopType loop = this.currentAnimation.loop;
					this.currentAnimation = animation;
					this.currentAnimation.loop = loop;
				}
			}
		}

		createInitialQueues(modelRendererList);

		double adjustedTick = adjustTick(tick);

		// Transition period has ended, reset the tick and set the animation to running
		if (animationState == AnimationState.TRANSITIONING && adjustedTick >= this.transitionLength) {
			this.shouldResetTick = true;
			this.animationState = AnimationState.RUNNING;
			adjustedTick = adjustTick(tick);
		}

		assert adjustedTick >= 0 : "GeckoLib: Tick was less than zero";

		// This tests the animation predicate
		PlayState playState = this.testAnimationPredicate(event);

		if (playState == PlayState.STOP || (this.currentAnimation == null && this.animationQueue.size() == 0)) {
			// The animation should transition to the model's initial state
			this.animationState = AnimationState.STOPPED;
			this.justStopped = true;

			return;
		}

		if (this.justStartedTransition && (this.shouldResetTick || this.justStopped)) {
			this.justStopped = false;
			adjustedTick = adjustTick(tick);
		}
		else if (this.currentAnimation == null && this.animationQueue.size() != 0) {
			this.shouldResetTick = true;
			this.animationState = AnimationState.TRANSITIONING;
			this.justStartedTransition = true;
			this.needsAnimationReload = false;
			adjustedTick = adjustTick(tick);
		}
		else if (this.animationState != AnimationState.TRANSITIONING) {
			this.animationState = AnimationState.RUNNING;
		}

		// Handle transitioning to a different animation (or just starting one)
		if (this.animationState == AnimationState.TRANSITIONING) {
			// Just started transitioning, so set the current animation to the first one
			if (adjustedTick == 0 || this.isJustStarting) {
				this.justStartedTransition = false;
				this.currentAnimation = animationQueue.poll();

				resetEventKeyFrames();
				saveSnapshotsForAnimation(this.currentAnimation, boneSnapshotCollection);
			}
			if (this.currentAnimation != null) {
				setAnimTime(parser, 0);

				for (BoneAnimation boneAnimation : this.currentAnimation.boneAnimations) {
					BoneAnimationQueue boneAnimationQueue = this.boneAnimationQueues.get(boneAnimation.boneName);
					BoneSnapshot boneSnapshot = this.boneSnapshots.get(boneAnimation.boneName);
					Optional<GeoBone> first = Optional.empty();

					for (GeoBone bone : modelRendererList) {
						if (bone.getName().equals(boneAnimation.boneName)) {
							first = Optional.of(bone);
							break;
						}
					}

					if (first.isEmpty()) {
						if (crashWhenCantFindBone)
							throw new RuntimeException("Could not find bone: " + boneAnimation.boneName);

						continue;
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
						boneAnimationQueue.rotationXQueue().add(new AnimationPoint(null, adjustedTick, this.transitionLength,
								boneSnapshot.rotationValueX - initialSnapshot.rotationValueX,
								xPoint.animationStartValue));
						boneAnimationQueue.rotationYQueue().add(new AnimationPoint(null, adjustedTick, this.transitionLength,
								boneSnapshot.rotationValueY - initialSnapshot.rotationValueY,
								yPoint.animationStartValue));
						boneAnimationQueue.rotationZQueue().add(new AnimationPoint(null, adjustedTick, this.transitionLength,
								boneSnapshot.rotationValueZ - initialSnapshot.rotationValueZ,
								zPoint.animationStartValue));
					}

					if (!positionKeyFrames.xKeyFrames.isEmpty()) {
						AnimationPoint xPoint = getAnimationPointAtTick(positionKeyFrames.xKeyFrames, 0, false, Axis.X);
						AnimationPoint yPoint = getAnimationPointAtTick(positionKeyFrames.yKeyFrames, 0, false, Axis.Y);
						AnimationPoint zPoint = getAnimationPointAtTick(positionKeyFrames.zKeyFrames, 0, false, Axis.Z);
						boneAnimationQueue.positionXQueue().add(new AnimationPoint(null, adjustedTick, this.transitionLength,
								boneSnapshot.positionOffsetX, xPoint.animationStartValue));
						boneAnimationQueue.positionYQueue().add(new AnimationPoint(null, adjustedTick, this.transitionLength,
								boneSnapshot.positionOffsetY, yPoint.animationStartValue));
						boneAnimationQueue.positionZQueue().add(new AnimationPoint(null, adjustedTick, this.transitionLength,
								boneSnapshot.positionOffsetZ, zPoint.animationStartValue));
					}

					if (!scaleKeyFrames.xKeyFrames.isEmpty()) {
						AnimationPoint xPoint = getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, 0, false, Axis.X);
						AnimationPoint yPoint = getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, 0, false, Axis.Y);
						AnimationPoint zPoint = getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, 0, false, Axis.Z);
						boneAnimationQueue.scaleXQueue().add(new AnimationPoint(null, adjustedTick, this.transitionLength,
								boneSnapshot.scaleValueX, xPoint.animationStartValue));
						boneAnimationQueue.scaleYQueue().add(new AnimationPoint(null, adjustedTick, this.transitionLength,
								boneSnapshot.scaleValueY, yPoint.animationStartValue));
						boneAnimationQueue.scaleZQueue().add(new AnimationPoint(null, adjustedTick, this.transitionLength,
								boneSnapshot.scaleValueZ, zPoint.animationStartValue));
					}
				}
			}
		}
		else if (getAnimationState() == AnimationState.RUNNING) {
			// Actually run the animation
			processCurrentAnimation(adjustedTick, tick, parser, crashWhenCantFindBone);
		}
	}

	private void setAnimTime(MolangParser parser, final double tick) {
		parser.setValue("query.anim_time", () -> tick / 20);
	}

	private IAnimatableModel<T> getModel(T animatable) {
		for (ModelFetcher<?> modelFetcher : modelFetchers) {
			IAnimatableModel<T> model = (IAnimatableModel<T>) modelFetcher.apply(animatable);

			if (model != null)
				return model;
		}

		System.out.printf(
				"Could not find suitable model for animatable of type %s. Did you register a Model Fetcher?%n",
				animatable.getClass());

		return null;
	}

	protected PlayState testAnimationPredicate(AnimationEvent<T> event) {
		return this.stateHandler.handle(event);
	}

	// At the beginning of a new transition, save a snapshot of the model's
	// rotation, position, and scale values as the initial value to lerp from
	private void saveSnapshotsForAnimation(Animation animation,
			Map<String, Pair<GeoBone, BoneSnapshot>> boneSnapshotCollection) {
		for (Pair<GeoBone, BoneSnapshot> snapshot : boneSnapshotCollection.values()) {
			if (animation != null && animation.boneAnimations != null) {
				for (BoneAnimation boneAnimation : animation.boneAnimations) {
					if (boneAnimation.boneName.equals(snapshot.getLeft().getName())) {
						this.boneSnapshots.put(boneAnimation.boneName, new BoneSnapshot(snapshot.getRight()));

						break;
					}
				}
			}
		}
	}

	private void processCurrentAnimation(double tick, double actualTick, MolangParser parser,
			boolean crashWhenCantFindBone) {
		assert currentAnimation != null;
		// Animation has ended
		if (tick >= this.currentAnimation.animationLength) {
			resetEventKeyFrames();
			// If the current animation is set to loop, keep it as the current animation and
			// just start over
			if (!this.currentAnimation.loop.isRepeatingAfterEnd()) {
				// Pull the next animation from the queue
				Animation peek = this.animationQueue.peek();

				if (peek == null) {
					// No more animations left, stop the animation controller
					this.animationState = AnimationState.STOPPED;

					return;
				}
				else {
					// Otherwise, set the state to transitioning and start transitioning to the next
					// animation next frame
					this.animationState = AnimationState.TRANSITIONING;
					this.shouldResetTick = true;
					this.currentAnimation = this.animationQueue.peek();
				}
			}
			else {
				// Reset the adjusted tick so the next animation starts at tick 0
				this.shouldResetTick = true;
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
				if (crashWhenCantFindBone)
					throw new RuntimeException("Could not find bone: " + boneAnimation.boneName);

				continue;
			}

			VectorKeyFrameList<KeyFrame<IValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames;
			VectorKeyFrameList<KeyFrame<IValue>> positionKeyFrames = boneAnimation.positionKeyFrames;
			VectorKeyFrameList<KeyFrame<IValue>> scaleKeyFrames = boneAnimation.scaleKeyFrames;

			if (!rotationKeyFrames.xKeyFrames.isEmpty()) {
				boneAnimationQueue.rotationXQueue()
						.add(getAnimationPointAtTick(rotationKeyFrames.xKeyFrames, tick, true, Axis.X));
				boneAnimationQueue.rotationYQueue()
						.add(getAnimationPointAtTick(rotationKeyFrames.yKeyFrames, tick, true, Axis.Y));
				boneAnimationQueue.rotationZQueue()
						.add(getAnimationPointAtTick(rotationKeyFrames.zKeyFrames, tick, true, Axis.Z));
			}

			if (!positionKeyFrames.xKeyFrames.isEmpty()) {
				boneAnimationQueue.positionXQueue()
						.add(getAnimationPointAtTick(positionKeyFrames.xKeyFrames, tick, false, Axis.X));
				boneAnimationQueue.positionYQueue()
						.add(getAnimationPointAtTick(positionKeyFrames.yKeyFrames, tick, false, Axis.Y));
				boneAnimationQueue.positionZQueue()
						.add(getAnimationPointAtTick(positionKeyFrames.zKeyFrames, tick, false, Axis.Z));
			}

			if (!scaleKeyFrames.xKeyFrames.isEmpty()) {
				boneAnimationQueue.scaleXQueue()
						.add(getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, tick, false, Axis.X));
				boneAnimationQueue.scaleYQueue()
						.add(getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, tick, false, Axis.Y));
				boneAnimationQueue.scaleZQueue()
						.add(getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, tick, false, Axis.Z));
			}
		}

		if (this.soundKeyframeHandler != null || this.particleKeyframeHandler != null || this.customKeyframeHandler != null) {
			for (EventKeyFrame<String> soundKeyFrame : this.currentAnimation.soundKeyFrames) {
				if (!this.executedKeyFrames.contains(soundKeyFrame) && tick >= soundKeyFrame.getStartTick()) {
					SoundKeyframeEvent<T> event = new SoundKeyframeEvent<>(this.animatable, tick,
							soundKeyFrame.getEventData(), this);

					this.soundKeyframeHandler.handle(event);
					this.executedKeyFrames.add(soundKeyFrame);
				}
			}

			for (ParticleEventKeyFrame particleEventKeyFrame : this.currentAnimation.particleKeyFrames) {
				if (!this.executedKeyFrames.contains(particleEventKeyFrame)
						&& tick >= particleEventKeyFrame.getStartTick()) {
					ParticleKeyFrameEvent<T> event = new ParticleKeyFrameEvent<>(this.animatable, tick,
							particleEventKeyFrame.effect, particleEventKeyFrame.locator, particleEventKeyFrame.script,
							this);

					this.particleKeyframeHandler.handle(event);
					this.executedKeyFrames.add(particleEventKeyFrame);
				}
			}

			for (EventKeyFrame<String> customInstructionKeyFrame : currentAnimation.customInstructionKeyframes) {
				if (!this.executedKeyFrames.contains(customInstructionKeyFrame)
						&& tick >= customInstructionKeyFrame.getStartTick()) {
					CustomInstructionKeyframeEvent<T> event = new CustomInstructionKeyframeEvent<>(this.animatable,
							tick, customInstructionKeyFrame.getEventData(), this);

					this.customKeyframeHandler.handle(event);
					this.executedKeyFrames.add(customInstructionKeyFrame);
				}
			}
		}

		if (this.transitionLength == 0 && shouldResetTick && this.animationState == AnimationState.TRANSITIONING)
			this.currentAnimation = animationQueue.poll();
	}

	// Helper method to populate all the initial animation point queues
	private void createInitialQueues(List<GeoBone> modelRendererList) {
		this.boneAnimationQueues.clear();

		for (GeoBone modelRenderer : modelRendererList) {
			this.boneAnimationQueues.put(modelRenderer.getName(), new BoneAnimationQueue(modelRenderer));
		}
	}

	// Used to reset the "tick" everytime a new animation starts, a transition
	// starts, or something else of importance happens
	protected double adjustTick(double tick) {
		if (this.shouldResetTick) {
			if (getAnimationState() == AnimationState.TRANSITIONING) {
				this.tickOffset = tick;
			}
			else if (getAnimationState() == AnimationState.RUNNING) {
				this.tickOffset = tick;
			}

			this.shouldResetTick = false;

			return 0;
		}
		else {
			// assert tick - this.tickOffset >= 0;
			return this.animationSpeed * Math.max(tick - this.tickOffset, 0.0D);
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

				if (axis == Axis.X || axis == Axis.Y)
					startValue *= -1;
			}

			if (!(currentFrame.getEndValue() instanceof ConstantValue)) {
				endValue = Math.toRadians(endValue);

				if (axis == Axis.X || axis == Axis.Y)
					endValue *= -1;
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

	/**
	 * Every render frame, the {@code AnimationController} will call this handler for <u>each</u> animatable that is being rendered.
	 * This handler defines which animation should be currently playing, and returning a {@link PlayState} to tell the controller what to do next.<br>
	 * Example Usage:<br>
	 * <pre>AnimationFrameHandler myIdleWalkHandler = event -> {
	 * 	if (event.isMoving()) {
	 * 		event.getController().setAnimation(myWalkAnimation);
	 * 	}
	 * 	else {
	 * 		event.getController().setAnimation(myIdleAnimation);
	 * 	}
	 *
	 * 	return PlayState.CONTINUE;
	 * };</pre>
	 */
	@FunctionalInterface
	public interface AnimationStateHandler<A extends GeoAnimatable> {
		/**
		 * The handling method, called each frame.
		 * Return {@link PlayState#CONTINUE} to tell the controller to continue animating,
		 * or return {@link PlayState#STOP} to tell it to stop playing all animations and wait for the next {@code PlayState.CONTINUE} return.
		 */
		PlayState handle(AnimationEvent<A> event);
	}

	/**
	 * A handler for when a predefined sound keyframe is hit.
	 * When the keyframe is encountered, the {@link SoundKeyframeHandler#handle(SoundKeyframeEvent)} method will be called.
	 * Play the sound(s) of your choice at this time.
	 */
	@FunctionalInterface
	public interface SoundKeyframeHandler<A extends GeoAnimatable> {
		void handle(SoundKeyframeEvent<A> event);
	}

	/**
	 * A handler for when a predefined particle keyframe is hit.
	 * When the keyframe is encountered, the {@link ParticleKeyframeHandler#handle(ParticleKeyFrameEvent)} method will be called.
	 * Spawn the particles/effects of your choice at this time.
	 */
	@FunctionalInterface
	public interface ParticleKeyframeHandler<A extends GeoAnimatable> {
		void handle(ParticleKeyFrameEvent<A> event);
	}

	/**
	 * A handler for pre-defined custom instruction keyframes.
	 * When the keyframe is encountered, the {@link CustomKeyframeHandler#handle(CustomInstructionKeyframeEvent)} method will be called.
	 * You can then take whatever action you want at this point.
	 */
	@FunctionalInterface
	public interface CustomKeyframeHandler<A extends GeoAnimatable> {
		void handle(CustomInstructionKeyframeEvent<A> event);
	}
}