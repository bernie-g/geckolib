package software.bernie.geckolib3.loading.json.typeadapter;

import com.google.gson.*;
import com.mojang.math.Vector3f;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.geo.raw.pojo.FaceUv;
import software.bernie.geckolib3.geo.raw.pojo.UvFaces;
import software.bernie.geckolib3.geo.raw.pojo.UvUnion;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.util.VectorUtils;
import software.bernie.geckolib3.util.json.JsonUtil;

import java.lang.reflect.Type;

/**
 * {@link com.google.gson.Gson} {@link JsonDeserializer} for {@link software.bernie.geckolib3.geo.render.built.GeoCube GeoCubes}.<br>
 * Acts as the deserialization interface for {@code GeoCubes}
 */
public class GeoCubeAdapter implements JsonDeserializer<GeoCube> {
	@Override
	public GeoCube deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		Double inflate = JsonUtil.getOptionalDouble(obj, "inflate");
		Boolean mirror = JsonUtil.getOptionalBoolean(obj, "mirror");
		double[] origin = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "origin", null));
		double[] pivot = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "pivot", null));
		double[] rotation = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "rotation", null));
		double[] size = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "size", null));
		UvUnion uvUnion = obj.has("uv") ? context.deserialize(obj.get("uv"), UvUnion.class) : null;

		GeoQuad[] quads = new GeoQuad[6];

		UvFaces faces = uvUnion.faceUV;
		boolean isBoxUV = uvUnion.isBoxUV;
		cube.mirror = cubeIn.getMirror();
		cube.inflate = cubeIn.getInflate() == null ? (boneInflate == null ? 0 : boneInflate) : cubeIn.getInflate() / 16;

		float textureHeight = properties.getTextureHeight().floatValue();
		float textureWidth = properties.getTextureWidth().floatValue();

		Vec3 size = VectorUtils.fromArray(cubeIn.getSize());
		Vec3 origin = VectorUtils.fromArray(cubeIn.getOrigin());
		origin = new Vec3(-(origin.x + size.x) / 16, origin.y / 16, origin.z / 16);

		size = size.multiply(0.0625f, 0.0625, 0.0625f);

		Vector3f rotation = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(cubeIn.getRotation()));
		rotation.mul(-1, -1, 1);

		rotation.setX((float) Math.toRadians(rotation.x()));
		rotation.setY((float) Math.toRadians(rotation.y()));
		rotation.setZ((float) Math.toRadians(rotation.z()));

		Vector3f pivot = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(cubeIn.getPivot()));
		pivot.mul(-1, 1, 1);

		cube.pivot = pivot;
		cube.rotation = rotation;

		//
		//
		// P7 P8
		// - - - - - - - - - - - - -
		// | \ | \
		// | \ | \
		// | \ | \
		// | \ | \
		// Y | \ | \
		// | \ | \
		// | \ P3 | \ P4
		// | - - - - - - - - - - - - -
		// | | | |
		// | | | |
		// | | | |
		// P5 - - - - - - - - | - - - - P6 |
		// \ | \ |
		// \ | \ |
		// \ | \ |
		// X \ | \ |
		// \ | \ |
		// \ | \ |
		// \ | \ |
		// - - - - - - - - - - - - -
		// P1 P2
		// Z
		// this drawing corresponds to the points declared below
		//

		// Making all 8 points of the cube using the origin (where the Z, X, and Y
		// values are smallest) and offseting each point by the right size values
		GeoVertex P1 = new GeoVertex(origin.x - cube.inflate, origin.y - cube.inflate, origin.z - cube.inflate);
		GeoVertex P2 = new GeoVertex(origin.x - cube.inflate, origin.y - cube.inflate,
				origin.z + size.z + cube.inflate);
		GeoVertex P3 = new GeoVertex(origin.x - cube.inflate, origin.y + size.y + cube.inflate,
				origin.z - cube.inflate);
		GeoVertex P4 = new GeoVertex(origin.x - cube.inflate, origin.y + size.y + cube.inflate,
				origin.z + size.z + cube.inflate);
		GeoVertex P5 = new GeoVertex(origin.x + size.x + cube.inflate, origin.y - cube.inflate,
				origin.z - cube.inflate);
		GeoVertex P6 = new GeoVertex(origin.x + size.x + cube.inflate, origin.y - cube.inflate,
				origin.z + size.z + cube.inflate);
		GeoVertex P7 = new GeoVertex(origin.x + size.x + cube.inflate, origin.y + size.y + cube.inflate,
				origin.z - cube.inflate);
		GeoVertex P8 = new GeoVertex(origin.x + size.x + cube.inflate, origin.y + size.y + cube.inflate,
				origin.z + size.z + cube.inflate);

		GeoQuad quadWest;
		GeoQuad quadEast;
		GeoQuad quadNorth;
		GeoQuad quadSouth;
		GeoQuad quadUp;
		GeoQuad quadDown;

		if (!isBoxUV) {
			FaceUv west = faces.getWest();
			FaceUv east = faces.getEast();
			FaceUv north = faces.getNorth();
			FaceUv south = faces.getSouth();
			FaceUv up = faces.getUp();
			FaceUv down = faces.getDown();
			// Pass in vertices starting from the top right corner, then going
			// counter-clockwise
			quadWest = west == null ? null
					: new GeoQuad(new GeoVertex[] { P4, P3, P1, P2 }, west.getUv(), west.getUvSize(), textureWidth,
					textureHeight, cubeIn.getMirror(), Direction.WEST);
			quadEast = east == null ? null
					: new GeoQuad(new GeoVertex[] { P7, P8, P6, P5 }, east.getUv(), east.getUvSize(), textureWidth,
					textureHeight, cubeIn.getMirror(), Direction.EAST);
			quadNorth = north == null ? null
					: new GeoQuad(new GeoVertex[] { P3, P7, P5, P1 }, north.getUv(), north.getUvSize(), textureWidth,
					textureHeight, cubeIn.getMirror(), Direction.NORTH);
			quadSouth = south == null ? null
					: new GeoQuad(new GeoVertex[] { P8, P4, P2, P6 }, south.getUv(), south.getUvSize(), textureWidth,
					textureHeight, cubeIn.getMirror(), Direction.SOUTH);
			quadUp = up == null ? null
					: new GeoQuad(new GeoVertex[] { P4, P8, P7, P3 }, up.getUv(), up.getUvSize(), textureWidth,
					textureHeight, cubeIn.getMirror(), Direction.UP);
			quadDown = down == null ? null
					: new GeoQuad(new GeoVertex[] { P1, P5, P6, P2 }, down.getUv(), down.getUvSize(), textureWidth,
					textureHeight, cubeIn.getMirror(), Direction.DOWN);

			if (cubeIn.getMirror() == Boolean.TRUE || mirror == Boolean.TRUE) {
				quadWest = west == null ? null
						: new GeoQuad(new GeoVertex[] { P7, P8, P6, P5 }, west.getUv(), west.getUvSize(), textureWidth,
						textureHeight, cubeIn.getMirror(), Direction.WEST);
				quadEast = east == null ? null
						: new GeoQuad(new GeoVertex[] { P4, P3, P1, P2 }, east.getUv(), east.getUvSize(), textureWidth,
						textureHeight, cubeIn.getMirror(), Direction.EAST);
				quadNorth = north == null ? null
						: new GeoQuad(new GeoVertex[] { P3, P7, P5, P1 }, north.getUv(), north.getUvSize(), textureWidth,
						textureHeight, cubeIn.getMirror(), Direction.NORTH);
				quadSouth = south == null ? null
						: new GeoQuad(new GeoVertex[] { P8, P4, P2, P6 }, south.getUv(), south.getUvSize(), textureWidth,
						textureHeight, cubeIn.getMirror(), Direction.SOUTH);
				quadUp = up == null ? null
						: new GeoQuad(new GeoVertex[] { P1, P5, P6, P2 }, up.getUv(), up.getUvSize(), textureWidth,
						textureHeight, cubeIn.getMirror(), Direction.UP);
				quadDown = down == null ? null
						: new GeoQuad(new GeoVertex[] { P4, P8, P7, P3 }, down.getUv(), down.getUvSize(), textureWidth,
						textureHeight, cubeIn.getMirror(), Direction.DOWN);
			}
		} else {
			double[] UV = cubeIn.getUv().boxUVCoords;
			Vec3 UVSize = VectorUtils.fromArray(cubeIn.getSize());
			UVSize = new Vec3(Math.floor(UVSize.x), Math.floor(UVSize.y), Math.floor(UVSize.z));

			quadWest = new GeoQuad(new GeoVertex[] { P4, P3, P1, P2 },
					new double[] { UV[0] + UVSize.z + UVSize.x, UV[1] + UVSize.z }, new double[] { UVSize.z, UVSize.y },
					textureWidth, textureHeight, cubeIn.getMirror(), Direction.WEST);
			quadEast = new GeoQuad(new GeoVertex[] { P7, P8, P6, P5 }, new double[] { UV[0], UV[1] + UVSize.z },
					new double[] { UVSize.z, UVSize.y }, textureWidth, textureHeight, cubeIn.getMirror(),
					Direction.EAST);
			quadNorth = new GeoQuad(new GeoVertex[] { P3, P7, P5, P1 },
					new double[] { UV[0] + UVSize.z, UV[1] + UVSize.z }, new double[] { UVSize.x, UVSize.y },
					textureWidth, textureHeight, cubeIn.getMirror(), Direction.NORTH);
			quadSouth = new GeoQuad(new GeoVertex[] { P8, P4, P2, P6 },
					new double[] { UV[0] + UVSize.z + UVSize.x + UVSize.z, UV[1] + UVSize.z },
					new double[] { UVSize.x, UVSize.y }, textureWidth, textureHeight, cubeIn.getMirror(),
					Direction.SOUTH);
			quadUp = new GeoQuad(new GeoVertex[] { P4, P8, P7, P3 }, new double[] { UV[0] + UVSize.z, UV[1] },
					new double[] { UVSize.x, UVSize.z }, textureWidth, textureHeight, cubeIn.getMirror(), Direction.UP);
			quadDown = new GeoQuad(new GeoVertex[] { P1, P5, P6, P2 },
					new double[] { UV[0] + UVSize.z + UVSize.x, UV[1] + UVSize.z },
					new double[] { UVSize.x, -UVSize.z }, textureWidth, textureHeight, cubeIn.getMirror(),
					Direction.DOWN);

			if (cubeIn.getMirror() == Boolean.TRUE || mirror == Boolean.TRUE) {
				quadWest = new GeoQuad(new GeoVertex[] { P7, P8, P6, P5 },
						new double[] { UV[0] + UVSize.z + UVSize.x, UV[1] + UVSize.z },
						new double[] { UVSize.z, UVSize.y }, textureWidth, textureHeight, cubeIn.getMirror(),
						Direction.WEST);
				quadEast = new GeoQuad(new GeoVertex[] { P4, P3, P1, P2 }, new double[] { UV[0], UV[1] + UVSize.z },
						new double[] { UVSize.z, UVSize.y }, textureWidth, textureHeight, cubeIn.getMirror(),
						Direction.EAST);
				quadNorth = new GeoQuad(new GeoVertex[] { P3, P7, P5, P1 },
						new double[] { UV[0] + UVSize.z, UV[1] + UVSize.z }, new double[] { UVSize.x, UVSize.y },
						textureWidth, textureHeight, cubeIn.getMirror(), Direction.NORTH);
				quadSouth = new GeoQuad(new GeoVertex[] { P8, P4, P2, P6 },
						new double[] { UV[0] + UVSize.z + UVSize.x + UVSize.z, UV[1] + UVSize.z },
						new double[] { UVSize.x, UVSize.y }, textureWidth, textureHeight, cubeIn.getMirror(),
						Direction.SOUTH);
				quadUp = new GeoQuad(new GeoVertex[] { P4, P8, P7, P3 }, new double[] { UV[0] + UVSize.z, UV[1] },
						new double[] { UVSize.x, UVSize.z }, textureWidth, textureHeight, cubeIn.getMirror(), Direction.UP);
				quadDown = new GeoQuad(new GeoVertex[] { P1, P5, P6, P2 },
						new double[] { UV[0] + UVSize.z + UVSize.x, UV[1] + UVSize.z },
						new double[] { UVSize.x, -UVSize.z }, textureWidth, textureHeight, cubeIn.getMirror(),
						Direction.DOWN);
			}
		}

		quads[0] = quadWest;
		quads[1] = quadEast;
		quads[2] = quadNorth;
		quads[3] = quadSouth;
		quads[4] = quadUp;
		quads[5] = quadDown;
		
		return cube;
	}
}
