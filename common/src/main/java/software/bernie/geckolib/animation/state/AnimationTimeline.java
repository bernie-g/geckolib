package software.bernie.geckolib.animation.state;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.EasingType;
import software.bernie.geckolib.animation.object.LoopType;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.cache.animation.keyframeevent.CustomInstructionKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.KeyFrameData;
import software.bernie.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import software.bernie.geckolib.cache.animation.keyframeevent.SoundKeyframeData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.List;

/// Timeline holder for a given [RawAnimation] in combination with a [AnimationController]'s [AnimationController#getTransitionTicks()]
///
/// @param stages The array of stages this timeline represents, based on an interleaving of transition periods and Animations
@ApiStatus.Internal
public record AnimationTimeline(Stage[] stages) {
    /// Return the time in seconds at which the last animation in the timeline ends
    public double lastAnimationEndTime() {
        final Stage stage = this.stages[this.stages.length - 1];

        return stage.isTransition ? stage.startTime : stage.endTime;
    }

    /// Return the total length of the timeline in seconds
    public double totalTime() {
        return this.stages[this.stages.length - 1].endTime;
    }

    /// Get the index of the stage that the given timeline time is in
    public int getStageIndex(double timelineTime) {
        for (int i = 0; i < this.stages.length; i++) {
            if (timelineTime < this.stages[i].endTime)
                return i;
        }

        return this.stages.length - 1;
    }

    /// Get the stage that the given timeline time is in
    public Stage getStage(double timelineTime) {
        return this.stages[getStageIndex(timelineTime)];
    }

    /// Return the transition ticks value used to construct this timeline
    public int getTransitionLength() {
        return this.stages[0].isTransition ? Mth.ceil(this.stages[0].endTime) : 0;
    }

    /// Return the current time position in a transition stage, or -1 if not in a transition stage
    public double getTransitionTime(double timelineTime) {
        final Stage stage = getStage(timelineTime);

        return stage.isTransition ? timelineTime - stage.startTime : -1;
    }

    /// Get the animation stage for the given time, accounting for transitions
    public Stage getAnimationStage(double timelineTime) {
        int stageIndex = getStageIndex(timelineTime);
        AnimationTimeline.Stage stage = this.stages[stageIndex];

        if (!stage.isTransition)
            return stage;

        return stageIndex >= this.stages.length - 1 ? this.stages[stageIndex - 1] : this.stages[stageIndex + 1];
    }

    /// @return Whether the given timeline time after the last animation in the timeline has completed
    public boolean hasFinishedAnimations(double timelineTime) {
        return timelineTime >= lastAnimationEndTime();
    }

    /// Create an [AnimationPoint] for the given position on the timeline
    ///
    /// If the timeline position is during a transition, the resultant point will be the start of the following animation if transitioning to an animation,
    /// or end of the preceding animation if transitioning to reset
    ///
    /// Returns `null` if the timeline is empty
    public AnimationPoint createAnimationPoint(double timelineTime, @Nullable AnimationPoint existingPoint, @Nullable EasingType easingOverride) {
        final int stageIndex = getStageIndex(timelineTime);
        Stage stage = this.stages[stageIndex];
        double time = timelineTime - stage.startTime;

        if (stage.isTransition) {
            boolean isReset = stageIndex >= this.stages.length - 1;
            stage = this.stages[isReset ? stageIndex - 1 : stageIndex + 1];
            time = isReset ? stage.endTime : stage.startTime;
        }

        double existingTime = existingPoint == null ? 0 : stage.startTime + existingPoint.animTime();

        if (existingPoint == null || existingPoint.animation() != stage.animation || totalTime() - existingTime < existingTime)
            return AnimationPoint.createFor(stage.animation, easingOverride, stage.loopType(), time);

        return existingPoint.createNext(time);
    }

    /// Run through any [KeyFrameData] between the given times, calling the appropriate handlers for each
    public <T extends GeoAnimatable> void triggerKeyframeMarkersBetween(T animatable, GeoRenderState renderState, double fromTime, double toTime, AnimationController<T> controller,
                                              AnimationController.@Nullable KeyframeEventHandler<T, SoundKeyframeData> soundHandler,
                                              AnimationController.@Nullable KeyframeEventHandler<T, ParticleKeyframeData> particleHandler,
                                              AnimationController.@Nullable KeyframeEventHandler<T, CustomInstructionKeyframeData> customInstructionHandler) {
        if (soundHandler == null && particleHandler == null && customInstructionHandler == null)
            return;

        List<SoundKeyframeData> soundMarkers = new ObjectArrayList<>();
        List<ParticleKeyframeData> particleMarkers = new ObjectArrayList<>();
        List<CustomInstructionKeyframeData> customInstructionMarkers = new ObjectArrayList<>();
        double minTime = Math.min(fromTime, toTime);
        double maxTime = Math.max(fromTime, toTime);

        for (int i = 0; i < this.stages.length; i++) {
            Stage stage = this.stages[i];

            if (stage.startTime > maxTime)
                break;

            if (stage.endTime > minTime && !stage.isTransition) {
                double animFromTime = Math.max(0, minTime - stage.startTime);
                double animToTime = Math.min(stage.animation.length(), maxTime - stage.startTime);

                if (soundHandler != null)
                    soundMarkers.addAll(getKeyframesForAnimation(animFromTime, animToTime, stage.animation.keyframeMarkers().sounds()));

                if (particleHandler != null)
                    particleMarkers.addAll(getKeyframesForAnimation(animFromTime, animToTime, stage.animation.keyframeMarkers().particles()));

                if (customInstructionHandler != null)
                    customInstructionMarkers.addAll(getKeyframesForAnimation(animFromTime, animToTime, stage.animation.keyframeMarkers().customInstructions()));
            }
        }

        if (!soundMarkers.isEmpty()) {
            for (SoundKeyframeData soundData : toTime < fromTime ? soundMarkers.reversed() : soundMarkers) {
                soundHandler.handle(new KeyFrameEvent<>(animatable, renderState, controller, soundData));
            }
        }

        if (!particleMarkers.isEmpty()) {
            for (ParticleKeyframeData particleData : toTime < fromTime ? particleMarkers.reversed() : particleMarkers) {
                particleHandler.handle(new KeyFrameEvent<>(animatable, renderState, controller, particleData));
            }
        }

        if (!customInstructionMarkers.isEmpty()) {
            for (CustomInstructionKeyframeData customData : toTime < fromTime ? customInstructionMarkers.reversed() : customInstructionMarkers) {
                customInstructionHandler.handle(new KeyFrameEvent<>(animatable, renderState, controller, customData));
            }
        }
    }

    /// Iterate over the provided keyframe marker group, calling the handler as needed
    private <M extends KeyFrameData> List<M> getKeyframesForAnimation(double startTime, double endTime, M[] markers) {
        final List<M> validMarkers = new ObjectArrayList<>();

        for (int i = 0; i < markers.length; i++) {
            final M marker = markers[i];

            if (marker.getTime() > endTime)
                break;

            if (marker.getTime() > startTime || startTime == 0)
                validMarkers.add(marker);
        }

        return validMarkers;
    }

    /// Create a timeline from a given [RawAnimation], or null if no animations were found
    public static <T extends GeoAnimatable> @Nullable AnimationTimeline create(RawAnimation rawAnimation, T animatable, GeoModel<T> model, int transitionTicks) {
        final List<RawAnimation.Stage> rawStages = rawAnimation.getAnimationStages();
        final List<Stage> stages = new ObjectArrayList<>(rawStages.size());
        final double transitionTime = transitionTicks / 20f;
        double currentTime = 0;

        for (RawAnimation.Stage stage : rawStages) {
            Animation animation = AnimationProcessor.getOrCreateAnimation(stage, animatable, model);

            if (animation != null) {
                if (transitionTime > 0) {
                    stages.add(Stage.transition(currentTime, transitionTime, animation));
                    currentTime += transitionTime;
                }

                stages.add(Stage.animation(currentTime, animation, stage.loopType()));
                currentTime += animation.length();
            }
        }

        if (transitionTime > 0)
            stages.add(Stage.transition(currentTime, transitionTime, stages.getLast().animation()));

        if (stages.isEmpty())
            return null;

        return new AnimationTimeline(stages.toArray(new Stage[0]));
    }

    /// Container class representing a single stage of the timeline
    ///
    /// @param startTime The time of the start of the stage (in seconds)
    /// @param endTime The time of the end of the stage (in seconds). Is equal to the startTime of the following stage
    /// @param isTransition Whether this is a transition stage
    /// @param animation The animation to be extracting keyframes for animation for this stage. For transition stages, is the animation transitioning to, or from if at the end of the timeline
    /// @param loopType The loop type for this stage as defined by the [RawAnimation] used to construct this timeline
    public record Stage(double startTime, double endTime, boolean isTransition, @Nullable Animation animation, @Nullable LoopType loopType) {
        /// Create a new transition stage
        private static Stage transition(double startTime, double transitionTime, Animation animation) {
            return new Stage(startTime, startTime + transitionTime, true, animation, null);
        }

        /// Create a new animation stage
        private static Stage animation(double startTime, Animation animation, LoopType loopType) {
            return new Stage(startTime, startTime + animation.length(), false, animation, loopType);
        }
    }
}
