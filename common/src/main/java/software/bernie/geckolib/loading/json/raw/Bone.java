package software.bernie.geckolib.loading.json.raw;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.util.JsonUtil;

import java.util.Map;

/**
 * Container class for bone information, only used in deserialization at startup
 *
 * @param cubes The unbaked cubes array for this bone
 * @param debug An optional debug marker for this bone, not used by GeckoLib
 * @param inflate The optional inflation value for this bone
 * @param locators The map of locator markers by their name contained by this bone
 * @param mirror An optional mirror toggle for this bone, not used by GeckoLib
 * @param name The name of this bone, or null if unnamed
 * @param neverRender An optional render toggle for this bone, not used by GeckoLib
 * @param parent The name of the parent bone for this bone
 * @param pivot The x/y/z pivot position of this bone
 * @param polyMesh An optional mesh definition for this bone, not used by GeckoLib
 * @param renderGroupId The numerical group id this bone belongs to, not used by GeckoLib
 * @param rotation The x/y/z rotation of this bone
 * @param textureMeshes An optional texture mesh definition for this bone, not used by GeckoLib
 */
public record Bone(Cube[] cubes, @Nullable Boolean debug, // TODO Check support for current bedrock format values
				   @Nullable Double inflate, @Nullable Map<String, LocatorValue> locators,
				   @Nullable Boolean mirror, @Nullable String name, @Nullable Boolean neverRender,
				   @Nullable String parent, double[] pivot, @Nullable PolyMesh polyMesh,
				   @Nullable Long renderGroupId, double[] rotation,
				   @Nullable TextureMesh[] textureMeshes) {
	public static JsonDeserializer<Bone> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			JsonObject obj = json.getAsJsonObject();
			Cube[] cubes = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "cubes", new JsonArray(0)), context, Cube.class);
			Boolean debug = JsonUtil.getOptionalBoolean(obj, "debug");
			Double inflate = JsonUtil.getOptionalDouble(obj, "inflate");
			Map<String, LocatorValue> locators = obj.has("locators") ? JsonUtil.jsonObjToMap(GsonHelper.getAsJsonObject(obj, "locators"), context, LocatorValue.class) : null;
			Boolean mirror = JsonUtil.getOptionalBoolean(obj, "mirror");
			String name = GsonHelper.getAsString(obj, "name", null);
			Boolean neverRender = JsonUtil.getOptionalBoolean(obj, "neverRender");
			String parent = GsonHelper.getAsString(obj, "parent", null);
			double[] pivot = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "pivot", new JsonArray(0)));
			PolyMesh polyMesh = GsonHelper.getAsObject(obj, "poly_mesh", null, context, PolyMesh.class);
			Long renderGroupId = JsonUtil.getOptionalLong(obj, "render_group_id");
			double[] rotation = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "rotation", null));
			TextureMesh[] textureMeshes = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "texture_meshes", new JsonArray(0)), context, TextureMesh.class);

			return new Bone(cubes, debug, inflate, locators, mirror, name, neverRender, parent, pivot, polyMesh, renderGroupId, rotation, textureMeshes);
		};
	}
}
