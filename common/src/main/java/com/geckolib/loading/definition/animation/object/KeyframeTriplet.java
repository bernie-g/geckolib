package com.geckolib.loading.definition.animation.object;

import com.geckolib.cache.animation.Keyframe;

/// X/Y/Z axis [Keyframe] triplet
public record KeyframeTriplet(Keyframe x, Keyframe y, Keyframe z) {}
