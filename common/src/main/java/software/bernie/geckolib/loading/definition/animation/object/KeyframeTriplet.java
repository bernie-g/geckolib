package software.bernie.geckolib.loading.definition.animation.object;

import software.bernie.geckolib.cache.animation.Keyframe;

/// X/Y/Z axis [Keyframe] triplet
public record KeyframeTriplet(Keyframe x, Keyframe y, Keyframe z) {}
