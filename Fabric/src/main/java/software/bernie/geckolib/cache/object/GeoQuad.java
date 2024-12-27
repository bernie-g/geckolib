package software.bernie.geckolib.cache.object;

import org.joml.Vector3f;

import net.minecraft.core.Direction;
import software.bernie.geckolib.loading.json.raw.FaceUV;

/**
 * Quad data holder
 */
public record GeoQuad(GeoVertex[] vertices, Vector3f normal, Direction direction) {
	@Deprecated(forRemoval = true)
	public static GeoQuad build(GeoVertex[] vertices, double[] uvCoords, double[] uvSize, float texWidth, float texHeight, boolean mirror, Direction direction) {
		return build(vertices, (float)uvCoords[0], (float)uvCoords[1], (float)uvSize[0], (float)uvSize[1], texWidth, texHeight, mirror, direction);
	}

	@Deprecated(forRemoval = true)
	public static GeoQuad build(GeoVertex[] vertices, float u, float v, float uSize, float vSize, float texWidth, float texHeight, boolean mirror, Direction direction) {
		return build(vertices, u, v, uSize, vSize, FaceUV.Rotation.NONE, texWidth, texHeight, mirror, direction);
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

		return new GeoQuad(vertices, normal, direction);
	}
}

