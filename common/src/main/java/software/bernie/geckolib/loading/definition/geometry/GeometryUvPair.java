package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;

/**
 * A simple U/V coordinate pair container
 */
public record GeometryUvPair(double u, double v) {
    /**
     * Parse a GeometryUvPair instance from raw json input via {@link Gson}
     */
    public static JsonDeserializer<GeometryUvPair> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonArray uv = json.getAsJsonArray();
            final double u = uv.get(0).getAsDouble();
            final double v = uv.get(1).getAsDouble();

            return new GeometryUvPair(u, v);
        };
    }
}
