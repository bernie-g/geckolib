package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.util.JsonUtil;
import software.bernie.geckolib.util.MiscUtil;

/**
 * Container class for a single geometry cube, only used for intermediary steps between .json deserialization and GeckoLib object creation
 *
 * @param origin The unrotated lower corner position of the cube, or null if not defined
 * @param size The size of the cube (in {@link MiscUtil#MODEL_TO_WORLD_SIZE model units)}, or null if not defined
 * @param rotation The rotation of the cube, in degrees, or null if not defined
 * @param pivot The pivot point of the cube, defaults to the cube's center, or null if not defined
 * @param inflate An optional inflation value for this cube
 * @param mirror An optional mirror toggle for this cube
 * @param uv The UV coordinate assignments for this cube
 * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
 */
@ApiStatus.Internal
public record GeometryCube(@Nullable Vec3 origin, @Nullable Vec3 size, @Nullable Vec3 rotation, @Nullable Vec3 pivot, float inflate, boolean mirror, GeometryUv uv) {
    /**
     * Parse a GeometryBone instance from raw .json input via {@link Gson}
     */
    public static JsonDeserializer<GeometryCube> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final Vec3 origin = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "origin", null));
            final Vec3 size = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "size", null));
            final Vec3 rotation = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "rotation", null));
            final Vec3 pivot = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "pivot", null));
            final float inflate = GsonHelper.getAsFloat(obj, "inflate", 0f);
            final boolean mirror = GsonHelper.getAsBoolean(obj, "mirror", false);
            final GeometryUv uv = GsonHelper.getAsObject(obj, "uv", null, context, GeometryUv.class);

            return new GeometryCube(origin, size, rotation, pivot, inflate, mirror, uv);
        };
    }
}
