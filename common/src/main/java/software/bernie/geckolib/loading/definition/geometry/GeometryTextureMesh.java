package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.util.JsonUtil;

/// Container class for a single geometry bone's texture mesh details, only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// This information isn't used by GeckoLib natively
///
/// @param texture The texture resource location for this mesh
/// @param position The optional position of the pivot point for this mesh _after_ rotation
/// @param localPivot The optional position of the pivot point for this mesh
/// @param rotation The optional rotation of this mesh (in degrees)
/// @param scale The optional scale of the texture on this mesh
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
@ApiStatus.Internal
public record GeometryTextureMesh(Identifier texture, @Nullable Vec3 position, @Nullable Vec3 localPivot, @Nullable Vec3 rotation, @Nullable Vec3 scale) {
    /// Parse a GeometryTextureMesh instance from raw .json input via [Gson]
    public static JsonDeserializer<GeometryTextureMesh> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final Identifier texture = Identifier.parse(GsonHelper.getAsString(obj, "texture"));
            final JsonArray position = GsonHelper.getAsJsonArray(obj, "position", null);
            final JsonArray localPivot = GsonHelper.getAsJsonArray(obj, "local_pivot", null);
            final JsonArray rotation = GsonHelper.getAsJsonArray(obj, "rotation", null);
            final JsonArray scale = GsonHelper.getAsJsonArray(obj, "scale", null);

            return new GeometryTextureMesh(texture,
                                           position == null ? null : JsonUtil.arrayToVec(JsonUtil.jsonArrayToDoubleArray(position)),
                                           localPivot == null ? null : JsonUtil.arrayToVec(JsonUtil.jsonArrayToDoubleArray(localPivot)),
                                           rotation == null ? null : JsonUtil.arrayToVec(JsonUtil.jsonArrayToDoubleArray(rotation)),
                                           scale == null ? null : JsonUtil.arrayToVec(JsonUtil.jsonArrayToDoubleArray(scale)));
        };
    }
}
