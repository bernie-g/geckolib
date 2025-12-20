package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.util.JsonUtil;

import java.util.Map;

/**
 * Container class for a single geometry bone's locators collection, only used for intermediary steps between .json deserialization and GeckoLib object creation
 * <p>
 * This information isn't used by GeckoLib natively
 *
 * @param locators The locators collection for this bone
 * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
 */
@ApiStatus.Internal
public record GeometryLocators(Map<String, GeometryLocator> locators) {
    /**
     * Parse a GeometryLocators instance from raw json input via {@link Gson}
     */
    public static JsonDeserializer<GeometryLocators> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final Map<String, GeometryLocator> locators = JsonUtil.jsonObjToMap(obj, context, GeometryLocator.class);

            return new GeometryLocators(locators);
        };
    }
}
