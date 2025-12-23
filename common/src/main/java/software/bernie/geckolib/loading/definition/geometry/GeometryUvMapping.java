package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.*;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.Map;

/**
 * A U/V coordinate mapping container, mapping a face to associated UV details
 * <p>
 * A face not being present in the mapping represents an absent face
 *
 * @param uvFaces The U/V coordinate mapping for each face
 * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
 */
public record GeometryUvMapping(EnumMap<Direction, GeometryUvMappingDetails> uvFaces) {
    /**
     * Parse a GeometryUvMapping instance from raw .json input via {@link Gson}
     */
    public static JsonDeserializer<GeometryUvMapping> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final EnumMap<Direction, GeometryUvMappingDetails> uvFaces = new EnumMap<>(Direction.class);

            for (Map.Entry<String, JsonElement> face : obj.entrySet()) {
                final Direction direction = Direction.byName(face.getKey());

                uvFaces.put(direction, context.deserialize(face.getValue(), GeometryUvMappingDetails.class));
            }

            return new GeometryUvMapping(uvFaces);
        };
    }
}
