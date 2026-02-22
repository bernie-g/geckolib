package com.geckolib.loading.definition.geometry.object;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import com.geckolib.cache.model.GeoQuad;
import com.geckolib.cache.model.GeoVertex;
import com.geckolib.loading.definition.geometry.GeometryUvPair;

/// Holder class for containing the pair of UV coordinates and UV size values for a single quad
public record GeometryQuadUvs(GeometryUvPair uvCoordinates, GeometryUvPair uvSize) {
    /// Create a `GeometryQuadUvs` instance from a given direction and UV details
    public static GeometryQuadUvs ofBoxUv(Direction direction, double u, double v, Vec3 uvSize) {
        return switch(direction) {
            case WEST -> new GeometryQuadUvs(new GeometryUvPair(u + uvSize.z + uvSize.x, v + uvSize.z),
                                             new GeometryUvPair(uvSize.z, uvSize.y));
            case EAST -> new GeometryQuadUvs(new GeometryUvPair(u, v + uvSize.z),
                                             new GeometryUvPair(uvSize.z, uvSize.y));
            case NORTH -> new GeometryQuadUvs(new GeometryUvPair(u + uvSize.z, v + uvSize.z),
                                             new GeometryUvPair(uvSize.x, uvSize.y));
            case SOUTH -> new GeometryQuadUvs(new GeometryUvPair(u + uvSize.z + uvSize.x + uvSize.z, v + uvSize.z),
                                             new GeometryUvPair(uvSize.x, uvSize.y));
            case UP -> new GeometryQuadUvs(new GeometryUvPair(u + uvSize.z, v),
                                             new GeometryUvPair(uvSize.x, uvSize.z));
            case DOWN -> new GeometryQuadUvs(new GeometryUvPair(u + uvSize.z + uvSize.x, v + uvSize.z),
                                             new GeometryUvPair(uvSize.x, -uvSize.z));
        };
    }

    /// Bake a `GeoQuad` instance from this `GeometryQuadUvs` instance and associated values
    public GeoQuad bakeQuad(VertexSet vertices, UvFaceRotation uvFaceRotation, boolean isBoxUvs, int textureWidth, int textureHeight, boolean mirror, Direction direction) {
        final GeoVertex[] verticesArray = vertices.verticesForQuad(direction, isBoxUvs, mirror);
        final Vector3f normal = direction.step();

        adjustVerticesForMirrorAndRotation(verticesArray, uvFaceRotation, normal, mirror, textureWidth, textureHeight);

        return new GeoQuad(verticesArray, normal.x, normal.y, normal.z, direction);
    }

    /// Adjust the vertices array and normal vector to account for potential mirroring and face rotation
    private void adjustVerticesForMirrorAndRotation(GeoVertex[] verticesArray, UvFaceRotation uvFaceRotation, Vector3f normal, boolean mirror, int textureWidth, int textureHeight) {
        final double uSize = this.uvSize.u();
        final double vSize = this.uvSize.v();
        double u = this.uvCoordinates.u();
        double v = this.uvCoordinates.v();
        double uWidth = (u + uSize) / textureWidth;
        double vHeight = (v + vSize) / textureHeight;
        u /= textureWidth;
        v /= textureHeight;

        if (!mirror) {
            double tempWidth = uWidth;
            uWidth = u;
            u = tempWidth;
        }
        else {
            normal.mul(-1, 1, 1);
        }

        double[] uvs = uvFaceRotation.createRotatedUvs(u, v, uWidth, vHeight);
        verticesArray[0] = verticesArray[0].withUVs(uvs[0], uvs[1]);
        verticesArray[1] = verticesArray[1].withUVs(uvs[2], uvs[3]);
        verticesArray[2] = verticesArray[2].withUVs(uvs[4], uvs[5]);
        verticesArray[3] = verticesArray[3].withUVs(uvs[6], uvs[7]);
    }
}
