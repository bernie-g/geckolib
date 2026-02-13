package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.loading.definition.geometry.object.UvFaceRotation;

/// A U/V coordinate mapping container for a specific face
///
/// @param uv The U/V coordinate pair for this face
/// @param uvSize The U/V size of this face, if overriding the default 1:1 mapping
/// @param uvRotation The U/V rotation of this face, defaults to [UvFaceRotation#NONE]
/// @param materialInstance An optional material instance to use for this face. Not used by GeckoLib
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
public record GeometryUvMappingDetails(GeometryUvPair uv, @Nullable GeometryUvPair uvSize, UvFaceRotation uvRotation, @Nullable String materialInstance) {
    /// Parse a GeometryUvMappingDetails instance from raw .json input via [Gson]
    public static JsonDeserializer<GeometryUvMappingDetails> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final GeometryUvPair uv = GsonHelper.getAsObject(obj, "uv", context, GeometryUvPair.class);
            final GeometryUvPair uvSize = GsonHelper.getAsObject(obj, "uv_size", context, GeometryUvPair.class);
            final UvFaceRotation uvRotation = UvFaceRotation.fromDegrees(GsonHelper.getAsInt(obj, "uv_rotation", 0));
            final String materialInstance = GsonHelper.getAsString(obj, "material_instance", null);

            return new GeometryUvMappingDetails(uv, uvSize, uvRotation, materialInstance);
        };
    }
}
