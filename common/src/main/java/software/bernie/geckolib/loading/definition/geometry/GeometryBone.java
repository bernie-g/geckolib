package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.util.JsonUtil;

import java.util.Map;

/**
 * Container class for a single geometry bone, only used for intermediary steps between .json deserialization and GeckoLib object creation
 *
 * @param name The name of this bone
 * @param parent The parent bone of this bone, if any
 * @param pivot The pivot point for this bone, or null if not defined
 * @param rotation The rotation of this bone, in degrees, or null if not defined
 * @param debug An optional debug marker for this bone. Not used by GeckoLib
 * @param mirror An optional mirror toggle for this bone
 * @param inflate An optional inflation value for this bone
 * @param renderGroupId The numerical group index this bone belongs to. Not used by GeckoLib
 * @param cubes The array of cube definitions for this bone
 * @param binding An optional binding for this bone, defining its parental relationship. Not used by GeckoLib
 * @param locators An optional map of locator markers for this bone
 * @param polyMesh An optional poly mesh definition for this bone. Not used by GeckoLib
 * @param textureMeshes An optional array of texture mesh definitions for this bone. Not used by GeckoLib
 * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
 */
@ApiStatus.Internal
public record GeometryBone(String name, @Nullable String parent, @Nullable Vec3 pivot, @Nullable Vec3 rotation, boolean debug,
                           boolean mirror, float inflate, int renderGroupId, GeometryCube[] cubes,
                           @Nullable String binding, @Nullable Map<String, GeometryLocator> locators, @Nullable GeometryPolyMesh polyMesh,
                           GeometryTextureMesh @Nullable[] textureMeshes) {
    /**
     * Parse a GeometryBone instance from raw .json input via {@link Gson}
     */
    public static JsonDeserializer<GeometryBone> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final String name = GsonHelper.getAsString(obj, "name");
            final String parent = GsonHelper.getAsString(obj, "parent", null);
            final Vec3 pivot = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "pivot", null));
            final Vec3 rotation = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "rotation", null));
            final boolean debug = GsonHelper.getAsBoolean(obj, "debug", false);
            final boolean mirror = GsonHelper.getAsBoolean(obj, "mirror", false);
            final float inflate = GsonHelper.getAsFloat(obj, "inflate", 0f);
            final int renderGroupId = GsonHelper.getAsInt(obj, "render_group_id", 0);
            final GeometryCube[] cubes = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "cubes", null), context, GeometryCube.class);
            final String binding = GsonHelper.getAsString(obj, "binding", null);
            final Map<String, GeometryLocator> locators = JsonUtil.jsonObjToMap(GsonHelper.getAsJsonObject(obj, "locators", null), context, GeometryLocator.class);
            final GeometryPolyMesh polyMesh = GsonHelper.getAsObject(obj, "poly_mesh", null, context, GeometryPolyMesh.class);
            final GeometryTextureMesh[] textureMeshes = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "texture_meshes", null), context, GeometryTextureMesh.class);

            return new GeometryBone(name, parent, pivot, rotation, debug, mirror, inflate, renderGroupId, cubes, binding, locators, polyMesh, textureMeshes);
        };
    }
}
