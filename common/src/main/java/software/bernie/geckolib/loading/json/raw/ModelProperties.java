package software.bernie.geckolib.loading.json.raw;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.util.JsonUtil;

/**
 * Container class for model property information, only used in deserialization at startup
 *
 * @param identifier The asset identifier for this model. Not used by GeckoLib
 * @param visibleBoundsWidth The width of the visible bounds for this model. Not used by GeckoLib
 * @param visibleBoundsHeight The height of the visible bounds for this model. Not used by GeckoLib
 * @param visibleBoundsOffset The offset of the visible bounds for this model. Not used by GeckoLib
 * @param textureWidth The width of the texture for this model. Technically optional, but GeckoLib requires it
 * @param textureHeight The height of the texture for this model. Technically optional, but GeckoLib requires it
 * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
 */
public record ModelProperties(String identifier, @Nullable Float visibleBoundsWidth, @Nullable Float visibleBoundsHeight, @Nullable Vec3 visibleBoundsOffset,
							  int textureWidth, int textureHeight) {
	public static JsonDeserializer<ModelProperties> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			final JsonObject obj = json.getAsJsonObject();
			final String identifier = GsonHelper.getAsString(obj, "identifier");
			final Float visibleBoundsWidth = JsonUtil.getOptionalFloat(obj, "visible_bounds_width");
			final Float visibleBoundsHeight = JsonUtil.getOptionalFloat(obj, "visible_bounds_height");
			final double[] visibleBoundsOffset = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "visible_bounds_offset", null));
			final int textureWidth = GsonHelper.getAsInt(obj, "texture_width", 16);
			final int textureHeight = GsonHelper.getAsInt(obj, "texture_height", 16);

			if (!obj.has("texture_width") || !obj.has("texture_height"))
				GeckoLibConstants.LOGGER.warn("GeckoLib model {} does not have texture dimensions specified, likely an invalid geometry json!", identifier);

			return new ModelProperties(identifier == null ? String.valueOf(obj.hashCode()) : identifier,
									   visibleBoundsWidth, visibleBoundsHeight, new Vec3(visibleBoundsOffset[0], visibleBoundsOffset[1], visibleBoundsOffset[2]),
									   textureWidth, textureHeight);
		};
	}
}
