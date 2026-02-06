package software.bernie.geckolib.cache.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.loading.json.raw.FaceUV;

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

	public static GeoQuad build(GeoVertex[] vertices, double[] uvCoords, double[] uvSize, FaceUV.Rotation uvRotation, float texWidth, float texHeight, boolean mirror, Direction direction) {
		return build(vertices, (float)uvCoords[0], (float)uvCoords[1], (float)uvSize[0], (float)uvSize[1], uvRotation, texWidth, texHeight, mirror, direction);
	}

	public static GeoQuad build(GeoVertex[] vertices, float u, float v, float uSize, float vSize, FaceUV.Rotation uvRotation, float texWidth, float texHeight, boolean mirror, Direction direction) {
		float uWidth = (u + uSize) / texWidth;
		float vHeight = (v + vSize) / texHeight;
		u /= texWidth;
		v /= texHeight;
		Vector3f normal = direction.step();

		if (!mirror) {
			float tempWidth = uWidth;
			uWidth = u;
			u = tempWidth;
		}
		else {
			normal.mul(-1, 1, 1);
		}

		float[] uvs = uvRotation.rotateUvs(u, v, uWidth, vHeight);
		vertices[0] = vertices[0].withUVs(uvs[0], uvs[1]);
		vertices[1] = vertices[1].withUVs(uvs[2], uvs[3]);
		vertices[2] = vertices[2].withUVs(uvs[4], uvs[5]);
		vertices[3] = vertices[3].withUVs(uvs[6], uvs[7]);

		return new GeoQuad(vertices, normal.x, normal.y, normal.z, direction);
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
