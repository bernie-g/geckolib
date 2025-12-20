package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;

/**
 * Basic container class for a U/V coordinate pair or per-face UV mapping
 */
public record GeometryUv(Either<GeometryUvPair, GeometryUvMapping> uvData) {
    /**
     * Parse a GeometryUv instance from raw json input via {@link Gson}
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
