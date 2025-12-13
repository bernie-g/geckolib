package software.bernie.geckolib.animation.state;

import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.EasingType;
import software.bernie.geckolib.animation.object.LoopType;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.cache.animation.BoneAnimation;
import software.bernie.geckolib.cache.animation.Keyframe;
import software.bernie.geckolib.cache.animation.KeyframeStack;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.util.MiscUtil;

import java.util.function.Function;

/**
 * Container representing a point in time for a given animation
 * <p>
 * Data is intentionally compressed into a 3-dimensional array for both memory and performance optimisations,
 * given that this is not typically a user-facing record
 * <p>
 * Data structure:
 * <pre>{@code int[boneIndex][scale|rotation|translation][x|y|z]}</pre>
 *
 * @param animation The specific animation this point is for
 * @param easingOverride The optional EasingType override the controller that created this AnimationPoint has set
 * @param loopType The loop type for this animation as defined by the {@link RawAnimation}
 * @param animTime The current animation-relative time this point is for
 * @param keyFramePoints The keyframe index array defining the {@link BoneAnimation} keyframe index for this point
 */
public record AnimationPoint(Animation animation, @Nullable EasingType easingOverride, LoopType loopType, double animTime, int[][][] keyFramePoints) {
    public static final int NO_KEYFRAME = -2;
    public static final int BEFORE_FIRST_KEYFRAME = -1;

    /**
     * Return whether this AnimationPoint has reached the end of its animation
     */
    public boolean hasFinished() {
        return MiscUtil.areFloatsEqual(this.animTime, this.animation.length());
    }

    /**
     * Get the scale keyframe points array for the given bone animation index
     * <p>
     * This returns a 3-element single-dimensional array representing the x/y/z keyframe indices
     */
    public int[] scalePoints(int boneAnimationIndex) {
        return this.keyFramePoints[boneAnimationIndex][Transform.SCALE.index];
    }

    /**
     * Get the rotation keyframe points array for the given bone animation index
     * <p>
     * This returns a 3-element single-dimensional array representing the x/y/z keyframe indices
     */
    public int[] rotationPoints(int boneAnimationIndex) {
        return this.keyFramePoints[boneAnimationIndex][Transform.ROTATION.index];
    }

    /**
     * Get the translation keyframe points array for the given bone animation index
     * <p>
     * This returns a 3-element single-dimensional array representing the x/y/z keyframe indices
     */
    public int[] translationPoints(int boneAnimationIndex) {
        return this.keyFramePoints[boneAnimationIndex][Transform.TRANSLATION.index];
    }

    /**
     * Get the keyframe prior to the one associated with this AnimationPoint for the given bone, transformation type, and axis
     * <p>
     * If no keyframe exists for that combination, returns null instead
     */
    public @Nullable Keyframe getPreviousKeyframe(int boneAnimationIndex, Transform transformationType, Axis axis) {
        return getKeyframe(boneAnimationIndex, transformationType, axis, -1);
    }

    /**
     * Get the keyframe associated with this AnimationPoint for the given bone, transformation type, and axis
     * <p>
     * If no keyframe exists for that combination, returns null instead
     */
    public @Nullable Keyframe getCurrentKeyframe(int boneAnimationIndex, Transform transformationType, Axis axis) {
        return getKeyframe(boneAnimationIndex, transformationType, axis, 0);
    }

    /**
     * Get the keyframe after the associated with this AnimationPoint for the given bone, transformation type, and axis
     * <p>
     * If no keyframe exists for that combination, returns null instead
     */
    public @Nullable Keyframe getNextKeyframe(int boneAnimationIndex, Transform transformationType, Axis axis) {
        return getKeyframe(boneAnimationIndex, transformationType, axis, 1);
    }

    /**
     * Get the keyframe associated with this AnimationPoint for the given bone, transformation type, and axis
     * <p>
     * If no keyframe exists for that combination, returns null instead
     */
    public @Nullable Keyframe getKeyframe(int boneAnimationIndex, Transform transformationType, Axis axis, int keyframeOffset) {
        final BoneAnimation boneAnimation = this.animation.boneAnimations()[boneAnimationIndex];
        final Keyframe[] keyframes = axis.keyframes(transformationType.keyframeStack(boneAnimation));
        final int keyframeIndex = this.keyFramePoints[boneAnimationIndex][transformationType.index][axis.index];

        return keyframes.length == 0 || keyframeIndex == NO_KEYFRAME ? null : keyframes[Mth.clamp(keyframeIndex + keyframeOffset, 0, keyframes.length - 1)];
    }

    /**
     * Create a new AnimationPoint representing the same animation, but at a different time
     */
    public AnimationPoint createNext(double animTime) {
        if (MiscUtil.areFloatsEqual(animTime, this.animTime))
            return this;

        animTime = Mth.clamp(animTime, 0, animation.length());
        final AnimationPoint animationPoint = new AnimationPoint(this.animation, this.easingOverride, this.loopType, animTime, this.keyFramePoints);
        final boolean reverse = animTime < this.animTime;

        for (int i = 0; i < this.keyFramePoints.length; i++) {
            findBonePoints(this.animation.boneAnimations()[i], animationPoint.keyFramePoints[i], animTime, reverse);
        }

        return animationPoint;
    }

    /**
     * Find the {@link BoneAnimation} index of the given {@link GeoBone} for this {@link #animation}
     */
    public int findBoneIndex(GeoBone bone) {
        final BoneAnimation[] boneAnimations = this.animation.boneAnimations();

        for (int i = 0; i < boneAnimations.length; i++) {
            if (boneAnimations[i].boneName().equals(bone.name()))
                return i;
        }

        return -1;
    }

    /**
     * Create a new AnimationPoint instance for the given animation and time
     */
    public static AnimationPoint createFor(Animation animation, @Nullable EasingType easingOverride, LoopType loopType, double animTime) {
        animTime = Mth.clamp(animTime, 0, animation.length());

        return new AnimationPoint(animation, easingOverride, loopType, animTime, constructBoneArray(animation, animTime));
    }

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">
    /**
     * Construct the bone animation point array for all bones in this animation
     */
    private static int[][][] constructBoneArray(Animation animation, double animTime) {
        final BoneAnimation[] boneAnimations = animation.boneAnimations();
        final int[][][] bones = new int[boneAnimations.length][3][3];

        for (int i = 0; i < boneAnimations.length; i++) {
            findBonePoints(boneAnimations[i], bones[i], animTime, false);
        }

        return bones;
    }

    /**
     * Construct the bone animation point array for all keyframes in this bone animation
     */
    private static void findBonePoints(BoneAnimation boneAnimation, int[][] bonePoints, double animTime, boolean reverse) {
        findKeyframePoints(boneAnimation.scaleKeyFrames(), bonePoints[Transform.SCALE.index], animTime, reverse);
        findKeyframePoints(boneAnimation.rotationKeyFrames(), bonePoints[Transform.ROTATION.index], animTime, reverse);
        findKeyframePoints(boneAnimation.positionKeyFrames(), bonePoints[Transform.TRANSLATION.index], animTime, reverse);
    }

    /**
     * Construct the animation point array for the given keyframe stack, assigning the current keyframe indices
     */
    private static void findKeyframePoints(KeyframeStack keyframeStack, int[] axisPoints, double animTime, boolean reverse) {
        if (reverse) {
            axisPoints[Axis.X.index] = findKeyframePointReverse(keyframeStack.xKeyframes(), animTime, axisPoints[Axis.X.index]);
            axisPoints[Axis.Y.index] = findKeyframePointReverse(keyframeStack.yKeyframes(), animTime, axisPoints[Axis.Y.index]);
            axisPoints[Axis.Z.index] = findKeyframePointReverse(keyframeStack.zKeyframes(), animTime, axisPoints[Axis.Z.index]);
        }
        else {
            axisPoints[Axis.X.index] = findKeyframePointForward(keyframeStack.xKeyframes(), animTime, axisPoints[Axis.X.index]);
            axisPoints[Axis.Y.index] = findKeyframePointForward(keyframeStack.yKeyframes(), animTime, axisPoints[Axis.Y.index]);
            axisPoints[Axis.Z.index] = findKeyframePointForward(keyframeStack.zKeyframes(), animTime, axisPoints[Axis.Z.index]);
        }
    }

    /**
     * Identify the keyframe index for a given animation time, starting from the given index
     */
    private static int findKeyframePointForward(Keyframe[] keyframes, double animTime, int startingIndex) {
        if (keyframes.length == 0)
            return NO_KEYFRAME;

        if (keyframes[0].startTime() > animTime)
            return BEFORE_FIRST_KEYFRAME;

        for (int i = Math.max(0, startingIndex); i < keyframes.length; i++) {
            if (i + 1 < keyframes.length && keyframes[i + 1].startTime() >= animTime)
                return i;
        }

        return keyframes.length - 1;
    }

    /**
     * Identify the keyframe index for a given animation time, starting from the given index and going backwards
     */
    private static int findKeyframePointReverse(Keyframe[] keyframes, double animTime, int startingIndex) {
        if (keyframes.length == 0)
            return NO_KEYFRAME;

        for (int i = Math.min(startingIndex, keyframes.length - 1); i >= 0; i--) {
            if (i - 1 >= 0 && keyframes[i - 1].startTime() <= animTime)
                return i - 1;
        }

        return BEFORE_FIRST_KEYFRAME;
    }

    public enum Transform {
        SCALE(0, BoneAnimation::scaleKeyFrames),
        ROTATION(1, BoneAnimation::rotationKeyFrames),
        TRANSLATION(2, BoneAnimation::positionKeyFrames);

        public final int index;
        public final Function<BoneAnimation, KeyframeStack> stackFunction;

        Transform(int index, Function<BoneAnimation, KeyframeStack> stackFunction) {
            this.index = index;
            this.stackFunction = stackFunction;
        }

        public KeyframeStack keyframeStack(BoneAnimation boneAnimation) {
            return this.stackFunction.apply(boneAnimation);
        }
    }

    public enum Axis {
        X(0, KeyframeStack::xKeyframes),
        Y(1, KeyframeStack::yKeyframes),
        Z(2, KeyframeStack::zKeyframes);

        public final int index;
        public final Function<KeyframeStack, Keyframe[]> framesFunction;

        Axis(int index, Function<KeyframeStack, Keyframe[]> framesFunction) {
            this.index = index;
            this.framesFunction = framesFunction;
        }

        public Keyframe[] keyframes(KeyframeStack stack) {
            return this.framesFunction.apply(stack);
        }
    }
    //</editor-fold>
}
