package software.bernie.geckolib3.geo.render.built;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

public class GeoQuad {
	public GeoVertex[] vertices;
	public final Vec3i normal;
	public EnumFacing direction;

	public GeoQuad(GeoVertex[] verticesIn, float u1, float v1, float uSize, float vSize, float texWidth,
			float texHeight, Boolean mirrorIn, EnumFacing directionIn) {
		this.direction = directionIn;
		this.vertices = verticesIn;

		/*
		 * u1 is the distance from the very left of the texture to where the uv region
		 * starts v1 is the distance from the very top of the texture to where the uv
		 * region starts u2 is the horizontal distance from u1 to where the uv region
		 * ends v2 is the vertical distance from the v1 to where the uv region ends
		 */

		float u2 = u1 + uSize;
		float v2 = v1 + vSize;

		// Normalize the coordinates to be relative (between 0 and 1)
		u1 /= texWidth;
		u2 /= texWidth;
		v1 /= texHeight;
		v2 /= texHeight;

		// u1, v1 - Top left corner of uv region
		// u2, v1 - Top right corner of uv region
		// u1, v2 - Bottom left corner of uv region
		// u2, v2 - Bottom right corner of uv region

		// Sets the new normalized texture coordinates of each vertex using the
		// positions described above
		if (mirrorIn != null && mirrorIn) {
			vertices[0] = verticesIn[0].setTextureUV(u1, v1); // Top left corner
			vertices[1] = verticesIn[1].setTextureUV(u2, v1); // Top right corner
			vertices[2] = verticesIn[2].setTextureUV(u2, v2); // Bottom left corner
			vertices[3] = verticesIn[3].setTextureUV(u1, v2); // Bottom right corner
		} else {
			vertices[0] = verticesIn[0].setTextureUV(u2, v1); // Top left corner
			vertices[1] = verticesIn[1].setTextureUV(u1, v1); // Top right corner
			vertices[2] = verticesIn[2].setTextureUV(u1, v2); // Bottom left corner
			vertices[3] = verticesIn[3].setTextureUV(u2, v2); // Bottom right corner
		}

		// only god knows what this does, but eliot told me it generates a normal vector
		// which helps the game do lighting properly or something idk i didnt pay
		// attention in physics we were in remote learning gimme a break
		this.normal = directionIn.getDirectionVec();
	}

	public GeoQuad(GeoVertex[] verticesIn, double[] uvCoords, double[] uvSize, float texWidth, float texHeight,
			Boolean mirrorIn, EnumFacing directionIn) {
		this(verticesIn, (float) uvCoords[0], (float) uvCoords[1], (float) uvSize[0], (float) uvSize[1], texWidth,
				texHeight, mirrorIn, directionIn);
	}
}
