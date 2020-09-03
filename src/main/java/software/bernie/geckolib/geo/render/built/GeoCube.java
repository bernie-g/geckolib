package software.bernie.geckolib.geo.render.built;

import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Direction;
import software.bernie.geckolib.geo.raw.pojo.*;
import software.bernie.geckolib.util.VectorUtils;

public class GeoCube
{
	public GeoQuad[] quads = new GeoQuad[6];
	public Vector3f pivot;
	public Vector3f rotation;
	public Double inflate;
	public Boolean mirror;

	private GeoCube(){}

	public static GeoCube createFromPojoCube(Cube cubeIn, ModelProperties properties)
	{
		GeoCube cube = new GeoCube();

		UvUnion uvUnion = cubeIn.getUv();
		UvFaces faces = uvUnion.faceUV;
		boolean isBoxUV = uvUnion.isBoxUV;

		float textureHeight = properties.getTextureHeight().floatValue();
		float textureWidth = properties.getTextureWidth().floatValue();

		Vector3d origin = VectorUtils.fromArray(cubeIn.getOrigin());
		Vector3d size = VectorUtils.fromArray(cubeIn.getSize());

		Vector3f rotation = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(cubeIn.getRotation()));
		Vector3f pivot = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(cubeIn.getPivot()));

		cube.pivot = pivot;
		cube.rotation = rotation;
		cube.mirror = cubeIn.getMirror();
		cube.inflate = cubeIn.getInflate();


		//
		//
		//               P7                       P8
		//               - - - - - - - - - - - - -
		//               | \                     | \
		//               |   \                   |   \
		//               |     \                 |     \
		//               |       \               |       \
		//           Y   |         \             |         \
		//               |           \           |           \
		//               |             \ P3      |             \  P4
		//               |               - - - - - - - - - - - - -
		//               |               |       |               |
		//               |               |       |               |
		//               |               |       |               |
		//            P5 - - - - - - - - | - - - - P6            |
		//                 \             |         \             |
		//                   \           |           \           |
		//                     \         |             \         |
		//                  X    \       |               \       |
		//                         \     |                 \     |
		//                           \   |                   \   |
		//                             \ |                     \ |
		//                               - - - - - - - - - - - - -
		//                              P1                        P2
		//                                          Z
		//  this drawing corresponds to the points declared below
		//


		//Making all 8 points of the cube using the origin (where the Z, X, and Y values are smallest) and offseting each point by the right size values
		GeoVertex P1 = new GeoVertex(origin.x, origin.y, origin.z);
		GeoVertex P2 = new GeoVertex(origin.x, origin.y, origin.z + size.z);
		GeoVertex P3 = new GeoVertex(origin.x, origin.y + size.y, origin.z);
		GeoVertex P4 = new GeoVertex(origin.x, origin.y + size.y, origin.z + size.z);
		GeoVertex P5 = new GeoVertex(origin.x + size.x, origin.y, origin.z);
		GeoVertex P6 = new GeoVertex(origin.x + size.x, origin.y, origin.z + size.z);
		GeoVertex P7 = new GeoVertex(origin.x + size.x, origin.y + size.y, origin.z);
		GeoVertex P8 = new GeoVertex(origin.x + size.x, origin.y + size.y, origin.z + size.z);

		if(!isBoxUV)
		{
			FaceUv west = faces.getWest();
			FaceUv east = faces.getEast();
			FaceUv north = faces.getNorth();
			FaceUv south = faces.getSouth();
			FaceUv up = faces.getUp();
			FaceUv down = faces.getDown();


			//Pass in vertices starting from the top right corner, then going counter-clockwise
			GeoQuad quadWest = new GeoQuad(new GeoVertex[]{P4, P3, P1, P2}, west.getUv(), west.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.WEST);
			GeoQuad quadEast = new GeoQuad(new GeoVertex[]{P7, P8, P6, P5}, east.getUv(), east.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.EAST);
			GeoQuad quadNorth = new GeoQuad(new GeoVertex[]{P3, P7, P5, P1}, north.getUv(), north.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.NORTH);
			GeoQuad quadSouth = new GeoQuad(new GeoVertex[]{P8, P4, P2, P6}, south.getUv(), south.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.SOUTH);
			GeoQuad quadUp = new GeoQuad(new GeoVertex[]{P8, P7, P3, P4}, up.getUv(), up.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.UP);
			GeoQuad quadDown = new GeoQuad(new GeoVertex[]{P2, P1, P5, P6}, down.getUv(), down.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.DOWN);
			cube.quads[0] = quadWest;
			cube.quads[1] = quadEast;
			cube.quads[2] = quadNorth;
			cube.quads[3] = quadSouth;
			cube.quads[4] = quadUp;
			cube.quads[5] = quadDown;
		}

		return cube;
	}
}
