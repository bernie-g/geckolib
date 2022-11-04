/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.animation;

import com.eliotlash.mclib.math.IValue;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.ConstantValue;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animatable.model.GeoBone;
import software.bernie.geckolib3.core.animatable.model.GeoModel;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.*;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.core.state.BoneSnapshot;
import software.bernie.geckolib3.core.util.Axis;

import java.util.*;
import java.util.function.Function;

/**
 * The actual controller that handles the playing and usage of animations, including their various keyframes and instruction markers.
 * Each controller can only play a single animation at a time - for example you may have one controller to animate walking,
 * one to control attacks, one to control size, etc.
 */
public class AnimationController<T extends GeoAnimatable> {
	protected final T animatable;
	protected final String name;
	protected final AnimationStateHandler<T> stateHandler;
	protected final double transitionLength;
	protected final Map<String, BoneAnimationQueue> boneAnimationQueues = new Object2ObjectOpenHashMap<>();
	protected final Map<String, BoneSnapshot> boneSnapshots = new Object2ObjectOpenHashMap<>();
	protected Queue<AnimationProcessor.QueuedAnimation> animationQueue = new LinkedList<>();

	protected boolean isJustStarting = false;
	protected boolean needsAnimationReload = false;
	protected boolean shouldResetTick = false;
	private boolean justStopped = false;
	protected boolean justStartedTransition = false;

	protected SoundKeyframeHandler<T> soundKeyframeHandler;
	protected ParticleKeyframeHandler<T> particleKeyframeHandler;
	protected CustomKeyframeHandler<T> customKeyframeHandler;

	protected RawAnimation currentRawAnimation;
	protected AnimationProcessor.QueuedAnimation currentAnimation;
	protected AnimationState animationState = AnimationState.STOPPED;
	protected double tickOffset;
	protected Function<T, Double> animationSpeedModifier = animatable -> 1d;
	protected EasingType easingType = EasingType.LINEAR;
	private final Set<EventKeyFrame<?>> executedKeyFrames = new ObjectOpenHashSet<>();

	/**
	 * Instantiates a new {@code AnimationController}.<br>
	 * This constructor assumes a 0-tick transition length between animations, and a generic name.
	 * @param animatable The object that will be animated by this controller
	 * @param animationHandler The {@link AnimationStateHandler} animation state handler responsible for deciding which animations to play
	 */
	public AnimationController(T animatable, AnimationStateHandler<T> animationHandler) {
		this(animatable, "base_controller", 0, animationHandler);
	}

	/**
	 * Instantiates a new {@code AnimationController}.<br>
	 * This constructor assumes a 0-tick transition length between animations.
	 * @param animatable The object that will be animated by this controller
	 * @param name The name of the controller - should represent what animations it handles
	 * @param animationHandler The {@link AnimationStateHandler} animation state handler responsible for deciding which animations to play
	 */
	public AnimationController(T animatable, String name, AnimationStateHandler<T> animationHandler) {
		this(animatable, name, 0, animationHandler);
	}

	/**
	 * Instantiates a new {@code AnimationController}.<br>
	 * This constructor assumes a generic name.
	 * @param animatable The object that will be animated by this controller
	 * @param transitionTickTime The amount of time (in <b>ticks</b>) that the controller should take to transition between animations.
	 *                              Lerping is automatically applied where possible
	 * @param animationHandler The {@link AnimationStateHandler} animation state handler responsible for deciding which animations to play
	 */
	public AnimationController(T animatable, int transitionTickTime, AnimationStateHandler<T> animationHandler) {
		this(animatable, "base_controller", transitionTickTime, animationHandler);
	}

	/**
	 * Instantiates a new {@code AnimationController}.<br>
	 * @param animatable The object that will be animated by this controller
	 * @param name The name of the controller - should represent what animations it handles
	 * @param transitionTickTime The amount of time (in <b>ticks</b>) that the controller should take to transition between animations.
	 *                              Lerping is automatically applied where possible
	 * @param animationHandler The {@link AnimationStateHandler} animation state handler responsible for deciding which animations to play
	 */
	public AnimationController(T animatable, String name, int transitionTickTime, AnimationStateHandler<T> animationHandler) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLength = transitionTickTime;
		this.stateHandler = animationHandler;
	}

	/**
	 * Applies the given {@link SoundKeyframeHandler} to this controller, for handling {@link SoundKeyframeEvent sound keyframe instructions}.
	 * @return this
	 */
	public AnimationController<T> setSoundKeyframeHandler(SoundKeyframeHandler<T> soundHandler) {
		this.soundKeyframeHandler = soundHandler;

		return this;
	}

	/**
	 * Applies the given {@link ParticleKeyframeHandler} to this controller, for handling {@link ParticleKeyFrameEvent particle keyframe instructions}.
	 * @return this
	 */
	public AnimationController<T> setParticleKeyframeHandler(ParticleKeyframeHandler<T> particleHandler) {
		this.particleKeyframeHandler = particleHandler;

		return this;
	}

	/**
	 * Applies the given {@link CustomKeyframeHandler} to this controller, for handling {@link CustomInstructionKeyframeEvent sound keyframe instructions}.
	 * @return this
	 */
	public AnimationController<T> setCustomInstructionKeyframeHandler(CustomKeyframeHandler<T> customInstructionHandler) {
		this.customKeyframeHandler = customInstructionHandler;

		return this;
	}

	/**
	 * Applies the given modifier function to this controller, for handling the speed that the controller should play its animations at.<br>
	 * An output value of 1 is considered neutral, with 2 playing an animation twice as fast, 0.5 playing half as fast, etc.
	 * @param speedModFunction The function to apply to this controller to handle animation speed
	 * @return this
	 */
	public AnimationController<T> setAnimationSpeedHandler(Function<T, Double> speedModFunction) {
		this.animationSpeedModifier = speedModFunction;

		return this;
	}

	/**
	 * Applies the given modifier value to this controller, for handlign the speed that the controller hsould play its animations at.<br>
	 * A value of 1 is considered neutral, with 2 playing an animation twice as fast, 0.5 playing half as fast, etc.
	 * @param speed The speed modifier to apply to this controller to handle animation speed.
	 * @return this
	 */
	public AnimationController<T> setAnimationSpeed(double speed) {
		return setAnimationSpeedHandler(animatable -> speed);
	}

	/**
	 * Gets the controller's name.
	 * @return The name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the currently loaded {@link Animation}. Can be null<br>
	 * An animation returned here does not guarantee it is currently playing, just that it is the currently loaded animation for this controller
	 */

	public AnimationProcessor.QueuedAnimation getCurrentAnimation() {
		return this.currentAnimation;
	}

	/**
	 * Returns the current state of this controller.
	 */
	public AnimationState getAnimationState() {
		return this.animationState;
	}

	/**
	 * Gets the currently loaded animation's {@link BoneAnimationQueue BoneAnimationQueues}.
	 */
	public Map<String, BoneAnimationQueue> getBoneAnimationQueues() {
		return this.boneAnimationQueues;
	}

	/**
	 * Gets the current animation speed modifier.<br>
	 * This modifier defines the relative speed in which animations will be played based on the current state of the game.
	 * @return The computed current animation speed modifier
	 */
	public double getAnimationSpeed() {
		return this.animationSpeedModifier.apply(this.animatable);
	}

	/**
	 * Marks the controller as needing to reset its animation and state the next time {@link AnimationController#setAnimation(RawAnimation)} is called.
	 */
	public void markNeedsReload() {
		this.needsAnimationReload = true;
	}

	/**
	 * Tells the controller to stop all animations until told otherwise.<br>
	 * Calling this will prevent the controller from continuing to play the currently loaded animation until
	 * either {@link AnimationController#markNeedsReload()} is called, or
	 * {@link AnimationController#setAnimation(RawAnimation)} is called with a different animation
	 */
	public void stop() {
		this.animationState = AnimationState.STOPPED;
	}

	/**
	 * Sets the currently loaded animation to the one provided.<br>
	 * This method may be safely called every render frame, as passing the same builder that is already loaded will do nothing.<br>
	 * Pass null to this method to tell the controller to stop.<br>
	 * If {@link AnimationController#markNeedsReload()} has been called prior to this, the controller will reload the animation regardless of whether it matches the currently loaded one or not
	 * @param rawAnimation
	 */
	public void setAnimation(RawAnimation rawAnimation) {
		if (rawAnimation == null || rawAnimation.getAnimationStages().isEmpty()) {
			stop();

			return;
		}

		if (this.needsAnimationReload || !rawAnimation.equals(this.currentRawAnimation)) {
			GeoModel<T> model = this.animatable.getGeoModel().get();

			if (model != null) {
				Queue<AnimationProcessor.QueuedAnimation> animations = model.getAnimationProcessor().buildAnimationQueue(this.animatable, rawAnimation);

				if (animations != null) {
					this.animationQueue = animations;
					this.currentRawAnimation = rawAnimation;
					this.shouldResetTick = true;
					this.animationState = AnimationState.TRANSITIONING;
					this.justStartedTransition = true;
					this.needsAnimationReload = false;

					return;
				}
			}

			stop();
		}
	}

	/**
	 * This method is called every frame in order to populate the animation point
	 * queues, and process animation state logic.
	 *
	 * @param seekTime                   The current tick + partial tick
	 * @param event                  The animation test event
	 * @param bones      The registered {@link GeoBone bones} for this model
	 * @param snapshots The {@link BoneSnapshot} map
	 * @param crashWhenCantFindBone Whether to hard-fail when a bone can't be found, or to continue with the remaining bones
	 */
	public void process(final double seekTime, AnimationEvent<T> event, List<GeoBone> bones,
						Map<String, BoneSnapshot> snapshots, boolean crashWhenCantFindBone) {
		double adjustedTick = adjustTick(seekTime);

		createInitialQueues(bones);

		if (animationState == AnimationState.TRANSITIONING && adjustedTick >= this.transitionLength) {
			this.shouldResetTick = true;
			this.animationState = AnimationState.RUNNING;
			adjustedTick = adjustTick(seekTime);
		}

		PlayState playState = this.stateHandler.handle(event);

		if (playState == PlayState.STOP || (this.currentAnimation == null && this.animationQueue.isEmpty())) {
			this.animationState = AnimationState.STOPPED;
			this.justStopped = true;

			return;
		}

		if (this.justStartedTransition && (this.shouldResetTick || this.justStopped)) {
			this.justStopped = false;
			adjustedTick = adjustTick(seekTime);
		}
		else if (this.currentAnimation == null) {
			this.shouldResetTick = true;
			this.animationState = AnimationState.TRANSITIONING;
			this.justStartedTransition = true;
			this.needsAnimationReload = false;
			adjustedTick = adjustTick(seekTime);
		}
		else if (this.animationState != AnimationState.TRANSITIONING) {
			this.animationState = AnimationState.RUNNING;
		}

		if (getAnimationState() == AnimationState.RUNNING) {
			processCurrentAnimation(adjustedTick, seekTime, crashWhenCantFindBone);
		}
		else if (this.animationState == AnimationState.TRANSITIONING) {
			if (adjustedTick == 0 || this.isJustStarting) {
				this.justStartedTransition = false;
				this.currentAnimation = this.animationQueue.poll();

				resetEventKeyFrames();

				if (currentAnimation == null)
					return;

				saveSnapshotsForAnimation(this.currentAnimation, snapshots);
			}

			if (this.currentAnimation != null) {
				MolangParser.INSTANCE.setValue("query.anim_time", () -> 0);

				for (BoneAnimation boneAnimation : this.currentAnimation.animation().boneAnimations()) {
					BoneAnimationQueue boneAnimationQueue = this.boneAnimationQueues.get(boneAnimation.boneName);
					BoneSnapshot boneSnapshot = this.boneSnapshots.get(boneAnimation.boneName);
					Optional<GeoBone> first = Optional.empty();

					for (GeoBone bone : bones) {
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
	}

	/**
	 * Sets the {@code anim_time} molang query, overriding the current value
	 * @param tick The tick
	 */
	private void setAnimTime(double tick) {

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

	/**
	 * Cache the relevant {@link BoneSnapshot BoneSnapshots} for the current {@link software.bernie.geckolib3.core.animation.AnimationProcessor.QueuedAnimation}
	 * for animation lerping
	 * @param animation The {@code QueuedAnimation} to filter {@code BoneSnapshots} for
	 * @param snapshots The master snapshot collection to pull filter from
	 */
	private void saveSnapshotsForAnimation(AnimationProcessor.QueuedAnimation animation, Map<String, BoneSnapshot> snapshots) {
		for (BoneSnapshot snapshot : snapshots.values()) {
			if (animation.animation().boneAnimations() != null) {
				for (BoneAnimation boneAnimation : animation.boneAnimations) {
					if (boneAnimation.boneName.equals(snapshot.name)) {
						this.boneSnapshots.put(boneAnimation.boneName, new BoneSnapshot(snapshot));

						break;
					}
				}
			}
		}
	}

	private void processCurrentAnimation(double tick, double actualTick, boolean crashWhenCantFindBone) {
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

	// TODO: Look into replacing the BoneAnimationQueue functionality, it is very inefficient
	/**
	 * Prepare the {@link BoneAnimationQueue} map for the current render frame
	 * @param modelRendererList The bone list from the {@link AnimationProcessor}
	 */
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
			return this.animationSpeedModifier * Math.max(tick - this.tickOffset, 0.0D);
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

	/**
	 * Clear the {@link EventKeyFrame} cache in preparation for the next animation
	 */
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