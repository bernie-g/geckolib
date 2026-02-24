package com.geckolib.loading.definition.animation.object;

import com.geckolib.animation.object.EasingType;
import com.geckolib.cache.animation.Keyframe;
import com.geckolib.loading.math.MathValue;
import org.jspecify.annotations.Nullable;

/// X/Y/Z axis [Keyframe] triplet
public record KeyframeTriplet(Keyframe x, Keyframe y, Keyframe z) {
    /// Create a new [KeyframeTriplet] instance from keyframe track values
    public KeyframeTriplet(double timestamp, double keyframeLength, @Nullable KeyframeTriplet previousTriplet,
                           MathValue xTarget, MathValue yTarget, MathValue zTarget, EasingType easingType, MathValue[] easingArgs) {
        final MathValue fromX = previousTriplet != null ? previousTriplet.x().endValue() : xTarget;
        final MathValue fromY = previousTriplet != null ? previousTriplet.y().endValue() : yTarget;
        final MathValue fromZ = previousTriplet != null ? previousTriplet.z().endValue() : zTarget;

        this(new Keyframe(timestamp, keyframeLength, fromX, xTarget, easingType, easingArgs),
             new Keyframe(timestamp, keyframeLength, fromY, yTarget, easingType, easingArgs),
             new Keyframe(timestamp, keyframeLength, fromZ, zTarget, easingType, easingArgs));
    }
}
