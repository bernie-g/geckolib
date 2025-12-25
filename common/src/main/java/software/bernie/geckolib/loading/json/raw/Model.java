package software.bernie.geckolib.loading.json.raw;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib.util.JsonUtil;

/**
 * Container class for model information, only used in deserialization at startup
 *
 * @param formatVersion The <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemaslist?view=minecraft-bedrock-stable">geometry format version</a> for this model
 * @param minecraftGeometry The geometry array for this model. Typically only one geometry exists per model
 */
public record Model(String formatVersion, MinecraftGeometry[] minecraftGeometry) {
	public static JsonDeserializer<Model> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			JsonObject obj = json.getAsJsonObject();
			String formatVersion = obj.get("format_version").getAsString();
			MinecraftGeometry[] minecraftGeometry = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "minecraft:geometry", null), context, MinecraftGeometry.class);

			if (minecraftGeometry == null)
				minecraftGeometry = new MinecraftGeometry[0];

			return new Model(formatVersion, minecraftGeometry);
		};
	}
}
