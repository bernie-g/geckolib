package software.bernie.geckolib3.loading.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.cache.object.*;
import software.bernie.geckolib3.loading.json.raw.*;
import software.bernie.geckolib3.util.GeckoLibUtil;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Base interface for a factory of {@link BakedGeoModel} objects.
 * Handled by default by GeckoLib, but custom implementations may be added by other mods for special needs
 */
public interface BakedModelFactory {
	final Map<String, BakedModelFactory> FACTORIES = new Object2ObjectOpenHashMap<>(1);
	final BakedModelFactory DEFAULT_FACTORY = new Builtin();

	/**
	 * Construct the output model from the given {@link GeometryTree}.<br>
	 */
	BakedGeoModel constructGeoModel(GeometryTree geometryTree);

	/**
	 * Construct a {@link GeoBone} from the relevant raw input data
	 * @param boneStructure The {@code BoneStructure} comprising the structure of the bone and its children
	 * @param properties The loaded properties for the model
	 * @param parent The parent bone for this bone, or null if a top-level bone
	 */
	GeoBone constructBone(BoneStructure boneStructure, ModelProperties properties, @Nullable GeoBone parent);

	/**
	 * Construct a {@link GeoCube} from the relevant raw input data
	 * @param cube The raw {@code Cube} comprising the structure and properties of the cube
	 * @param properties The loaded properties for the model
	 * @param bone The bone this cube belongs to
	 */
	GeoCube constructCube(Cube cube, ModelProperties properties, GeoBone bone);

	/**
	 * Builtin method to construct the quad list from the various vertices and related data, to make it easier.<br>
	 * Vertices have already been mirrored here if {@code mirror} is true
	 */
	default GeoQuad[] buildQuads(UVUnion uvUnion, GeoVertex[] vertices, Cube cube, float textureWidth, float textureHeight, boolean mirror) {
		GeoQuad[] quads = new GeoQuad[6];
		boolean cubeMirror = cube.mirror() == Boolean.TRUE;

		quads[0] = buildQuad(new GeoVertex[] {vertices[3], vertices[2], vertices[0], vertices[1]}, cube, uvUnion, textureWidth, textureHeight, cubeMirror, Direction.WEST);
		quads[1] = buildQuad(new GeoVertex[]{vertices[4], vertices[5], vertices[7], vertices[6]}, cube, uvUnion, textureWidth, textureHeight, cubeMirror, Direction.EAST);
		quads[2] = buildQuad(new GeoVertex[]{vertices[2], vertices[4], vertices[6], vertices[0]}, cube, uvUnion, textureWidth, textureHeight, cubeMirror, Direction.NORTH);
		quads[3] = buildQuad(new GeoVertex[]{vertices[5], vertices[3], vertices[1], vertices[7]}, cube, uvUnion, textureWidth, textureHeight, cubeMirror, Direction.SOUTH);
		quads[4] = buildQuad(new GeoVertex[]{vertices[3], vertices[5], vertices[4], vertices[2]}, cube, uvUnion, textureWidth, textureHeight, cubeMirror, Direction.UP);
		quads[5] = buildQuad(new GeoVertex[]{vertices[0], vertices[6], vertices[7], vertices[1]}, cube, uvUnion, textureWidth, textureHeight, cubeMirror, Direction.DOWN);

		return quads;
	}

	/**
	 * Build an individual quad
	 */
	default GeoQuad buildQuad(GeoVertex[] vertices, Cube cube, UVUnion uvUnion, float textureWidth, float textureHeight, boolean mirror, Direction direction) {
		if (!uvUnion.isBoxUV()) {
			FaceUV faceUV = uvUnion.faceUV().fromDirection(direction);

			return GeoQuad.build(vertices, faceUV.uv(), faceUV.uvSize(), textureWidth, textureHeight, mirror, direction);
		}

		double[] uv = cube.uv().boxUVCoords();
		double[] uvSize = cube.size();
		Vec3 uvSizeVec = new Vec3(Math.floor(uvSize[0]), Math.floor(uvSize[1]), Math.floor(uvSize[2]));
		double[][] uvData = switch(direction) {
			case WEST -> new double[][] {
					new double[] {uv[0] + uvSizeVec.z + uvSizeVec.x, uv[1] + uvSizeVec.z},
					new double[] {uvSizeVec.z, uvSizeVec.y}
			};
			case EAST -> new double[][] {
					new double[] { uv[0], uv[1] + uvSizeVec.z },
					new double[] { uvSizeVec.z, uvSizeVec.y }
			};
			case NORTH -> new double[][] {
					new double[] {uv[0] + uvSizeVec.z, uv[1] + uvSizeVec.z},
					new double[] {uvSizeVec.x, uvSizeVec.y}
			};
			case SOUTH -> new double[][] {
					new double[] {uv[0] + uvSizeVec.z + uvSizeVec.x + uvSizeVec.z, uv[1] + uvSizeVec.z},
					new double[] {uvSizeVec.x, uvSizeVec.y }
			};
			case UP -> new double[][] {
					new double[] {uv[0] + uvSizeVec.z, uv[1]},
					new double[] {uvSizeVec.x, uvSizeVec.z}
			};
			case DOWN -> new double[][] {
					new double[] {uv[0] + uvSizeVec.z + uvSizeVec.x, uv[1] + uvSizeVec.z},
					new double[] {uvSizeVec.x, -uvSizeVec.z}
			};
		};

		return GeoQuad.build(vertices, uvData[0], uvData[1], textureWidth, textureHeight, mirror, direction);
	}

	static BakedModelFactory getForNamespace(String namespace) {
		return FACTORIES.getOrDefault(namespace, DEFAULT_FACTORY);
	}

	/**
	 * Register a custom {@link BakedModelFactory} to handle loading models in a custom way.<br>
	 * <b><u>MUST be called during mod construct</u></b><br>
	 * It is recommended you don't call this directly, and instead call it via {@link GeckoLibUtil#addCustomBakedModelFactory}
	 * @param namespace The namespace (modid) to register the factory for
	 * @param factory The factory responsible for model loading under the given namespace
	 */
	static void register(String namespace, BakedModelFactory factory) {
		FACTORIES.put(namespace, factory);
	}

	final class Builtin implements BakedModelFactory {
		@Override
		public BakedGeoModel constructGeoModel(GeometryTree geometryTree) {
			List<GeoBone> bones = new ObjectArrayList<>();

			for (BoneStructure boneStructure : geometryTree.topLevelBones().values()) {
				bones.add(constructBone(boneStructure, geometryTree.properties(), null));
			}

			return new BakedGeoModel(bones, geometryTree.properties());
		}

		@Override
		public GeoBone constructBone(BoneStructure boneStructure, ModelProperties properties, GeoBone parent) {
			Bone bone = boneStructure.self();
			GeoBone newBone = new GeoBone(parent, bone.name(), bone.mirror(), bone.inflate(), bone.neverRender(), bone.reset());
			Vec3 rotation = RenderUtils.arrayToVec(bone.rotation());
			Vec3 pivot = RenderUtils.arrayToVec(bone.pivot());

			newBone.updateRotation((float)Math.toRadians(-rotation.x), (float)Math.toRadians(-rotation.y), (float)Math.toRadians(rotation.z));
			newBone.updatePivot((float)-pivot.x, (float)pivot.y, (float)pivot.z);

			for (Cube cube : bone.cubes()) {
				newBone.getCubes().add(constructCube(cube, properties, newBone));
			}

			for (BoneStructure child : boneStructure.children().values()) {
				newBone.getChildBones().add(constructBone(child, properties, newBone));
			}

			return newBone;
		}

		@Override
		public GeoCube constructCube(Cube cube, ModelProperties properties, GeoBone bone) {
			boolean mirror = bone.getMirror() == Boolean.TRUE;
			double inflate = cube.inflate() != null ? cube.inflate() / 16f : (bone.getInflate() == null ? 0 : bone.getInflate() / 16f);
			Vec3 size = RenderUtils.arrayToVec(cube.size());
			Vec3 origin = RenderUtils.arrayToVec(cube.origin());
			Vec3 rotation = RenderUtils.arrayToVec(cube.rotation());
			Vec3 pivot = RenderUtils.arrayToVec(cube.pivot());
			origin = new Vec3(-(origin.x + size.x) / 16d, origin.y / 16d, origin.z / 16d);
			size = size.multiply(1 / 16d, 1 / 16d, 1 / 16d);

			rotation = rotation.multiply(-1, -1, 1);
			pivot = pivot.multiply(-1, 1, 1);
			rotation = new Vec3(Math.toRadians(rotation.x), Math.toRadians(rotation.y), Math.toRadians(rotation.z));

			GeoVertex bottomLeftBack = new GeoVertex(origin.x - inflate, origin.y - inflate, origin.z - inflate);
			GeoVertex bottomRightBack = new GeoVertex(origin.x - inflate, origin.y - inflate, origin.z + size.z + inflate);
			GeoVertex topLeftBack = new GeoVertex(origin.x - inflate, origin.y + size.y + inflate, origin.z - inflate);
			GeoVertex topRightBack = new GeoVertex(origin.x - inflate, origin.y + size.y + inflate, origin.z + size.z + inflate);
			GeoVertex topLeftFront = new GeoVertex(origin.x + size.x + inflate, origin.y + size.y + inflate, origin.z - inflate);
			GeoVertex topRightFront = new GeoVertex(origin.x + size.x + inflate, origin.y + size.y + inflate, origin.z + size.z + inflate);
			GeoVertex bottomLeftFront = new GeoVertex(origin.x + size.x + inflate, origin.y - inflate, origin.z - inflate);
			GeoVertex bottomRightFront = new GeoVertex(origin.x + size.x + inflate, origin.y - inflate, origin.z + size.z + inflate);

			GeoQuad[] quads = buildQuads(cube.uv(),
					(mirror ?
							new GeoVertex[] {bottomRightFront, bottomLeftFront, topRightFront, topLeftFront, topRightBack, topLeftBack, bottomRightBack, bottomLeftBack} :
							new GeoVertex[] {bottomLeftBack, bottomRightBack, topLeftBack, topRightBack, topLeftFront, topRightFront, bottomLeftFront, bottomRightFront}),
					cube, (float)properties.textureWidth(), (float)properties.textureHeight(), (mirror || cube.mirror() == Boolean.TRUE));

			return new GeoCube(quads, pivot, rotation, size, inflate, mirror);
		}
	}
}
