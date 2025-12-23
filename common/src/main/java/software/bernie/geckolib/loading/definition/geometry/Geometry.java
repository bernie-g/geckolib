package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.util.JsonUtil;

/**
 * Container class for a full geometry file definition, only used for intermediary steps between .json deserialization and GeckoLib object creation
 * <p>
 * This is the root-level object for a fully processed .geo file
 *
 * @param formatVersion The bedrock geometry format version of this geometry instance
 * @param debug An optional debug marker for this geometry instance, not used by GeckoLib
 * @param definitions The array of geometry definitions contained in this geometry instance
 * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
 */
@ApiStatus.Internal
public record Geometry(String formatVersion, boolean debug, GeometryDefinition[] definitions) {
    /**
     * Publicly accessible GSON parser for GeckoLib geometry .json files
     */
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Geometry.class, gsonDeserializer())
            .registerTypeAdapter(GeometryBone.class, GeometryBone.gsonDeserializer())
            .registerTypeAdapter(GeometryCube.class, GeometryCube.gsonDeserializer())
            .registerTypeAdapter(GeometryDefinition.class, GeometryDefinition.gsonDeserializer())
            .registerTypeAdapter(GeometryDescription.class, GeometryDescription.gsonDeserializer())
            .registerTypeAdapter(GeometryLocator.class, GeometryLocator.gsonDeserializer())
            .registerTypeAdapter(GeometryLocators.class, GeometryLocators.gsonDeserializer())
            .registerTypeAdapter(GeometryPolyIndex.class, GeometryPolyIndex.gsonDeserializer())
            .registerTypeAdapter(GeometryPolyIndices.class, GeometryPolyIndices.gsonDeserializer())
            .registerTypeAdapter(GeometryPolyMesh.class, GeometryPolyMesh.gsonDeserializer())
            .registerTypeAdapter(GeometryTextureMesh.class, GeometryTextureMesh.gsonDeserializer())
            .registerTypeAdapter(GeometryUv.class, GeometryUv.gsonDeserializer())
            .registerTypeAdapter(GeometryUvMapping.class, GeometryUvMapping.gsonDeserializer())
            .registerTypeAdapter(GeometryUvMappingDetails.class, GeometryUvMappingDetails.gsonDeserializer())
            .registerTypeAdapter(GeometryUvPair.class, GeometryUvPair.gsonDeserializer())
            .create();

    /**
     * Parse a Geometry instance from raw .json input via {@link Gson}
     */
    public static JsonDeserializer<Geometry> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final String version = GsonHelper.getAsString(obj, "format_version");
            final boolean debug = GsonHelper.getAsBoolean(obj, "debug", false);
            final GeometryDefinition[] definitions = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "minecraft:geometry", new JsonArray(0)), context, GeometryDefinition.class);

            if (definitions.length == 0)
                GeckoLibConstants.LOGGER.warn("No geometry definitions found in model file!");

            return new Geometry(version, debug, definitions);
        };
    }
}
