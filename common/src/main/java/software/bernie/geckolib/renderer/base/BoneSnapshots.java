package software.bernie.geckolib.renderer.base;

import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.model.GeoBone;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Getter interface for {@link BoneSnapshot}s at render time.
 * <p>
 * This is where you access scale/rotation/translation values for bones for a render pass
 */
@FunctionalInterface
public interface BoneSnapshots {
    /**
     * Get a {@link GeoBone}'s {@link BoneSnapshot} by the bone's name, if the bone exists
     *
     * @param boneName The name of the bone to get the snapshot for
     */
    Optional<BoneSnapshot> get(String boneName);

    /**
     * Get a {@link GeoBone}'s {@link BoneSnapshot} by the bone's name.<br>
     * If it exists, run the given action.
     */
    default void ifPresent(String boneName, Consumer<BoneSnapshot> action) {
        get(boneName).ifPresent(action);
    }

    /**
     * Get a {@link GeoBone}'s {@link BoneSnapshot} by the bone itself
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default BoneSnapshot get(GeoBone bone) {
        return bone.frameSnapshot != null ? bone.frameSnapshot : get(bone.name()).get();
    }
}
