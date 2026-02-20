package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.*;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.model.GeoQuad;
import software.bernie.geckolib.loading.definition.geometry.object.GeometryQuadUvs;
import software.bernie.geckolib.loading.definition.geometry.object.VertexSet;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/// A U/V coordinate mapping container, mapping a face to associated UV details
///
/// A face not being present in the mapping represents an absent face
///
/// @param uvFaces The U/V coordinate mapping for each face
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
public record GeometryUvMapping(EnumMap<Direction, GeometryUvMappingDetails> uvFaces) {
    /// Shortcut method to retrieve the [GeometryUvMappingDetails] for a given face
    ///
    /// This method should be preferred to direct map retrieval as it ensures null-safety awareness
    public @Nullable GeometryUvMappingDetails fromDirection(Direction direction) {
        return this.uvFaces.get(direction);
    }

    /// Parse a GeometryUvMapping instance from raw .json input via [Gson]
    public static JsonDeserializer<GeometryUvMapping> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final EnumMap<Direction, GeometryUvMappingDetails> uvFaces = new EnumMap<>(Direction.class);

            for (Map.Entry<String, JsonElement> face : obj.entrySet()) {
                final Direction direction = Direction.byName(face.getKey());

                if (direction == null)
                    throw new JsonParseException("Error while parsing UV values. Expected Direction value: (" + Arrays.toString(Direction.values()) + "), found " + face.getKey());

                uvFaces.put(direction, context.deserialize(face.getValue(), GeometryUvMappingDetails.class));
            }

            return new GeometryUvMapping(uvFaces);
        };
    }

    /// Bake this UV map into a [GeoQuad] for the given direction
    public @Nullable GeoQuad bakeQuad(VertexSet vertices, Vec3 cubeSize, Direction direction, boolean mirror, int textureWidth, int textureHeight) {
        final GeometryUvMappingDetails faceUV = fromDirection(direction);

        if (faceUV == null)
            return null;

        return new GeometryQuadUvs(faceUV.uv(), faceUV.uvSize())
                .bakeQuad(vertices, faceUV.uvRotation(), false, textureWidth, textureHeight, mirror, direction);
    }
}
