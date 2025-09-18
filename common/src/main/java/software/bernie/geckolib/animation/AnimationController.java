package software.bernie.geckolib.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Direction.Axis;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.keyframe.*;
import software.bernie.geckolib.animation.keyframe.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib.animation.keyframe.event.ParticleKeyframeEvent;
import software.bernie.geckolib.animation.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.animation.keyframe.event.data.CustomInstructionKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.KeyFrameData;
import software.bernie.geckolib.animation.keyframe.event.data.ParticleKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.SoundKeyframeData;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.loading.math.value.Constant;
import software.bernie.geckolib.model.GeoModel;

import java.util.*;
import java.util.function.Function;

/**
 * The actual controller that handles the playing and usage of animations, including their various keyframes and instruction markers
 * <p>
 * Each controller can only play a single animation at a time - for example you may have one controller to animate walking,
 * one to control attacks, one to control size, etc.
 */
public class AnimationController<T extends GeoAnimatable> {
	protected final T animatable;
	protected final String name;
	protected final AnimationStateHandler<T> stateHandler;
	protected final Map<String, BoneAnimationQueue> boneAnimationQueues = new Object2ObjectOpenHashMap<>();
	protected final Map<String, BoneSnapshot> boneSnapshots = new Object2ObjectOpenHashMap<>();
	protected Queue<AnimationProcessor.QueuedAnimation> animationQueue = new LinkedList<>();

	protected boolean isJustStarting = false;
	protected boolean needsAnimationReload = false;
	protected boolean shouldResetTick = false;
	private boolean justStopped = true;
	protected boolean justStartedTransition = false;

	protected SoundKeyframeHandler<T> soundKeyframeHandler = null;
	protected ParticleKeyframeHandler<T> particleKeyframeHandler = null;
	protected CustomKeyframeHandler<T> customKeyframeHandler = null;

	protected final Map<String, RawAnimation> triggerableAnimations = new Object2ObjectOpenHashMap<>(0);
	protected RawAnimation triggeredAnimation = null;
	protected boolean handlingTriggeredAnimations = false;

	protected double transitionLength;
	protected RawAnimation currentRawAnimation;
	protected AnimationProcessor.QueuedAnimation currentAnimation;
	protected State animationState = State.STOPPED;
	protected double tickOffset;
	protected double lastPollTime = -1;
	protected Function<T, Double> animationSpeedModifier = animatable -> 1d;
	protected Function<T, EasingType> overrideEasingTypeFunction = animatable -> null;
	private final Set<KeyFrameData> executedKeyFrames = new ObjectOpenHashSet<>();
	protected GeoModel<T> lastModel;

	/**
	 * Instantiates a new {@code AnimationController}
	 * <p>
	 * This constructor assumes a 0-tick transition length between animations, and a generic name
	 *
	 * @param animatable The object that will be animated by this controller
	 * @param animationHandler The {@link AnimationStateHandler} animation state handler responsible for deciding which animations to play
	 */
	public AnimationController(T animatable, AnimationStateHandler<T> animationHandler) {
		this(animatable, "base_controller", 0, animationHandler);
	}

	/**
	 * Instantiates a new {@code AnimationController}
	 * <p>
	 * This constructor assumes a 0-tick transition length between animations
	 *
	 * @param animatable The object that will be animated by this controller
	 * @param name The name of the controller - should represent what animations it handles
	 * @param animationHandler The {@link AnimationStateHandler} animation state handler responsible for deciding which animations to play
	 */
	public AnimationController(T animatable, String name, AnimationStateHandler<T> animationHandler) {
		this(animatable, name, 0, animationHandler);
	}

	/**
	 * Instantiates a new {@code AnimationController}
	 * <p>
	 * This constructor assumes a generic name
	 *
	 * @param animatable The object that will be animated by this controller
	 * @param transitionTickTime The amount of time (in <b>ticks</b>) that the controller should take to transition between animations.
	 *                              Lerping is automatically applied where possible
	 * @param animationHandler The {@link AnimationStateHandler} animation state handler responsible for deciding which animations to play
	 */
	public AnimationController(T animatable, int transitionTickTime, AnimationStateHandler<T> animationHandler) {
		this(animatable, "base_controller", transitionTickTime, animationHandler);
	}

	/**
	 * Instantiates a new {@code AnimationController}
	 *
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
	 * Applies the given {@link SoundKeyframeHandler} to this controller, for handling {@link SoundKeyframeEvent sound keyframe instructions}
	 *
	 * @return this
	 */
	public AnimationController<T> setSoundKeyframeHandler(SoundKeyframeHandler<T> soundHandler) {
		this.soundKeyframeHandler = soundHandler;

		return this;
	}

	/**
	 * Applies the given {@link ParticleKeyframeHandler} to this controller, for handling {@link ParticleKeyframeEvent particle keyframe instructions}
	 *
	 * @return this
	 */
	public AnimationController<T> setParticleKeyframeHandler(ParticleKeyframeHandler<T> particleHandler) {
		this.particleKeyframeHandler = particleHandler;

		return this;
	}

	/**
	 * Applies the given {@link CustomKeyframeHandler} to this controller, for handling {@link CustomInstructionKeyframeEvent sound keyframe instructions}
	 *
	 * @return this
	 */
	public AnimationController<T> setCustomInstructionKeyframeHandler(CustomKeyframeHandler<T> customInstructionHandler) {
		this.customKeyframeHandler = customInstructionHandler;

		return this;
	}

	/**
	 * Applies the given modifier function to this controller, for handling the speed that the controller should play its animations at
	 * <p>
	 * An output value of 1 is considered neutral, with 2 playing an animation twice as fast, 0.5 playing half as fast, etc
	 *
	 * @param speedModFunction The function to apply to this controller to handle animation speed
	 * @return this
	 */
	public AnimationController<T> setAnimationSpeedHandler(Function<T, Double> speedModFunction) {
		this.animationSpeedModifier = speedModFunction;

		return this;
	}

	/**
	 * Applies the given modifier value to this controller, for handlign the speed that the controller hsould play its animations at
	 * <p>
	 * A value of 1 is considered neutral, with 2 playing an animation twice as fast, 0.5 playing half as fast, etc
	 *
	 * @param speed The speed modifier to apply to this controller to handle animation speed.
	 * @return this
	 */
	public AnimationController<T> setAnimationSpeed(double speed) {
		return setAnimationSpeedHandler(animatable -> speed);
	}

	/**
	 * Sets the controller's {@link EasingType} override for animations
	 * <p>
	 * By default, the controller will use whatever {@code EasingType} was defined in the animation json
	 *
	 * @param easingTypeFunction The new {@code EasingType} to use
	 * @return this
	 */
	public AnimationController<T> setOverrideEasingType(EasingType easingTypeFunction) {
		return setOverrideEasingTypeFunction(animatable -> easingTypeFunction);
	}

	/**
	 * Sets the controller's {@link EasingType} override function for animations
	 * <p>
	 * By default, the controller will use whatever {@code EasingType} was defined in the animation json
	 *
	 * @param easingType The new {@code EasingType} to use
	 * @return this
	 */
	public AnimationController<T> setOverrideEasingTypeFunction(Function<T, EasingType> easingType) {
		this.overrideEasingTypeFunction = easingType;

		return this;
	}

	/**
	 * Registers a triggerable {@link RawAnimation} with the controller
	 * <p>
	 * These can then be triggered by the various {@code triggerAnim} methods in {@code GeoAnimatable}'s subclasses
	 *
	 * @param name The name of the triggerable animation
	 * @param animation The RawAnimation for this triggerable animation
	 * @return this
	 */
	public AnimationController<T> triggerableAnim(String name, RawAnimation animation) {
		this.triggerableAnimations.put(name, animation);

		return this;
	}

	/**
	 * Tells the AnimationController that you want to receive the {@link AnimationController.AnimationStateHandler} while a triggered animation is playing
	 * <p>
	 * This has no effect if no triggered animation has been registered, or one isn't currently playing
	 * <p>
	 * If a triggered animation is playing, it can be checked in your AnimationStateHandler via {@link #isPlayingTriggeredAnimation()}
	 */
	public AnimationController<T> receiveTriggeredAnimations() {
		this.handlingTriggeredAnimations = true;

		return this;
	}

	/**
	 * Gets the controller's name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the currently loaded {@link Animation}, if present
	 * <p>
	 * An animation returned here does not guarantee it is currently playing, just that it is the currently loaded animation for this controller
	 */
	@Nullable
	public AnimationProcessor.QueuedAnimation getCurrentAnimation() {
		return this.currentAnimation;
	}

	/**
	 * Gets the currently playing {@link RawAnimation triggered animation}, if present
	 */
	@Nullable
	public RawAnimation getTriggeredAnimation() {
		return this.triggeredAnimation;
	}

	/**
	 * Returns the current state of this controller.
	 */
	public State getAnimationState() {
		return this.animationState;
	}

    /**
     * Get the state handler for this controller
     */
    public AnimationStateHandler<T> getStateHandler() {
        return this.stateHandler;
    }

	/**
	 * Gets the currently loaded animation's {@link BoneAnimationQueue BoneAnimationQueues}.
	 */
	public Map<String, BoneAnimationQueue> getBoneAnimationQueues() {
		return this.boneAnimationQueues;
	}

	/**
	 * Gets the current animation speed modifier
	 * <p>
	 * This modifier defines the relative speed in which animations will be played based on the current state of the game
	 *
	 * @return The computed current animation speed modifier
	 */
	public double getAnimationSpeed() {
		return this.animationSpeedModifier.apply(this.animatable);
	}

	/**
	 * Marks the controller as needing to reset its animation and state the next time {@link #setAnimation(RawAnimation)} is called
	 * <p>
	 * Use this if you have a {@link RawAnimation} with multiple stages and you want it to start again from the first stage, or if you want to reset the currently playing animation to the start
	 */
	public void forceAnimationReset() {
		this.needsAnimationReload = true;
	}

	/**
	 * Tells the controller to stop all animations until told otherwise
	 * <p>
	 * Calling this will prevent the controller from continuing to play the currently loaded animation until
	 * either {@link #forceAnimationReset()} is called, or
	 * {@link #setAnimation(RawAnimation)} is called with a different animation
	 */
	public void stop() {
		this.animationState = State.STOPPED;
	}

	/**
	 * Overrides the animation transition time for the controller
	 */
	public AnimationController<T> transitionLength(int ticks) {
		this.transitionLength = ticks;

		return this;
	}

	/**
	 * Checks whether the last animation that was playing on this controller has finished or not
	 * <p>
	 * This will return true if the controller has had an animation set previously, and it has finished playing
	 * and isn't going to loop or proceed to another animation
	 *
	 * @return Whether the previous animation finished or not
	 */
	public boolean hasAnimationFinished() {
		return this.currentRawAnimation != null && this.animationState == State.STOPPED;
	}

	/**
	 * Returns the currently cached {@link RawAnimation}
	 * <p>
	 * This animation may or may not still be playing, but it is the last one to be set in {@link #setAnimation}
	 */
	public RawAnimation getCurrentRawAnimation() {
		return this.currentRawAnimation;
	}

	/**
	 * Returns whether the controller is currently playing a triggered animation registered in
	 * {@link #triggerableAnim}<br>
	 * Used for custom handling if {@link #receiveTriggeredAnimations()} was marked
	 */
	public boolean isPlayingTriggeredAnimation() {
		return this.triggeredAnimation != null && !hasAnimationFinished();
	}

	/**
	 * Sets the currently loaded animation to the one provided
	 * <p>
	 * This method may be safely called every render frame, as passing the same builder that is already loaded will do nothing
	 * <p>
	 * Pass null to this method to tell the controller to stop
	 * <p>
	 * If {@link #forceAnimationReset()} has been called prior to this, the controller will reload the animation regardless of whether it matches the currently loaded one or not
	 */
	public void setAnimation(RawAnimation rawAnimation) {
		if (rawAnimation == null || rawAnimation.getAnimationStages().isEmpty()) {
			stop();

			return;
		}

		if (this.needsAnimationReload || !rawAnimation.equals(this.currentRawAnimation)) {
			if (this.lastModel != null) {
				Queue<AnimationProcessor.QueuedAnimation> animations = this.lastModel.getAnimationProcessor().buildAnimationQueue(this.animatable, rawAnimation);

				if (animations != null) {
					this.animationQueue = animations;
					this.currentRawAnimation = rawAnimation;
					this.shouldResetTick = true;
					this.animationState = State.TRANSITIONING;
					this.justStartedTransition = true;
					this.needsAnimationReload = false;

					return;
				}
			}

			stop();
		}
	}

	/**
	 * Attempt to trigger an animation from the list of {@link #triggerableAnimations triggerable animations} this controller contains
	 *
	 * @param animName The name of the animation to trigger
	 * @return Whether the controller triggered an animation or not
	 */
	public boolean tryTriggerAnimation(String animName) {
		RawAnimation anim = this.triggerableAnimations.get(animName);

		if (anim == null)
			return false;

		this.triggeredAnimation = anim;

		if (this.animationState == State.STOPPED) {
			this.animationState = State.TRANSITIONING;
			this.shouldResetTick = true;
			this.justStartedTransition = true;
		}

		return true;
	}

	/**
	 * Stops and removes a previously triggered animation, effectively ending it immediately.
	 *
	 * @return true if a triggered animation was stopped
	 */
	protected boolean stopTriggeredAnimation() {
		if (this.triggeredAnimation == null)
			return false;

		if (this.currentRawAnimation == this.triggeredAnimation) {
			this.currentAnimation = null;
			this.currentRawAnimation = null;
		}

		this.triggeredAnimation = null;
		this.needsAnimationReload = true;

		return true;
	}

	/**
	 * Handle a given AnimationState, alongside the current triggered animation if applicable
	 */
	protected PlayState handleAnimationState(AnimationState<T> state) {
		if (this.triggeredAnimation != null) {
			if (this.currentRawAnimation != this.triggeredAnimation)
				this.currentAnimation = null;

			setAnimation(this.triggeredAnimation);

			if (!hasAnimationFinished() && (!this.handlingTriggeredAnimations || getStateHandler().handle(state) == PlayState.CONTINUE))
				return PlayState.CONTINUE;

			this.triggeredAnimation = null;
			this.needsAnimationReload = true;
		}

		return getStateHandler().handle(state);
	}

	/**
	 * This method is called every frame in order to populate the animation point
	 * queues, and process animation state logic
	 *
	 * @param model					The model currently being processed
	 * @param state                 The animation test state
	 * @param bones                 The registered {@link GeoBone bones} for this model
	 * @param snapshots             The {@link BoneSnapshot} map
	 * @param seekTime              The current tick + partial tick
	 * @param crashWhenCantFindBone Whether to hard-fail when a bone can't be found, or to continue with the remaining bones
	 */
	public void process(GeoModel<T> model, AnimationState<T> state, Map<String, GeoBone> bones, Map<String, BoneSnapshot> snapshots, final double seekTime, boolean crashWhenCantFindBone) {
		double adjustedTick = adjustTick(seekTime);
		this.lastModel = model;

		if (animationState == State.TRANSITIONING && adjustedTick >= this.transitionLength) {
			this.shouldResetTick = true;
			this.animationState = State.RUNNING;
			adjustedTick = adjustTick(seekTime);
		}

		PlayState playState = handleAnimationState(state);

		if (playState == PlayState.STOP || (this.currentAnimation == null && this.animationQueue.isEmpty())) {
			this.animationState = State.STOPPED;
			this.justStopped = true;

			return;
		}

		createInitialQueues(bones.values());

		if (this.justStartedTransition && (this.shouldResetTick || this.justStopped)) {
			this.justStopped = false;
			adjustedTick = adjustTick(seekTime);

			if (this.currentAnimation == null)
				this.animationState = State.TRANSITIONING;
		}
		else if (this.currentAnimation == null) {
			this.shouldResetTick = true;
			this.animationState = State.TRANSITIONING;
			this.justStartedTransition = true;
			this.needsAnimationReload = false;
			adjustedTick = adjustTick(seekTime);
		}
		else if (this.animationState != State.TRANSITIONING) {
			this.animationState = State.RUNNING;
		}

		if (getAnimationState() == State.RUNNING) {
			processCurrentAnimation(adjustedTick, seekTime, crashWhenCantFindBone);
		}
		else if (this.animationState == State.TRANSITIONING) {
			if (this.lastPollTime != seekTime && (adjustedTick == 0 || this.isJustStarting)) {
				this.justStartedTransition = false;
				this.lastPollTime = seekTime;
				this.currentAnimation = this.animationQueue.poll();

				resetEventKeyFrames();

				if (this.currentAnimation == null)
					return;

				saveSnapshotsForAnimation(this.currentAnimation, snapshots);
			}

			if (this.currentAnimation != null) {
				MathParser.setVariable(MolangQueries.ANIM_TIME, () -> 0);

				for (BoneAnimation boneAnimation : this.currentAnimation.animation().boneAnimations()) {
					BoneAnimationQueue boneAnimationQueue = this.boneAnimationQueues.get(boneAnimation.boneName());
					BoneSnapshot boneSnapshot = this.boneSnapshots.get(boneAnimation.boneName());
					GeoBone bone = bones.get(boneAnimation.boneName());

					if (boneSnapshot == null)
						continue;

					if (bone == null) {
						if (crashWhenCantFindBone)
							throw new RuntimeException("Could not find bone: " + boneAnimation.boneName());

						continue;
					}

					KeyframeStack<Keyframe<MathValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames();
					KeyframeStack<Keyframe<MathValue>> positionKeyFrames = boneAnimation.positionKeyFrames();
					KeyframeStack<Keyframe<MathValue>> scaleKeyFrames = boneAnimation.scaleKeyFrames();

					if (!rotationKeyFrames.xKeyframes().isEmpty()) {
						boneAnimationQueue.addNextRotation(null, adjustedTick, this.transitionLength, boneSnapshot, bone.getInitialSnapshot(),
								getAnimationPointAtTick(rotationKeyFrames.xKeyframes(), 0, true, Axis.X),
								getAnimationPointAtTick(rotationKeyFrames.yKeyframes(), 0, true, Axis.Y),
								getAnimationPointAtTick(rotationKeyFrames.zKeyframes(), 0, true, Axis.Z));
					}

					if (!positionKeyFrames.xKeyframes().isEmpty()) {
						boneAnimationQueue.addNextPosition(null, adjustedTick, this.transitionLength, boneSnapshot,
								getAnimationPointAtTick(positionKeyFrames.xKeyframes(), 0, false, Axis.X),
								getAnimationPointAtTick(positionKeyFrames.yKeyframes(), 0, false, Axis.Y),
								getAnimationPointAtTick(positionKeyFrames.zKeyframes(), 0, false, Axis.Z));
					}

					if (!scaleKeyFrames.xKeyframes().isEmpty()) {
						boneAnimationQueue.addNextScale(null, adjustedTick, this.transitionLength, boneSnapshot,
								getAnimationPointAtTick(scaleKeyFrames.xKeyframes(), 0, false, Axis.X),
								getAnimationPointAtTick(scaleKeyFrames.yKeyframes(), 0, false, Axis.Y),
								getAnimationPointAtTick(scaleKeyFrames.zKeyframes(), 0, false, Axis.Z));
					}
				}
			}
		}
	}

	/**
	 * Handle the current animation's state modifications and translations
	 *
	 * @param adjustedTick The controller-adjusted tick for animation purposes
	 * @param seekTime The lerped tick (current tick + partial tick)
	 * @param crashWhenCantFindBone Whether the controller should throw an exception when unable to find the required bone, or continue with the remaining bones
	 */
	private void processCurrentAnimation(double adjustedTick, double seekTime, boolean crashWhenCantFindBone) {
		if (adjustedTick >= this.currentAnimation.animation().length()) {
			if (this.currentAnimation.loopType().shouldPlayAgain(this.animatable, this, this.currentAnimation.animation())) {
				if (this.animationState != State.PAUSED) {
					this.shouldResetTick = true;

					adjustedTick = adjustTick(seekTime);
					resetEventKeyFrames();
				}
			}
			else {
				AnimationProcessor.QueuedAnimation nextAnimation = this.animationQueue.peek();

				resetEventKeyFrames();

				if (nextAnimation == null) {
					this.animationState = State.STOPPED;

					return;
				}
				else {
					this.animationState = State.TRANSITIONING;
					this.shouldResetTick = true;
					adjustedTick = adjustTick(seekTime);
					this.currentAnimation = this.animationQueue.poll();
				}
			}
		}

		final double finalAdjustedTick = adjustedTick;

		MathParser.setVariable(MolangQueries.ANIM_TIME, () -> finalAdjustedTick / 20d);

		for (BoneAnimation boneAnimation : this.currentAnimation.animation().boneAnimations()) {
			BoneAnimationQueue boneAnimationQueue = this.boneAnimationQueues.get(boneAnimation.boneName());

			if (boneAnimationQueue == null) {
				if (crashWhenCantFindBone)
					throw new RuntimeException("Could not find bone: " + boneAnimation.boneName());

				continue;
			}

			KeyframeStack<Keyframe<MathValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames();
			KeyframeStack<Keyframe<MathValue>> positionKeyFrames = boneAnimation.positionKeyFrames();
			KeyframeStack<Keyframe<MathValue>> scaleKeyFrames = boneAnimation.scaleKeyFrames();

			if (!rotationKeyFrames.xKeyframes().isEmpty()) {
				boneAnimationQueue.addRotations(
						getAnimationPointAtTick(rotationKeyFrames.xKeyframes(), adjustedTick, true, Axis.X),
						getAnimationPointAtTick(rotationKeyFrames.yKeyframes(), adjustedTick, true, Axis.Y),
						getAnimationPointAtTick(rotationKeyFrames.zKeyframes(), adjustedTick, true, Axis.Z));
			}

			if (!positionKeyFrames.xKeyframes().isEmpty()) {
				boneAnimationQueue.addPositions(
						getAnimationPointAtTick(positionKeyFrames.xKeyframes(), adjustedTick, false, Axis.X),
						getAnimationPointAtTick(positionKeyFrames.yKeyframes(), adjustedTick, false, Axis.Y),
						getAnimationPointAtTick(positionKeyFrames.zKeyframes(), adjustedTick, false, Axis.Z));
			}

			if (!scaleKeyFrames.xKeyframes().isEmpty()) {
				boneAnimationQueue.addScales(
						getAnimationPointAtTick(scaleKeyFrames.xKeyframes(), adjustedTick, false, Axis.X),
						getAnimationPointAtTick(scaleKeyFrames.yKeyframes(), adjustedTick, false, Axis.Y),
						getAnimationPointAtTick(scaleKeyFrames.zKeyframes(), adjustedTick, false, Axis.Z));
			}
		}

		adjustedTick += this.transitionLength;

		for (SoundKeyframeData keyframeData : this.currentAnimation.animation().keyFrames().sounds()) {
			if (adjustedTick >= keyframeData.getStartTick() && this.executedKeyFrames.add(keyframeData)) {
				if (this.soundKeyframeHandler == null) {
					GeckoLibConstants.LOGGER.log(Level.WARN, "Sound Keyframe found for " + this.animatable.getClass().getSimpleName() + " -> " + getName() + ", but no keyframe handler registered");

					break;
				}

				this.soundKeyframeHandler.handle(new SoundKeyframeEvent<>(this.animatable, adjustedTick, this, keyframeData));
			}
		}

		for (ParticleKeyframeData keyframeData : this.currentAnimation.animation().keyFrames().particles()) {
			if (adjustedTick >= keyframeData.getStartTick() && this.executedKeyFrames.add(keyframeData)) {
				if (this.particleKeyframeHandler == null) {
					GeckoLibConstants.LOGGER.log(Level.WARN, "Particle Keyframe found for " + this.animatable.getClass().getSimpleName() + " -> " + getName() + ", but no keyframe handler registered");

					break;
				}

				this.particleKeyframeHandler.handle(new ParticleKeyframeEvent<>(this.animatable, adjustedTick, this, keyframeData));
			}
		}

		for (CustomInstructionKeyframeData keyframeData : this.currentAnimation.animation().keyFrames().customInstructions()) {
			if (adjustedTick >= keyframeData.getStartTick() && this.executedKeyFrames.add(keyframeData)) {
				if (this.customKeyframeHandler == null) {
					GeckoLibConstants.LOGGER.log(Level.WARN, "Custom Instruction Keyframe found for " + this.animatable.getClass().getSimpleName() + " -> " + getName() + ", but no keyframe handler registered");

					break;
				}

				this.customKeyframeHandler.handle(new CustomInstructionKeyframeEvent<>(this.animatable, adjustedTick, this, keyframeData));
			}
		}

		if (this.transitionLength == 0 && this.shouldResetTick && this.animationState == State.TRANSITIONING)
			this.currentAnimation = this.animationQueue.poll();
	}

	/**
	 * Prepare the {@link BoneAnimationQueue} map for the current render frame
	 *
	 * @param modelRendererList The bone list from the {@link AnimationProcessor}
	 */
	private void createInitialQueues(Collection<GeoBone> modelRendererList) {
		this.boneAnimationQueues.clear();

		for (GeoBone modelRenderer : modelRendererList) {
			this.boneAnimationQueues.put(modelRenderer.getName(), new BoneAnimationQueue(modelRenderer));
		}
	}

	/**
	 * Cache the relevant {@link BoneSnapshot BoneSnapshots} for the current {@link AnimationProcessor.QueuedAnimation}
	 * for animation lerping
	 *
	 * @param animation The {@code QueuedAnimation} to filter {@code BoneSnapshots} for
	 * @param snapshots The master snapshot collection to pull filter from
	 */
	private void saveSnapshotsForAnimation(AnimationProcessor.QueuedAnimation animation, Map<String, BoneSnapshot> snapshots) {
		for (BoneSnapshot snapshot : snapshots.values()) {
			if (animation.animation().boneAnimations() != null) {
				for (BoneAnimation boneAnimation : animation.animation().boneAnimations()) {
					if (boneAnimation.boneName().equals(snapshot.getBone().getName())) {
						this.boneSnapshots.put(boneAnimation.boneName(), BoneSnapshot.copy(snapshot));

						break;
					}
				}
			}
		}
	}

	/**
	 * Adjust a tick value depending on the controller's current state and speed modifier
	 * <p>
	 * Is used when starting a new animation, transitioning, and a few other key areas
	 *
	 * @param tick The currently used tick value
	 * @return 0 if {@link #shouldResetTick} is set to false, or a {@link #animationSpeedModifier} modified value otherwise
	 */
	protected double adjustTick(double tick) {
		if (!this.shouldResetTick)
			return this.animationSpeedModifier.apply(this.animatable) * Math.max(tick - this.tickOffset, 0);

		if (getAnimationState() != State.STOPPED)
			this.tickOffset = tick;

		this.shouldResetTick = false;

		return 0;
	}

	/**
	 * Convert a {@link KeyframeLocation} to an {@link AnimationPoint}
	 */
	private AnimationPoint getAnimationPointAtTick(List<Keyframe<MathValue>> frames, double tick, boolean isRotation,
												   Axis axis) {
		KeyframeLocation<Keyframe<MathValue>> location = getCurrentKeyFrameLocation(frames, tick);
		Keyframe<MathValue> currentFrame = location.keyframe();
		double startValue = currentFrame.startValue().get();
		double endValue = currentFrame.endValue().get();

		if (isRotation) {
			if (!(currentFrame.startValue() instanceof Constant)) {
				startValue = Math.toRadians(startValue);

				if (axis == Axis.X || axis == Axis.Y)
					startValue *= -1;
			}

			if (!(currentFrame.endValue() instanceof Constant)) {
				endValue = Math.toRadians(endValue);

				if (axis == Axis.X || axis == Axis.Y)
					endValue *= -1;
			}
		}

		return new AnimationPoint(currentFrame, location.startTick(), currentFrame.length(), startValue, endValue);
	}

	/**
	 * Returns the {@link Keyframe} relevant to the current tick time
	 *
	 * @param frames The list of {@code KeyFrames} to filter through
	 * @param ageInTicks The current tick time
	 * @return A new {@code KeyFrameLocation} containing the current {@code KeyFrame} and the tick time used to find it
	 */
	private KeyframeLocation<Keyframe<MathValue>> getCurrentKeyFrameLocation(List<Keyframe<MathValue>> frames,
																			 double ageInTicks) {
		double totalFrameTime = 0;

		for (Keyframe<MathValue> frame : frames) {
			totalFrameTime += frame.length();

			if (totalFrameTime > ageInTicks)
				return new KeyframeLocation<>(frame, (ageInTicks - (totalFrameTime - frame.length())));
		}

		return new KeyframeLocation<>(frames.getLast(), ageInTicks);
	}

	/**
	 * Clear the {@link KeyFrameData} cache in preparation for the next animation
	 */
	private void resetEventKeyFrames() {
		this.executedKeyFrames.clear();
	}

	/**
	 * Every render frame, the {@code AnimationController} will call this handler for <u>each</u> animatable that is being rendered
	 * <p>
	 * This handler defines which animation should be currently playing, and returning a {@link PlayState} to tell the controller what to do next
	 * <p>
	 * Example Usage:
	 * <pre>{@code
	 * AnimationFrameHandler myIdleWalkHandler = state -> {
	 *	if (state.isMoving()) {
	 *		state.getController().setAnimation(myWalkAnimation);
	 *	}
	 *	else {
	 *		state.getController().setAnimation(myIdleAnimation);
	 *	}
	 *
	 *	return PlayState.CONTINUE;
	 *};}</pre>
	 */
	@FunctionalInterface
	public interface AnimationStateHandler<A extends GeoAnimatable> {
		/**
		 * The handling method, called each frame
		 * <p>
		 * Return {@link PlayState#CONTINUE} to tell the controller to continue animating,
		 * or return {@link PlayState#STOP} to tell it to stop playing all animations and wait for the next {@link PlayState#CONTINUE} return.
		 */
		PlayState handle(AnimationState<A> state);
	}

	/**
	 * A handler for when a predefined sound keyframe is hit
	 * <p>
	 * When the keyframe is encountered, the {@link SoundKeyframeHandler#handle(SoundKeyframeEvent)} method will be called.
	 * Play the sound(s) of your choice at this time.
	 */
	@FunctionalInterface
	public interface SoundKeyframeHandler<A extends GeoAnimatable> {
		void handle(SoundKeyframeEvent<A> event);
	}

	/**
	 * A handler for when a predefined particle keyframe is hit
	 * <p>
	 * When the keyframe is encountered, the {@link ParticleKeyframeHandler#handle(ParticleKeyframeEvent)} method will be called.
	 * Spawn the particles/effects of your choice at this time.
	 */
	@FunctionalInterface
	public interface ParticleKeyframeHandler<A extends GeoAnimatable> {
		void handle(ParticleKeyframeEvent<A> event);
	}

	/**
	 * A handler for pre-defined custom instruction keyframes
	 * <p>
	 * When the keyframe is encountered, the {@link CustomKeyframeHandler#handle(CustomInstructionKeyframeEvent)} method will be called.
	 * You can then take whatever action you want at this point.
	 */
	@FunctionalInterface
	public interface CustomKeyframeHandler<A extends GeoAnimatable> {
		void handle(CustomInstructionKeyframeEvent<A> event);
	}

	public enum State {
		RUNNING,
		TRANSITIONING,
		PAUSED,
		STOPPED
	}
}