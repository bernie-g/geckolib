package software.bernie.geckolib.cache;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.loading.object.BakedAnimations;

import java.util.Map;

/**
 * Container record for GeckoLib's baked animation cache
 */
public record BakedAnimationCache(Map<Identifier, BakedAnimations> cache) {
    /**
     * Get a baked {@link Animation} from the animation cache by its file id and animation name
     *
     * @param animationFile The file identifier of the animations file - (E.G. <code>mymod:entity/my_mob</code>)
     * @param fallbackFiles Any possible fallback animation file locations, in case the specific animation isn't found in the primary animation file
     * @param animationName The name of the animation to retrieve from the animation file
     * @return The baked {@link Animation} instance, or null if not found
     */
    @Nullable
    public Animation getAnimation(Identifier animationFile, Identifier[] fallbackFiles, String animationName) {
        BakedAnimations animations = null;

        for (int i = -1; i < fallbackFiles.length; i++) {
            Identifier path = i == -1 ? animationFile : fallbackFiles[i];
            animations = this.cache.get(path);

            if (animations == null) {
                Identifier strippedPath = stripLegacyPath(path);

                if (!strippedPath.equals(path)) {
                    GeckoLibConstants.LOGGER.error("Superfluous prefix or suffix found in animation resource path: '{}'. Should be '{}'", path, strippedPath);

                    animations = this.cache.get(animationFile = stripLegacyPath(path));
                }
            }

            if (animations != null) {
                Animation animation = animations.getAnimation(animationName);

                if (animation != null)
                    return animation;
            }
        }

        if (animations == null)
            throw new IllegalArgumentException("Unable to find animation file '" + animationFile + "'");

        GeckoLibConstants.LOGGER.error("Unable to find animation: '{}' in animation file '{}'", animationName, animationFile);

        return null;
    }

    /**
     * Strips out unnecessary prefix/suffix components of an animation resource path.<br>
     * Typically these are leftovers from previous versions of GeckoLib.
     *
     * @deprecated To be removed once a sufficient time has passed to allow devs to fix their paths
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = true)
    private static Identifier stripLegacyPath(Identifier legacyPath) {
        String path = legacyPath.getPath();

        if (path.startsWith("geckolib/"))
            path = path.substring(9);

        if (path.startsWith("animations/"))
            path = path.substring(11);

        if (path.endsWith(".json"))
            path = path.substring(0, path.length() - 5);

        if (path.endsWith(".animations"))
            path = path.substring(0, path.length() - 10);

        return !path.equals(legacyPath.getPath()) ? legacyPath.withPath(path) : legacyPath;
    }
}
