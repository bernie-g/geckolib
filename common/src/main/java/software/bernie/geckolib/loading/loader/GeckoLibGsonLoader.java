package software.bernie.geckolib.loading.loader;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.loading.definition.animation.ActorAnimations;
import software.bernie.geckolib.loading.definition.geometry.Geometry;
import software.bernie.geckolib.loading.definition.geometry.object.ModelFormatVersion;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.object.CompoundException;

import java.io.IOException;
import java.io.Reader;

/// [GeckoLib resource loader][GeckoLibLoader] using a [Gson][com.google.gson.Gson] implementation
public class GeckoLibGsonLoader implements GeckoLibLoader<JsonObject> {
    /// Read a GeckoLib model resource from disk into memory, deserializing it into type `T`
    ///
    /// @param resourcePath The resource path of the resource to load
    /// @param resource The resource reference to load
    /// @return An instance of type `T` ready for baking
    @Override
    public JsonObject deserializeGeckoLibModelFile(Identifier resourcePath, Resource resource) throws RuntimeException {
        return readResourceAsJson(resourcePath, resource);
    }

    /// Read a GeckoLib animations resource from disk into memory, deserializing it into type `T`
    ///
    /// @param resourcePath The resource path of the resource to load
    /// @param resource The resource reference to load
    /// @return An instance of type `T` ready for baking
    @Override
    public JsonObject deserializeGeckoLibAnimationFile(Identifier resourcePath, Resource resource) throws RuntimeException {
        return readResourceAsJson(resourcePath, resource);
    }

    /// Bake a GeckoLib model file into its final [BakedGeoModel] form from the raw `T` type instance
    /// read into memory in [#deserializeGeckoLibModelFile]
    ///
    /// @param resourcePath The resource path of the animations file that was loaded
    /// @param raw The raw `T` type object read in [#deserializeGeckoLibModelFile]
    @Override
    public BakedGeoModel bakeGeckoLibModelFile(Identifier resourcePath, JsonObject raw) throws RuntimeException {
        return bakeJsonModel(resourcePath, raw).bake(resourcePath);
    }

    /// Bake a GeckoLib animations file into its final [BakedAnimations] form from the raw `T` type instance
    /// read into memory in [#deserializeGeckoLibAnimationFile]
    ///
    /// @param resourcePath The resource path of the model file that was loaded
    /// @param raw The raw `T` type object read in [#deserializeGeckoLibAnimationFile]
    @Override
    public BakedAnimations bakeGeckoLibAnimationsFile(Identifier resourcePath, JsonObject raw) throws RuntimeException {
        return bakeJsonAnimations(resourcePath, raw).bake(resourcePath);
    }

    /// Read a single resource into its [JsonObject] form
    protected static JsonObject readResourceAsJson(Identifier resourcePath, Resource resource) throws RuntimeException {
        try (Reader reader = resource.openAsReader()) {
            return GsonHelper.parse(reader);
        }
        catch (IOException ex) {
            throw GeckoLibConstants.exception(resourcePath, "Error reading JSON file", ex);
        }
    }

    /// Bake a [Geometry] from its [JsonObject] serialized form
    protected static Geometry bakeJsonModel(Identifier resourcePath, JsonObject json) throws RuntimeException {
        if (resourcePath.getPath().endsWith(".animation.json"))
            throw new IllegalStateException("Found animation file found in models folder! '" + resourcePath + "'");

        final Geometry geometry = Geometry.GSON.fromJson(json, Geometry.class);
        final ModelFormatVersion matchedVersion = ModelFormatVersion.match(geometry.formatVersion());

        if (matchedVersion == null) {
            GeckoLibConstants.LOGGER.warn("{}: Unknown geo model format version: '{}'. This may not work correctly", resourcePath, geometry.formatVersion());
        }
        else if (!matchedVersion.isSupported()) {
            GeckoLibConstants.LOGGER.error("{}: Unsupported geo model format version: '{}'. {}", resourcePath, geometry.formatVersion(), matchedVersion.getErrorMessage());
        }

        return geometry;
    }

    /// Bake the [ActorAnimations] from a [JsonObject] serialized form
    protected static ActorAnimations bakeJsonAnimations(Identifier resourcePath, JsonObject json) throws RuntimeException {
        if (resourcePath.getPath().endsWith(".geo.json"))
            throw new IllegalStateException("Found model file in animations folder! '" + resourcePath + "'");

        try {
            return ActorAnimations.GSON.fromJson(json, ActorAnimations.class);
        }
        catch (CompoundException ex) {
            throw ex.withMessage(resourcePath + ": Error building animations from JSON");
        }
        catch (Exception ex) {
            throw GeckoLibConstants.exception(resourcePath, "Error building animations from JSON", ex);
        }
    }
}
