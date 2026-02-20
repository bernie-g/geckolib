package software.bernie.geckolib.cache;

import com.google.common.base.Suppliers;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.cache.model.*;
import software.bernie.geckolib.cache.model.cuboid.CuboidGeoBone;
import software.bernie.geckolib.cache.model.cuboid.GeoCube;

import java.util.Map;
import java.util.function.Supplier;

/// Container record for GeckoLib's baked model cache
///
/// @param cache The baked models map as loaded from the resource files
public record BakedModelCache(Map<Identifier, BakedGeoModel> cache) {
    public static final Supplier<BakedGeoModel> MISSINGNO = createMissingModel();

    /// @return The size of the model cache
    public int size() {
        return this.cache.size();
    }

    /// Get a [BakedGeoModel] from the model cache by its file id
    ///
    /// @param modelFile The file identifier of the animations file - (E.G. `mymod:entity/my_mob`)
    public BakedGeoModel getModel(Identifier modelFile) {
        BakedGeoModel model = this.cache.get(modelFile);

        if (model == null) {
            Identifier strippedPath = stripLegacyPath(modelFile);

            if (!modelFile.equals(strippedPath)) {
                GeckoLibConstants.LOGGER.error("Superfluous prefix or suffix found in model resource path: '{}'. Should be '{}'", modelFile, strippedPath);

                model = this.cache.get(strippedPath);
            }

            if (model == null) {
                GeckoLibConstants.LOGGER.error("Unable to find model: {}", modelFile);

                return MISSINGNO.get();
            }
        }

        return model;
    }

    /// Strips out unnecessary prefix/suffix components of a model resource path.
    /// Typically these are leftovers from previous versions of GeckoLib.
    ///
    /// @deprecated To be removed once a sufficient time has passed to allow devs to fix their paths
    @ApiStatus.Internal
    @Deprecated(forRemoval = true)
    private static Identifier stripLegacyPath(Identifier legacyPath) {
        String path = legacyPath.getPath();

        if (path.startsWith("geckolib/"))
            path = path.substring(9);

        if (path.startsWith("models/"))
            path = path.substring(7);

        if (path.endsWith(".json"))
            path = path.substring(0, path.length() - 5);

        if (path.endsWith(".geo"))
            path = path.substring(0, path.length() - 4);

        return !path.equals(legacyPath.getPath()) ? legacyPath.withPath(path) : legacyPath;
    }

    /// Create the "missingno" cube model for rendering when a model is not found
    private static Supplier<BakedGeoModel> createMissingModel() {
        return Suppliers.memoize(() -> {
            final GeoVertex[] vertices = new GeoVertex[] {
                    new GeoVertex(-0.5f, 1f, 0.5f, 1f, 0f),
                    new GeoVertex(-0.5f, 1f, -0.5f, 0, 0f),
                    new GeoVertex(-0.5f, 0, -0.5f, 0, 1f),
                    new GeoVertex(-0.5f, 0, 0.5f, 1f, 1f),
            };
            final GeoQuad[] quads = new GeoQuad[] {
                    new GeoQuad(vertices, -1, 0, 0, Direction.WEST),
                    new GeoQuad(vertices, 1, 0, 0, Direction.EAST),
                    new GeoQuad(vertices, 0, 0, -1, Direction.NORTH),
                    new GeoQuad(vertices, 0, 0, 1, Direction.SOUTH),
                    new GeoQuad(vertices, 0, 1, 0, Direction.UP),
                    new GeoQuad(vertices, 0, -1, 0, Direction.DOWN),
            };
            final GeoBone[] topLevelBones = new GeoBone[] {
                    new CuboidGeoBone(null, "Main", new GeoBone[0], new GeoCube[] {
                            new GeoCube(quads, Vec3.ZERO, Vec3.ZERO, new Vec3(16, 16, 16))
                    }, new GeoLocator[0], 0, 0, 0, 0, 0, 0)
            };
            final ModelProperties modelProperties = new ModelProperties(GeckoLibConstants.id("internal/missingno"), "geometry.unknown", 2.5f, 2.5f, new Vec3(0, 0.75f, 0), 16, 16);

            return new BakedGeoModel(topLevelBones, Map.of(), modelProperties);
        });
    }
}
