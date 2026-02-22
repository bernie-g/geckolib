package com.geckolib.cache.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/// Quad data holder
///
/// @param vertices The vertex array for this quad
/// @param normalX The x-axis normal value for this quad
/// @param normalY The y-axis normal value for this quad
/// @param normalZ The z-axis normal value for this quad
/// @param direction The cube-face of this quad. This does not necessarily represent the actual direction the quad is facing
public record GeoQuad(GeoVertex[] vertices, float normalX, float normalY, float normalZ, Direction direction) {
    public Vector3f normalVec() {
        return new Vector3f(this.normalX, this.normalY, this.normalZ);
    }

    /// Submit this texture quad to the vertex consumer
    public void render(Matrix4f pose, Vector3f normal, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor) {
        for (GeoVertex vertex : vertices()) {
            Vector4f vector4f = pose.transform(new Vector4f(vertex.posX(), vertex.posY(), vertex.posZ(), 1));

            vertexConsumer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), renderColor, vertex.texU(),
                                     vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        }
    }
}
