package software.bernie.geckolib.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.loading.object.BakedAnimations;

import java.util.function.BiPredicate;

/// Replaceable SPI for alternate deserialization schemes, replacing GeckoLib's default [Gson] handling
///
/// Not currently used by GeckoLib
/// @param <T> The raw object type this loader uses in memory. E.G. [GSON][JsonObject]
public interface GeckoLibLoader<T> {
    /// Read a GeckoLib model resource from disk into memory, deserializing it into type `T`
    ///
    /// @param id The resource path of the resource to load
    /// @param resource The resource reference to load
    /// @return An instance of type `T` ready for baking
    T deserializeGeckoLibModelFile(Identifier id, Resource resource);

    /// Read a GeckoLib animations resource from disk into memory, deserializing it into type `T`
    ///
    /// @param id The resource path of the resource to load
    /// @param resource The resource reference to load
    /// @return An instance of type `T` ready for baking
    T deserializeGeckoLibAnimationFile(Identifier id, Resource resource);

    /// Bake a GeckoLib model file into its final [BakedGeoModel] form from the raw `T` type instance
    /// read into memory in [#deserializeGeckoLibModelFile]
    ///
    /// @param id The resource path of the animations file that was loaded
    /// @param raw The raw `T` type object read in [#deserializeGeckoLibModelFile]
    BakedGeoModel bakeGeckoLibModelFile(Identifier id, T raw);

    /// Bake a GeckoLib animations file into its final [BakedAnimations] form from the raw `T` type instance
    /// read into memory in [#deserializeGeckoLibAnimationFile]
    ///
    /// @param id The resource path of the model file that was loaded
    /// @param raw The raw `T` type object read in [#deserializeGeckoLibAnimationFile]
    BakedAnimations bakeGeckoLibAnimationsFile(Identifier id, T raw);

    /// Predicate interface for determining whether a given [GeckoLibLoader] should handle a specific resource
    @FunctionalInterface
    interface Predicate extends BiPredicate<Identifier, Resource> {
        /// Return whether a given `GeckoLibLoader` should be used to handle the provided resource
        ///
        /// @return `true` if the resource should be handled by the accompanying loader, or false to fall back to default
        boolean shouldHandle(Identifier id, Resource resource);

        @Override
        @Deprecated
        default boolean test(Identifier identifier, Resource resource) {
            return shouldHandle(identifier, resource);
        }
    }
}
