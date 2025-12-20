package software.bernie.geckolib.loading.json.raw;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.loading.definition.geometry.GeometryDescription;
import software.bernie.geckolib.util.JsonUtil;

/**
 * Container class for generic geometry information, only used in deserialization at startup
 *
 * @param bones The raw bone array for this geometry
 * @param cape The cape name for this geometry, not used by GeckoLib
 * @param geometryDescription The additional model properties for this geometry, if present
 */
public record MinecraftGeometry(Bone[] bones, @Nullable String cape, @Nullable GeometryDescription geometryDescription) {
	public static JsonDeserializer<MinecraftGeometry> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			final JsonObject obj = json.getAsJsonObject();
			final Bone[] bones = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "bones", new JsonArray(0)), context, Bone.class);
			final String cape = GsonHelper.getAsString(obj, "cape", null);
			final GeometryDescription geometryDescription = GsonHelper.getAsObject(obj, "description", null, context, GeometryDescription.class);

			return new MinecraftGeometry(bones, cape, geometryDescription);
		};
	}
}
