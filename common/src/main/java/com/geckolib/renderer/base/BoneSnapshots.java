package com.geckolib.renderer.base;

import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;

import java.util.List;
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

    /// Create a [BoneSnapshots] instance for the given model, allowing for direct-bone retrieval where possible
    static BoneSnapshots createForModel(BakedGeoModel model, List<BoneSnapshot> snapshots) {
        return new BoneSnapshots() {
            @Override
            public Optional<BoneSnapshot> get(String boneName) {
                final GeoBone bone = model.boneLookup().get().getOrDefault(boneName, null);

                if (bone == null)
                    return Optional.empty();

                if (bone.frameSnapshot == null)
                    snapshots.add(bone.frameSnapshot = BoneSnapshot.create(bone));

                return Optional.of(bone.frameSnapshot);
            }

            @Override
            public BoneSnapshot get(GeoBone bone) {
                if (bone.frameSnapshot == null)
                    snapshots.add(bone.frameSnapshot = BoneSnapshot.create(bone));

                return bone.frameSnapshot;
            }
        };
    }
}
