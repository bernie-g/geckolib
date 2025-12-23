package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.util.JsonUtil;

import java.util.Optional;

/**
 * Container class for a single geometry bone's polygon indices array, only used for intermediary steps between .json deserialization and GeckoLib object creation
 * <p>
 * This information isn't used by GeckoLib natively
 *
 * @param trisOrQuads The polygon indices array for this mesh, either tris {@code (n=3)} or quads {@code (n=4)}
 * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
 */
@ApiStatus.Internal
public record GeometryPolyIndices(Either<GeometryPolyIndex[], GeometryPolyIndex[]> trisOrQuads) {
    /**
     * Get the mesh tris indices array, if present
     */
    public Optional<GeometryPolyIndex[]> tris() {
        return this.trisOrQuads.left();
    }

    /**
     * Get the mesh quad indices array, if present
     */
    public Optional<GeometryPolyIndex[]> quads() {
        return this.trisOrQuads.right();
    }

    /**
     * Parse a GeometryPolyIndices instance from raw .json input via {@link Gson}
     */
    public static JsonDeserializer<GeometryPolyIndices> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonArray array = json.getAsJsonArray();
            final GeometryPolyIndex[] indices = JsonUtil.jsonArrayToObjectArray(array, context, GeometryPolyIndex.class);
            final Either<GeometryPolyIndex[], GeometryPolyIndex[]> trisOrQuads = array.isEmpty() || array.get(0).getAsJsonArray().size() == 3 ?
                                                                                 Either.left(indices) :
                                                                                 Either.right(indices);

            return new GeometryPolyIndices(trisOrQuads);
        };
    }
}
