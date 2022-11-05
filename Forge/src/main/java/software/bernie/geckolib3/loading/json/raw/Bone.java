package software.bernie.geckolib3.loading.json.raw;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib3.util.json.JsonUtil;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Container class for cube information, only used in deserialization at startup
 */
public record Bone(double[] bindPoseRotation, @Nullable Cube[] cubes, @Nullable Boolean debug,
				   @Nullable Double inflate, @Nullable Map<String, LocatorValue> locators,
				   @Nullable Boolean mirror, @Nullable String name, @Nullable Boolean neverRender,
				   @Nullable String parent, double[] pivot, @Nullable PolyMesh polyMesh,
				   @Nullable Long renderGroupId, @Nullable Boolean reset, double[] rotation,
				   @Nullable TextureMesh[] textureMeshes) {
	public static JsonDeserializer<Bone> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			JsonObject obj = json.getAsJsonObject();
			double[] bindPoseRotation = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "bind_pose_rotation", null));
			Cube[] cubes = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "cubes", null), context, Cube.class);
			Boolean debug = JsonUtil.getOptionalBoolean(obj, "debug");
			Double inflate = JsonUtil.getOptionalDouble(obj, "inflate");
			Map<String, LocatorValue> locators = obj.has("locators") ? JsonUtil.jsonObjToMap(GsonHelper.getAsJsonObject(obj, "locators"), context, LocatorValue.class) : null;
			Boolean mirror = JsonUtil.getOptionalBoolean(obj, "mirror");
			String name = GsonHelper.getAsString(obj, "name", null);
			Boolean neverRender = JsonUtil.getOptionalBoolean(obj, "neverRender");
			String parent = GsonHelper.getAsString(obj, "parent", null);
			double[] pivot = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "pivot", null));
			PolyMesh polyMesh = GsonHelper.getAsObject(obj, "poly_mesh", null, context, PolyMesh.class);
			Long renderGroupId = JsonUtil.getOptionalLong(obj, "render_group_id");
			Boolean reset = JsonUtil.getOptionalBoolean(obj, "reset");
			double[] rotation = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "rotation", null));
			TextureMesh[] textureMeshes = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "texture_meshes", null), context, TextureMesh.class);

			return new Bone(bindPoseRotation, cubes, debug, inflate, locators, mirror, name, neverRender, parent, pivot, polyMesh, renderGroupId, reset, rotation, textureMeshes);
		};
	}
}
