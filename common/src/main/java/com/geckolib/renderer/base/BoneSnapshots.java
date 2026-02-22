package com.geckolib.renderer.base;

import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.model.GeoBone;

import java.util.Optional;
import java.util.function.Consumer;

/// Getter interface for [BoneSnapshot]s at render time.
///
/// This is where you access scale/rotation/translation values for bones for a render pass
@FunctionalInterface
public interface BoneSnapshots {
    /// Get a [GeoBone]'s [BoneSnapshot] by the bone's name, if the bone exists
    ///
    /// @param boneName The name of the bone to get the snapshot for
    Optional<BoneSnapshot> get(String boneName);

    /// Get a [GeoBone]'s [BoneSnapshot] by the bone's name.
    /// If it exists, run the given action.
    default void ifPresent(String boneName, Consumer<BoneSnapshot> action) {
        get(boneName).ifPresent(action);
    }

    /// Get a [GeoBone]'s [BoneSnapshot] by the bone itself
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default BoneSnapshot get(GeoBone bone) {
        return bone.frameSnapshot != null ? bone.frameSnapshot : get(bone.name()).get();
    }
}
