package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;

import java.util.Optional;

/**
 * Basic container class for a U/V coordinate pair or per-face UV mapping
 *
 * @param uvData The container for the UV data
 */
public record GeometryUv(Either<GeometryUvPair, GeometryUvMapping> uvData) {
    /**
     * Get the uv value pair, if present
     */
    public Optional<GeometryUvPair> uv() {
        return this.uvData.left();
    }

    /**
     * Get the UV face mapping data, if present
     */
    public Optional<GeometryUvMapping> uvFaceMapping() {
        return this.uvData.right();
    }

    /**
     * Parse a GeometryUv instance from raw .json input via {@link Gson}
     */
    public static JsonDeserializer<GeometryUv> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final Either<GeometryUvPair, GeometryUvMapping> pair = json.isJsonArray() ?
                                                   Either.left(context.deserialize(json, GeometryUvPair.class)) :
                                                   Either.right(context.deserialize(json, GeometryUvMapping.class));

            return new GeometryUv(pair);
        };
    }
}
