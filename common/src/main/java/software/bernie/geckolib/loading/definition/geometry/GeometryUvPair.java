package software.bernie.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.model.GeoQuad;
import software.bernie.geckolib.loading.definition.geometry.object.GeometryQuadUvs;
import software.bernie.geckolib.loading.definition.geometry.object.UvFaceRotation;
import software.bernie.geckolib.loading.definition.geometry.object.VertexSet;

/// A simple U/V coordinate pair container
///
/// @param u The U coordinate for this pair
/// @param v The V coordinate for this pair
public record GeometryUvPair(double u, double v) {
    /// Parse a GeometryUvPair instance from raw .json input via [Gson]
    public static JsonDeserializer<GeometryUvPair> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonArray uv = json.getAsJsonArray();
            final double u = uv.get(0).getAsDouble();
            final double v = uv.get(1).getAsDouble();

            return new GeometryUvPair(u, v);
        };
    }

    /// Bake this uv information into a `GeoQuad` for the given direction
    public GeoQuad bakeQuad(VertexSet vertices, Vec3 cubeSize, Direction direction, boolean mirror, int textureWidth, int textureHeight) {
        final Vec3 uvSize = new Vec3(Mth.floor(cubeSize.x), Mth.floor(cubeSize.y), Mth.floor(cubeSize.z));
        final GeometryQuadUvs uvData = GeometryQuadUvs.ofBoxUv(direction, this.u, this.v, uvSize);

        return uvData.bakeQuad(vertices, UvFaceRotation.NONE, true, textureWidth, textureHeight, mirror, direction);
    }
}
