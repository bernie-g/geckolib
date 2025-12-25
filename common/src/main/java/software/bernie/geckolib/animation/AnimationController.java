package software.bernie.geckolib.animation;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.util.InternalApi;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.object.EasingType;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.*;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.cache.animation.keyframeevent.CustomInstructionKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.KeyFrameData;
import software.bernie.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.SoundKeyframeData;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.MiscUtil;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Handles the state of an animation for a given {@link GeoAnimatable}.
 * <p>
 * This is what functionally stops/starts/plays animations for your animatable.
 *
 * @param <T> The {@link GeoAnimatable} type this controller belongs to
 */
@SuppressWarnings("UnusedReturnValue")
public class AnimationController<T extends GeoAnimatable> {
    protected final String name;
    protected final AnimationStateHandler<T> stateHandler;
    protected final Supplier<Map<String, RawAnimation>> triggerableAnimations = Suppliers.memoize(Object2ObjectOpenHashMap::new);

    protected @Nullable KeyframeEventHandler<T, SoundKeyframeData> soundKeyframeHandler = null;
    protected @Nullable KeyframeEventHandler<T, ParticleKeyframeData> particleKeyframeHandler = null;
    protected @Nullable KeyframeEventHandler<T, CustomInstructionKeyframeData> customKeyframeHandler = null;

    protected boolean additiveAnimations = false;
    protected int transitionTicks = 0;
    protected double animationSpeed = 1;
    protected boolean handlesTriggeredAnimations = false;
    protected @Nullable EasingType easingOverride = null;

    protected @Nullable AnimationTimeline timeline = null;
    protected PlayState playState = PlayState.STOP;
    protected @Nullable RawAnimation currentRawAnimation = null;
    protected @Nullable AnimationPoint transitionFromPoint = null;
    protected @Nullable AnimationPoint animationPoint = null;
    protected double timelineTime = NOT_ANIMATING;
    protected double lastAnimatableAge = 0;
    protected double triggeredAnimTime = NOT_TRIGGERED;

    /**
     * Instantiates a new {@code AnimationController} with a default name
     * <p>
     * This constructor assumes a 0-tick transition length between animations
     *
     * @param stateHandler The animation state handler responsible for deciding which animations to play
     */
    public AnimationController(AnimationStateHandler<T> stateHandler) {
        this("Default", 0, stateHandler);
    }

    /**
     * Instantiates a new {@code AnimationController}
     * <p>
     * This constructor assumes a 0-tick transition length between animations
     *
     * @param name The name of the controller - should represent what animations it handles
     * @param stateHandler The animation state handler responsible for deciding which animations to play
     */
    public AnimationController(String name, AnimationStateHandler<T> stateHandler) {
        this(name, 0, stateHandler);
    }

    /**
     * Instantiates a new {@code AnimationController}
     *
     * @param name The name of the controller - should represent what animations it handles
     * @param transitionTicks The amount of time (in <b>ticks</b>) that the controller should take to transition between animations.
     *                        Lerping is automatically applied where possible
     * @param stateHandler The animation state handler responsible for deciding which animations to play
     */
    public AnimationController(String name, int transitionTicks, AnimationStateHandler<T> stateHandler) {
        this.name = name;
        this.stateHandler = stateHandler;
        this.transitionTicks = transitionTicks;
    }

    /**
     * @return The name of the controller
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The animation state of the controller
     */
    public PlayState getPlayState() {
        return this.playState;
    }

    /**
     * @return The time (in ticks) that the controller should take to transition between animations
     */
    public int getTransitionTicks() {
        return this.transitionTicks;
    }

    /**
     * @return The speed multiplier for animations on this controller
     */
    public double getAnimationSpeed() {
        return this.animationSpeed;
    }

    /**
     * @return The current {@code AnimationTimeline} instance for the {@link #currentRawAnimation} playing, or null if no animation is playing
     */
    public @Nullable AnimationTimeline getTimeline() {
        return this.timeline;
    }

    /**
     * @return The current position (in seconds) on this controller's animation timeline,
     * or {@link #NOT_ANIMATING} if no animation is playing,
     * or {@link #FINISHED_ANIMATING} if the animation has finished playing
     */
    public double getCurrentTimelineTime() {
        return this.timelineTime;
    }

    /**
     * @return The current position (in seconds) on the current animation, or 0 if not currently playing an animation
     */
    public double getCurrentAnimationTime() {
        return this.animationPoint == null ? 0 : this.animationPoint.animTime();
    }

    /**
     * @return The current animation marker this controller is playing
     */
    public @Nullable AnimationPoint getCurrentAnimationPoint() {
        return this.animationPoint;
    }

    /**
     * @return The current unprocessed animation this controller should be playing
     */

    public @Nullable RawAnimation getCurrentRawAnimation() {
        return this.currentRawAnimation;
    }

    /**
     * Returns whether the controller is currently playing a triggered animation registered in
     * {@link #triggerableAnim}<br>
     * Used for custom handling if {@link #receiveTriggeredAnimations()} was marked
     */
    public boolean isPlayingTriggeredAnimation() {
        return this.triggeredAnimTime >= 0 && isAnimatingBones();
    }

    /**
     * Checks whether the last animation playing on this controller has finished or not
     * <p>
     * This will return true if the controller has had an animation set previously, and it has finished playing
     * and isn't going to loop or proceed to another animation
     *
     * @return Whether the previous animation finished or not
     */
    public boolean hasAnimationFinished() {
        return this.animationPoint != null && this.animationPoint.hasFinished() && this.timeline != null && this.timelineTime >= this.timeline.lastAnimationEndTime();
    }

    /**
     * Similar to {@link #hasAnimationFinished()}, but also includes the reset transition time
     * <p>
     * If this method returns false, this controller is performing no actions during the render pass
     */
    public boolean isAnimatingBones() {
        return this.animationPoint != null && this.timeline != null && this.timelineTime >= 0;
    }

    /**
     * Returns whether the controller is currently transitioning into, between, or out of an animation
     */
    public boolean isTransitioning() {
        return this.timeline != null && this.timelineTime >= 0 && this.timeline.getStage(this.timelineTime).isTransition();
    }

    /**
     * Adds the provided {@link KeyframeEventHandler} to this controller, for handling {@link SoundKeyframeData} keyframe events
     *
     * @return this
     */
    public AnimationController<T> setSoundKeyframeHandler(KeyframeEventHandler<T, SoundKeyframeData> soundHandler) {
        this.soundKeyframeHandler = soundHandler;

        return this;
    }

    /**
     * Adds the provided {@link KeyframeEventHandler} to this controller, for handling {@link ParticleKeyframeData} keyframe events
     *
     * @return this
     */
    public AnimationController<T> setParticleKeyframeHandler(KeyframeEventHandler<T, ParticleKeyframeData> particleHandler) {
        this.particleKeyframeHandler = particleHandler;

        return this;
    }

    /**
     * Adds the provided {@link KeyframeEventHandler} to this controller, for handling {@link CustomInstructionKeyframeData} keyframe events
     *
     * @return this
     */
    public AnimationController<T> setCustomInstructionKeyframeHandler(KeyframeEventHandler<T, CustomInstructionKeyframeData> customInstructionHandler) {
        this.customKeyframeHandler = customInstructionHandler;

        return this;
    }

    /**
     * Applies the given modifier value to this controller, for handling the speed that the controller should play its animations at
     * <p>
     * A value of 1 is considered neutral, with 2 playing an animation twice as fast, 0.5 playing half as fast, etc.
     *
     * @param speed The speed modifier to apply to this controller to handle animation speed.
     * @return this
     */
    public AnimationController<T> setAnimationSpeed(double speed) {
        this.animationSpeed = speed;

        return this;
    }

    /**
     * Sets a transition time (in ticks) for this controller.
     * <p>
     * This value is used for animation interpolation into, out of, and between animations
     * <p>
     * <b><u>NOTE:</u></b> This value does not take effect until a new animation is set via {@link #setAnimation(RawAnimation)}
     */
    public AnimationController<T> setTransitionTicks(int ticks) {
        this.transitionTicks = ticks;

        return this;
    }

    /**
     * Sets the controller's {@link EasingType} override for animations, ignoring the json-based easing preference
     * <p>
     * By default, the controller will use whatever {@code EasingType} was defined in the animation .json
     *
     * @param easingType The new {@code EasingType} to use
     * @return this
     */
    public AnimationController<T> setOverrideEasingType(EasingType easingType) {
        this.easingOverride = easingType;

        return this;
    }

    /**
     * Mark this controller as <i>additive</i><br>
     * This means that animations will add onto the existing animation value that bones already have, rather than replacing them outright
     */
    public AnimationController<T> additiveAnimations() {
        this.additiveAnimations = true;

        return this;
    }

    /**
     * Tells the AnimationController that you want to receive the {@link AnimationStateHandler} while a triggered animation is playing
     * <p>
     * This has no effect if no triggered animation has been registered, or one isn't currently playing
     * <p>
     * If a triggered animation is playing, it can be checked in your AnimationStateHandler via {@link #isPlayingTriggeredAnimation()}
     */
    public AnimationController<T> receiveTriggeredAnimations() {
        this.handlesTriggeredAnimations = true;

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
        this.triggerableAnimations.get().put(name, animation);

        return this;
    }

    /**
     * Sets this controller's active animation queue as the one provided
     * <p>
     * Can be safely called repeatedly with the same animation, as duplicate calls are ignored and continue playing the original animation as expected
     */
    @SuppressWarnings("ConstantValue")
    public void setAnimation(RawAnimation rawAnimation) {
        if (rawAnimation == null || rawAnimation.getStageCount() == 0) {
            GeckoLibConstants.LOGGER.warn("Tried to set an empty or null animation on controller {}!", this.name);

            return;
        }

        if (rawAnimation.equals(this.currentRawAnimation))
            return;

        this.currentRawAnimation = rawAnimation;
        this.transitionFromPoint = this.animationPoint;
        this.animationPoint = null;
        this.triggeredAnimTime = NOT_TRIGGERED;
    }

    /**
     * Attempt to trigger an animation from the list of {@link #triggerableAnimations} this controller contains
     * <p>
     * Only animations registered as triggerable via {@link #triggerableAnim(String, RawAnimation)} can be triggered
     *
     * @param animName The name of the animation to trigger
     * @return Whether the controller triggered an animation or not
     */
    public boolean triggerAnimation(String animName) {
        RawAnimation animation = this.triggerableAnimations.get().get(animName);

        if (animation == null)
            return false;

        this.currentRawAnimation = animation;
        this.transitionFromPoint = this.animationPoint;
        this.triggeredAnimTime = ClientUtil.getCurrentTick();
        this.animationPoint = null;
        this.playState = PlayState.CONTINUE;

        return true;
    }

    /**
     * Set the current position of the {@link Animation} currently playing
     * <p>
     * Has no effect if no animation is currently playing, or if another animation is started afterward
     */
    public void setAnimationTime(double animTime) {
        if (animTime < 0)
            throw new IllegalArgumentException("Attempting to set a negative animation time (" + animTime + ") on controller " + this.name + "?");

        if (this.animationPoint == null || this.timeline == null || this.timelineTime < 0) {
            this.timelineTime = animTime + this.transitionTicks / 20f;
        }
        else {
            AnimationTimeline.Stage stage = this.timeline.getAnimationStage(this.timelineTime);

            if (stage.animation() != this.animationPoint.animation())
                stage = this.timeline.getAnimationStage(0);

            this.timelineTime = Math.min(stage.startTime() + animTime, stage.endTime());
        }
    }

    /**
     * Set the current position on the overall timeline
     * <p>
     * Has no effect if no animation is currently playing, or if another animation is started afterward
     */
    public void setTimelineTime(double timelineTime) {
        this.timelineTime = timelineTime;
    }

    /**
     * Stops and removes a previously {@link #triggerAnimation(String) triggered} animation, effectively ending it immediately
     *
     * @return true if a triggered animation was stopped
     */
    public boolean stopTriggeredAnimation() {
        if (this.triggeredAnimTime == NOT_TRIGGERED)
            return false;

        this.triggeredAnimTime = NOT_TRIGGERED;

        if (this.timeline == null || this.animationPoint == null || this.currentRawAnimation == null) {
            reset();
        }
        else {
            this.timelineTime = this.timeline.lastAnimationEndTime();
        }

        return true;
    }

    /**
     * Tells the controller to stop its animation and reset back to a blank state
     */
    public void reset() {
        this.playState = PlayState.STOP;
        this.transitionFromPoint = null;
        this.animationPoint = null;
        this.timeline = null;
        this.currentRawAnimation = null;
        this.timelineTime = 0;
        this.triggeredAnimTime = NOT_TRIGGERED;
    }

    /**
     * Every render frame, the {@code AnimationController} will call this handler for <u>each</u> animatable that is being rendered
     * <p>
     * This handler defines which animation should be currently playing, and returning a {@link PlayState} to tell the controller what to do next
     * <p>
     * Example Usage:
     * <pre>{@code
     * AnimationStateHandler myIdleWalkHandler = test -> {
     *	if (test.isMoving()) {
     *		return test.setAndContinue(myWalkAnimation);
     *	}
     *	else {
     *		return test.setAndContinue(myIdleAnimation);
     *	}
     *};}</pre>
     *
     * @param <A> Animatable class type
     */
    @FunctionalInterface
    public interface AnimationStateHandler<A extends GeoAnimatable> {
        /**
         * The handling method, called before each render frame
         * <table>
         *     <caption>Return Values</caption>
         *     <tr><th>Return Value</th><th>Effect</th></tr>
         *     <tr><td>{@link PlayState#CONTINUE}</td><td>Start or continue animating</td></tr>
         *     <tr><td>{@link PlayState#PAUSE}</td><td>Continue animating, freezing at the current animation progress</td></tr>
         *     <tr><td>{@link PlayState#STOP}</td><td>Stop animating, resetting for the next animation</td></tr>
         * </table>
         */
        PlayState handle(AnimationTest<A> animatable);
    }
    /**
     * A handler for keyframe instructions defined in the animation .json
     * <p>
     * When the keyframe is encountered, the {@link KeyframeEventHandler#handle(KeyFrameEvent)} method will be called.
     * You can then take whatever action you want at this point.
     *
     * @param <A> Animatable class type
     * @param <E> Keyframe data class type
     */
    @FunctionalInterface
    public interface KeyframeEventHandler<A extends GeoAnimatable, E extends KeyFrameData> {
        void handle(KeyFrameEvent<A, E> event);
    }

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">
    protected static final int NOT_TRIGGERED = -1;
    protected static final int NOT_ANIMATING = -1;
    protected static final int FINISHED_ANIMATING = -2;
    /**
     * Extract the necessary data for this controller to play its animations during an upcoming render pass.
     * <p>
     * If the controller has no animation handling for this render pass, returns null.
     */
    @InternalApi
    public @Nullable ControllerState extractControllerState(T animatable, GeoRenderState renderState, AnimatableManager<T> manager, MolangQueries.Actor<T> actor, GeoModel<T> geoModel) {
        if (!checkControllerState(animatable, renderState, manager, geoModel))
            return null;

        final Reference2DoubleMap<Variable> queryValues = new Reference2DoubleOpenHashMap<>();

        MolangQueries.buildActorVariables(actor, getUsedVariables(), queryValues);

        //noinspection DataFlowIssue
        return new ControllerState(this.animationPoint, this.transitionFromPoint, this.timeline.getTransitionTime(this.timelineTime), this.timeline.getTransitionLength(), this.additiveAnimations, this.easingOverride, renderState, queryValues);
    }

    /**
     * Check the current state of the controller, including querying the {@link AnimationStateHandler}
     * <p>
     * This is the primary 'logic' point of the controller
     *
     * @return true if the controller is actively animating, false if it should do nothing
     */
    @InternalApi
    protected boolean checkControllerState(T animatable, GeoRenderState renderState, AnimatableManager<T> manager, GeoModel<T> geoModel) {
        final boolean wasStopped = this.playState == PlayState.STOP;
        final int transitionTicks = this.transitionTicks;
        final RawAnimation prevRawAnimation = this.currentRawAnimation;
        final double prevAnimationSpeed = this.animationSpeed;
        final double prevTimelineTime = this.timelineTime;
        final double timeDelta = this.playState == PlayState.PAUSE ? 0 : (renderState.getAnimatableAge() - this.lastAnimatableAge) / 20f * prevAnimationSpeed;
        this.timelineTime = this.timeline == null ? NOT_ANIMATING : this.timelineTime < 0 ? this.timelineTime : Mth.clamp(this.timelineTime + timeDelta, 0, this.timeline.totalTime());
        this.lastAnimatableAge = renderState.getAnimatableAge();

        if (this.triggeredAnimTime == NOT_TRIGGERED || this.handlesTriggeredAnimations)
            this.playState = this.stateHandler.handle(new AnimationTest<>(animatable, renderState, manager, this));

        if (this.playState != PlayState.STOP) {
            if (this.animationPoint == null || !Objects.equals(prevRawAnimation, this.currentRawAnimation)) {
                initializeNewAnimation(animatable, renderState, geoModel, prevAnimationSpeed, transitionTicks);
            }
            else if (this.timelineTime >= 0 || ((timeDelta < 0) == (this.timelineTime == FINISHED_ANIMATING))) {
                progressExistingAnimation(animatable, renderState, prevTimelineTime, timeDelta);
            }
        }
        else if (!wasStopped) {
            this.timelineTime = this.timeline == null ? NOT_ANIMATING : this.timeline.lastAnimationEndTime();
        }
        else if (this.animationPoint != null && this.timeline != null && (this.timelineTime >= 0 || ((timeDelta < 0) == (this.timelineTime == FINISHED_ANIMATING)))) {
            progressExistingAnimation(animatable, renderState, prevTimelineTime, timeDelta);
        }

        return isAnimatingBones();
    }

    /**
     * Attempt to initialize the animation state of a new animation for this controller, assuming an animation has been set to play
     * <p>
     * Establishes the {@link AnimationTimeline} and {@link AnimationPoint} instances for animating
     */
    @InternalApi
    protected void initializeNewAnimation(T animatable, GeoRenderState renderState, GeoModel<T> geoModel, double prevAnimSpeed, int prevTransitionTicks) {
        if (this.currentRawAnimation == null)
            return;

        double startTimeOffset = this.triggeredAnimTime >= 0 ? (ClientUtil.getCurrentTick() - this.triggeredAnimTime) * prevAnimSpeed : 0;
        this.timeline = AnimationTimeline.create(this.currentRawAnimation, animatable, geoModel, this.triggeredAnimTime > 0 ? prevTransitionTicks : this.transitionTicks);

        if (this.timeline == null)
            return;

        this.timelineTime = startTimeOffset;
        this.animationPoint = this.timeline.createAnimationPoint(this.timelineTime, this.animationPoint, this.easingOverride);

        if (startTimeOffset > 0)
            this.timeline.triggerKeyframeMarkersBetween(animatable, renderState, 0, startTimeOffset, this,
                                                        this.soundKeyframeHandler, this.particleKeyframeHandler, this.customKeyframeHandler);

        validateKeyframeListeners(animatable);
    }

    /**
     * Update the {@link AnimationPoint}, trigger animation keyframe markers, and any other maintenance required when traversing the timeline
     * <p>
     * This method assumes relevant checks have already been performed to ensure that traversal should occur
     */
    @InternalApi
    protected void progressExistingAnimation(T animatable, GeoRenderState renderState, double prevTimelineTime, double timeAdvanced) {
        if (timeAdvanced == 0 || this.animationPoint == null || this.timeline == null)
            return;

        boolean isBacktracking = timeAdvanced < 0;

        if (this.timelineTime < 0 && (isBacktracking) == (this.timelineTime == FINISHED_ANIMATING))
            this.timelineTime = isBacktracking ? this.timeline.totalTime() : 0;

        AnimationTimeline.Stage prevAnimStage = this.timeline.getAnimationStage(prevTimelineTime);
        AnimationTimeline.Stage currentAnimStage = this.timeline.getAnimationStage(this.timelineTime);

        if (prevAnimStage != currentAnimStage || MiscUtil.areFloatsEqual(currentAnimStage.endTime(), this.timelineTime)) {
            if (!isBacktracking && this.animationPoint.animation().loopType().shouldKeepPlaying(animatable, this.animationPoint, prevAnimStage, renderState, this)) {
                this.timeline.triggerKeyframeMarkersBetween(animatable, renderState, prevTimelineTime, prevAnimStage.endTime(), this,
                                                            this.soundKeyframeHandler, this.particleKeyframeHandler, this.customKeyframeHandler);

                this.animationPoint = this.timeline.createAnimationPoint(this.timelineTime, this.animationPoint, this.easingOverride);

                return;
                // TODO evaluate this vs lerping overtime value
                //this.timeline.triggerKeyframeMarkersBetween(animatable, renderState, prevAnimStage.startTime(), this.timelineTime, this,
                //                                            this.soundKeyframeHandler, this.particleKeyframeHandler, this.customKeyframeHandler);
            }
            else {
                this.transitionFromPoint = prevTimelineTime < this.timelineTime ?
                                           this.animationPoint :
                                           this.timeline.createAnimationPoint(currentAnimStage.endTime(), null, this.easingOverride);
            }
        }

        this.timeline.triggerKeyframeMarkersBetween(animatable, renderState, prevTimelineTime, this.timelineTime, this,
                                                    this.soundKeyframeHandler, this.particleKeyframeHandler, this.customKeyframeHandler);

        this.animationPoint = this.timeline.createAnimationPoint(this.timelineTime, this.animationPoint, this.easingOverride);

        if (timeAdvanced > 0 ? this.timelineTime >= this.timeline.totalTime() : this.timelineTime == 0)
            this.timelineTime = isBacktracking ? NOT_ANIMATING : FINISHED_ANIMATING;
    }

    /**
     * Compile a set of the {@link Variable}s that this controller is or could be using for the upcoming render pass
     */
    @InternalApi
    public Set<Variable> getUsedVariables() {
        if (this.animationPoint == null)
            return Set.of();

        Set<Variable> usedVariables = new ReferenceArraySet<>(this.animationPoint.animation().usedVariables());

        if (this.transitionFromPoint != null)
            usedVariables.addAll(this.transitionFromPoint.animation().usedVariables());

        return usedVariables;
    }

    /**
     * Returns whether the given {@link #triggerableAnim(String, RawAnimation) registered triggerable animation} is currently triggered
     * on this controller.
     *
     * @param animName The name matching the name the triggered animation was registered with
     */
    @InternalApi
    public boolean isTriggeredAnimation(String animName) {
        return this.currentRawAnimation != null && this.currentRawAnimation.equals(this.triggerableAnimations.get().get(animName));
    }

    /**
     * Validates the existence of keyframe handlers on this controller when finding keyframe markers on a given animation
     */
    @InternalApi
    private void validateKeyframeListeners(T animatable) {
        if (this.timeline == null)
            return;

        for (AnimationTimeline.Stage stage : this.timeline.stages()) {
            if (stage.animation() != null) {
                final Animation animation = stage.animation();
                final Animation.KeyframeMarkers markers = animation.keyframeMarkers();

                if (markers.customInstructions().length > 0 && this.customKeyframeHandler == null)
                    GeckoLibConstants.LOGGER.warn("AnimationController {} for {} loaded animation {} with custom instruction keyframe markers, but no custom instruction handler has been set!",
                                                  this.name, animatable.getClass().getName(), animation.name());

                if (markers.sounds().length > 0 && this.soundKeyframeHandler == null)
                    GeckoLibConstants.LOGGER.warn("AnimationController {} for {} loaded animation {} with sound instruction keyframe markers, but no sound instruction handler has been set!",
                                                  this.name, animatable.getClass().getName(), animation.name());

                if (markers.particles().length > 0 && this.particleKeyframeHandler == null)
                    GeckoLibConstants.LOGGER.warn("AnimationController {} for {} loaded animation {} with particle instruction keyframe markers, but no particle instruction handler has been set!",
                                                  this.name, animatable.getClass().getName(), animation.name());
            }
        }


    }
    //</editor-fold>
}
