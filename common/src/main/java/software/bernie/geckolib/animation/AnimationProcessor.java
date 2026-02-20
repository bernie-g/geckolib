package software.bernie.geckolib.animation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.object.EasingType;
import software.bernie.geckolib.animation.state.AnimationPoint;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.animation.state.EasingState;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.cache.animation.BoneAnimation;
import software.bernie.geckolib.cache.animation.Keyframe;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.ClientUtil;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/// Internal class for handling the processing of animations and animation-related functionality.
///
/// This is distinct from [AnimationController] in that the controller handles the _state_ of an animatable's animations,
/// whereas AnimationProcessor handles the _logic_ of individual animations.
@ApiStatus.Internal
public class AnimationProcessor {
    /// Perform the necessary preparations for the upcoming render pass and collect it to be passed along with the [GeoRenderState]
    ///
    /// @param animatable The animatable relevant to the upcoming render pass
    /// @param renderState The [GeoRenderState] being built for the upcoming render pass
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends GeoAnimatable> void extractControllerStates(T animatable, GeoRenderState renderState, GeoModel<T> geoModel) {
        final AnimatableManager<T> manager = (AnimatableManager) Objects.requireNonNull(renderState.getGeckolibData(DataTickets.ANIMATABLE_MANAGER));
        final Collection<AnimationController<T>> controllers = manager.getAnimationControllers().values();
        final double tick = renderState.getAnimatableAge();
        final List<ControllerState> controllerStates = new ObjectArrayList<>(controllers.size());

        manager.markRenderedAt(tick);

        if (!controllers.isEmpty()) {
            final float partialTick = renderState.getPartialTick();
            final Level level = ClientUtil.getLevel();
            final Player player = ClientUtil.getClientPlayer();
            final Vec3 cameraPos = ClientUtil.getCameraPos();
            final double renderTime = manager.getFirstRenderTick() - tick;

            if (level != null && player != null) {
                for (AnimationController<T> controller : controllers) {
                    MolangQueries.Actor<T> actor = new MolangQueries.Actor<>(animatable, renderState, controller, renderTime, partialTick, level, player, cameraPos);
                    ControllerState controllerState = controller.extractControllerState(animatable, renderState, manager, actor, geoModel);

                    if (controllerState != null)
                        controllerStates.add(controllerState);
                }
            }
        }

        renderState.addGeckolibData(DataTickets.ANIMATION_CONTROLLER_STATES, controllerStates.toArray(new ControllerState[0]));
    }

    /// Create the [BoneSnapshot]s for the upcoming render pass based on the provided [AnimationPoint]s
    public static void createBoneSnapshots(ControllerState controllerState, BoneSnapshots snapshots) {
        final AnimationPoint animation = controllerState.animationPoint();
        final AnimationPoint prevAnimation = controllerState.prevAnimationPoint();
        final EasingType easingOverride = controllerState.easingOverride();
        final boolean additive = controllerState.additive();
        final BoneAnimation[] boneAnimations = animation.animation().boneAnimations();

        for (int boneIndex = 0; boneIndex < boneAnimations.length; boneIndex++) {
            BoneSnapshot snapshot = snapshots.get(boneAnimations[boneIndex].boneName()).orElse(null);

            if (snapshot != null) {
                setSnapshotScale(snapshot, boneIndex, controllerState, additive, animation, prevAnimation, easingOverride);
                setSnapshotRotation(snapshot, boneIndex, controllerState, additive, animation, prevAnimation, easingOverride);
                setSnapshotTranslation(snapshot, boneIndex, controllerState, additive, animation, prevAnimation, easingOverride);
            }
        }
    }

    private static void setSnapshotScale(BoneSnapshot snapshot, int boneIndex, ControllerState controllerState, boolean additive,
                                         AnimationPoint animation, @Nullable AnimationPoint prevAnimation, @Nullable EasingType easingOverride) {
        float xScale = findAnimationPointValue(snapshot, controllerState, animation, prevAnimation, boneIndex, AnimationPoint.Transform.SCALE, AnimationPoint.Axis.X, easingOverride);
        float yScale = findAnimationPointValue(snapshot, controllerState, animation, prevAnimation, boneIndex, AnimationPoint.Transform.SCALE, AnimationPoint.Axis.Y, easingOverride);
        float zScale = findAnimationPointValue(snapshot, controllerState, animation, prevAnimation, boneIndex, AnimationPoint.Transform.SCALE, AnimationPoint.Axis.Z, easingOverride);

        if (additive) {
            xScale *= snapshot.getScaleX();
            yScale *= snapshot.getScaleY();
            zScale *= snapshot.getScaleZ();
        }

        snapshot.setScale(xScale, yScale, zScale);
    }

    private static void setSnapshotRotation(BoneSnapshot snapshot, int boneIndex, ControllerState controllerState, boolean additive,
                                            AnimationPoint animation, @Nullable AnimationPoint prevAnimation, @Nullable EasingType easingOverride) {
        float xRot = findAnimationPointValue(snapshot, controllerState, animation, prevAnimation, boneIndex, AnimationPoint.Transform.ROTATION, AnimationPoint.Axis.X, easingOverride);
        float yRot = findAnimationPointValue(snapshot, controllerState, animation, prevAnimation, boneIndex, AnimationPoint.Transform.ROTATION, AnimationPoint.Axis.Y, easingOverride);
        float zRot = findAnimationPointValue(snapshot, controllerState, animation, prevAnimation, boneIndex, AnimationPoint.Transform.ROTATION, AnimationPoint.Axis.Z, easingOverride);

        if (additive) {
            GeoBone bone = snapshot.getBone();
            xRot += snapshot.getRotX() - bone.baseRotX();
            yRot += snapshot.getRotY() - bone.baseRotY();
            zRot += snapshot.getRotZ() - bone.baseRotZ();
        }

        snapshot.setRotation(xRot, yRot, zRot);
    }

    private static void setSnapshotTranslation(BoneSnapshot snapshot, int boneIndex, ControllerState controllerState, boolean additive,
                                               AnimationPoint animation, @Nullable AnimationPoint prevAnimation, @Nullable EasingType easingOverride) {
        float xPos = findAnimationPointValue(snapshot, controllerState, animation, prevAnimation, boneIndex, AnimationPoint.Transform.TRANSLATION, AnimationPoint.Axis.X, easingOverride);
        float yPos = findAnimationPointValue(snapshot, controllerState, animation, prevAnimation, boneIndex, AnimationPoint.Transform.TRANSLATION, AnimationPoint.Axis.Y, easingOverride);
        float zPos = findAnimationPointValue(snapshot, controllerState, animation, prevAnimation, boneIndex, AnimationPoint.Transform.TRANSLATION, AnimationPoint.Axis.Z, easingOverride);

        if (additive) {
            xPos += snapshot.getTranslateX();
            yPos += snapshot.getTranslateY();
            zPos += snapshot.getTranslateZ();
        }

        snapshot.setTranslation(xPos, yPos, zPos);
    }

    /// Compute the effective animation point value from the provided [AnimationPoint]s and associated values
    public static float findAnimationPointValue(BoneSnapshot boneSnapshot, ControllerState controllerState, AnimationPoint animation, @Nullable AnimationPoint prevAnimation,
                                                int boneIndex, AnimationPoint.Transform transform, AnimationPoint.Axis axis, @Nullable EasingType easingOverride) {
        if (controllerState.transitionTime() >= 0) {
            return prevAnimation != null ?
                   findTransitionPointValue(boneSnapshot, controllerState, animation, prevAnimation, boneIndex, transform, axis, easingOverride) :
                   findResetPointValue(boneSnapshot, controllerState, animation, boneIndex, transform, axis, easingOverride);
        }

        final Keyframe fromKeyframe = animation.getCurrentKeyframe(boneIndex, transform, axis);
        final Keyframe toKeyframe;

        if (fromKeyframe == null || (toKeyframe = animation.getNextKeyframe(boneIndex, transform, axis)) == null)
            return transform.defaultValue;

        final double from = fromKeyframe.endValue().get(controllerState);
        final double to = toKeyframe.endValue().get(controllerState);
        final double delta = toKeyframe.length() == 0 ? 0 : (animation.animTime() - fromKeyframe.startTime()) / toKeyframe.length();
        final EasingState easingState = new EasingState(easingOverride != null ? easingOverride : toKeyframe.easingType(), toKeyframe.easingArgs(), delta, from, to);

        return (float)easingState.interpolate(controllerState);
    }

    /// Compute the effective animation point value from the provided [AnimationPoint]s, transitioning to the next animation
    private static float findTransitionPointValue(BoneSnapshot boneSnapshot, ControllerState controllerState, AnimationPoint animation, AnimationPoint prevAnimation,
                                                  int boneIndex, AnimationPoint.Transform transform, AnimationPoint.Axis axis, @Nullable EasingType easingOverride) {
        final Keyframe toKeyframe = animation.getNextKeyframe(boneIndex, transform, axis);
        final int prevBoneIndex;

        if (toKeyframe == null || (prevBoneIndex = prevAnimation.findBoneIndex(boneSnapshot.getBone())) < 0)
            return transform.defaultValue;

        final double from = wrapRotation(findAnimationPointValue(boneSnapshot, controllerState, prevAnimation, null, prevBoneIndex, transform, axis, prevAnimation.easingOverride()), transform);
        final double to = toKeyframe.startValue().get(controllerState);
        final double delta = controllerState.transitionTicks() == 0 ? 1 : Math.min(1, controllerState.transitionTime() / (float)controllerState.transitionTicks());
        final EasingState easingState = new EasingState(easingOverride != null ? easingOverride : toKeyframe.easingType(), toKeyframe.easingArgs(), delta, from, to);

        return (float)easingState.interpolate(controllerState);
    }

    /// Compute the effective animation point value from the provided [AnimationPoint]s, resetting back to the base bone state
    private static float findResetPointValue(BoneSnapshot boneSnapshot, ControllerState controllerState, AnimationPoint animation,
                                             int boneIndex, AnimationPoint.Transform transform, AnimationPoint.Axis axis, @Nullable EasingType easingOverride) {
        final ControllerState previousState = new ControllerState(controllerState.animationPoint(), null, -1, 0,
                                                            controllerState.additive(), controllerState.easingOverride(), controllerState.renderState(), controllerState.queryValues());
        final double delta = Math.min(1, controllerState.transitionTime() / (double)controllerState.transitionTicks());
        final double from = wrapRotation(findAnimationPointValue(boneSnapshot, previousState, animation, null, boneIndex, transform, axis, easingOverride), transform);
        final double to = getSnapshotResetTarget(boneSnapshot, transform, axis, controllerState.additive());
        final EasingState easingState = new EasingState(easingOverride == null ? EasingType.LINEAR : easingOverride, new MathValue[0], delta, from, to);

        return (float)easingState.interpolate(controllerState);
    }

    /// Get the base snapshot value to return to when resetting from an animation
    private static float getSnapshotResetTarget(BoneSnapshot snapshot, AnimationPoint.Transform transform, AnimationPoint.Axis axis, boolean additive) {
        if (!additive)
            return transform.defaultValue;

        return switch (transform) {
            case SCALE -> switch (axis) {
                case X -> snapshot.getScaleX();
                case Y -> snapshot.getScaleY();
                case Z -> snapshot.getScaleZ();
            };
            case ROTATION -> switch (axis) {
                case X -> snapshot.getRotX();
                case Y -> snapshot.getRotY();
                case Z -> snapshot.getRotZ();
            };
            case TRANSLATION -> switch (axis) {
                case X -> snapshot.getTranslateX();
                case Y -> snapshot.getTranslateY();
                case Z -> snapshot.getTranslateZ();
            };
        };
    }

    /// Wraps keyframe values to be the nearest multiple of 360 degrees, so that resetting and transitioning don't result in massive backspins
    private static double wrapRotation(double value, AnimationPoint.Transform transform) {
        if (transform != AnimationPoint.Transform.ROTATION)
            return value;

        return Mth.wrapDegrees(value * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
    }

    /// Get the [Animation] associated with the given [RawAnimation.Stage]
    ///
    /// Returns null if an animation by the given name for the provided animatable doesn't exist
    public static <T extends GeoAnimatable> @Nullable Animation getOrCreateAnimation(RawAnimation.Stage stage, T animatable, GeoModel<T> geoModel) {
        // This is intentional. DO NOT CHANGE THIS or Tslat will be unhappy
        //noinspection StringEquality
        if (stage.animationName() == RawAnimation.Stage.WAIT)
            return Animation.generateWaitAnimation(stage.waitTicks());

        return geoModel.getBakedAnimation(animatable, stage.animationName());
    }
}
