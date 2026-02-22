package com.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.cache.model.GeoQuad;
import com.geckolib.cache.model.cuboid.GeoCube;
import com.geckolib.loading.definition.geometry.object.VertexSet;
import com.geckolib.util.JsonUtil;

/// Container class for a single geometry cube, only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// @param origin The unrotated lower corner position of the cube, or null if not defined
/// @param size The size of the cube (in [model units][JsonUtil#worldToModelUnits(double)]), or null if not defined
/// @param rotation The rotation of the cube, in degrees, or null if not defined
/// @param pivot The pivot point of the cube, defaults to the cube's center, or null if not defined
/// @param inflate An optional inflation value for this cube
/// @param mirror An optional mirror toggle for this cube
/// @param uv The UV coordinate assignments for this cube
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
@ApiStatus.Internal
public record GeometryCube(@Nullable Vec3 origin, @Nullable Vec3 size, @Nullable Vec3 rotation, @Nullable Vec3 pivot, @Nullable Float inflate, @Nullable Boolean mirror, GeometryUv uv) {
    /// Parse a GeometryBone instance from raw .json input via [Gson]
    public static JsonDeserializer<GeometryCube> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final Vec3 origin = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "origin", null));
            final Vec3 size = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "size", null));
            final Vec3 rotation = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "rotation", null));
            final Vec3 pivot = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "pivot", null));
            final Float inflate = JsonUtil.getOptionalFloat(obj, "inflate");
            final Boolean mirror = JsonUtil.getOptionalBoolean(obj, "mirror");
            final GeometryUv uv = GsonHelper.getAsObject(obj, "uv", null, context, GeometryUv.class);

            return new GeometryCube(origin, size, rotation, pivot, inflate, mirror, uv);
        };
    }

    /// Bake this `GeometryCube` instance into the final [GeoCube] instance that GeckoLib uses for rendering
    public GeoCube bake(GeometryBone geometryBone, GeometryDescription geometryDescription) {
        final boolean mirror = this.mirror != null ? this.mirror : geometryBone.mirror() == Boolean.TRUE;
        final float inflate = (float)JsonUtil.modelToWorldUnits(this.inflate != null ? this.inflate : geometryBone.inflate() != null ? geometryBone.inflate() : 0);
        final double modelScale = JsonUtil.modelToWorldUnits(1);
        final Vec3 size = this.size == null ? Vec3.ZERO : this.size;
        final Vec3 origin = this.origin == null ? Vec3.ZERO : this.origin.add(size.x, 0, 0).multiply(-modelScale, modelScale, modelScale);
        final Vec3 rotation = this.rotation == null ? Vec3.ZERO : this.rotation.multiply(-Mth.DEG_TO_RAD, -Mth.DEG_TO_RAD, Mth.DEG_TO_RAD);
        final Vec3 pivot = this.pivot == null ? Vec3.ZERO : this.pivot.multiply(-1, 1, 1);
        final Vec3 vertSize = size.scale(modelScale);
        final @Nullable GeoQuad[] quads = bakeQuads(origin, size, vertSize, inflate, mirror, geometryDescription.textureWidth(), geometryDescription.textureHeight());

        return new GeoCube(quads, pivot, rotation, size);
    }

    /// Compile and bake the quads array for this cube
    private @Nullable GeoQuad[] bakeQuads(Vec3 origin, Vec3 size, Vec3 vertSize, float inflate, boolean mirror, int textureWidth, int textureHeight) {
        final VertexSet vertices = new VertexSet(origin, vertSize, inflate);
        final @Nullable GeoQuad[] quads = new GeoQuad[6];

        quads[0] = buildQuad(vertices, size, textureWidth, textureHeight, mirror, Direction.WEST);
        quads[1] = buildQuad(vertices, size, textureWidth, textureHeight, mirror, Direction.EAST);
        quads[2] = buildQuad(vertices, size, textureWidth, textureHeight, mirror, Direction.NORTH);
        quads[3] = buildQuad(vertices, size, textureWidth, textureHeight, mirror, Direction.SOUTH);
        quads[4] = buildQuad(vertices, size, textureWidth, textureHeight, mirror, Direction.UP);
        quads[5] = buildQuad(vertices, size, textureWidth, textureHeight, mirror, Direction.DOWN);

        return quads;
    }

    /// Build an individual quad
    private @Nullable GeoQuad buildQuad(VertexSet vertices, Vec3 cubeSize, int textureWidth, int textureHeight, boolean mirror, Direction direction) {
        return this.uv.uvData().map(uvPair -> uvPair.bakeQuad(vertices, cubeSize, direction, mirror, textureWidth, textureHeight),
                                    uvMapping -> uvMapping.bakeQuad(vertices, cubeSize, direction, mirror, textureWidth, textureHeight));
    }
}
