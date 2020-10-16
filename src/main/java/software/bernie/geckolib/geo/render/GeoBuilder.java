package software.bernie.geckolib.geo.render;

import net.minecraft.client.util.math.Vector3f;
import org.apache.commons.lang3.ArrayUtils;
import software.bernie.geckolib.geo.raw.pojo.Bone;
import software.bernie.geckolib.geo.raw.pojo.Cube;
import software.bernie.geckolib.geo.raw.pojo.ModelProperties;
import software.bernie.geckolib.geo.raw.tree.RawBoneGroup;
import software.bernie.geckolib.geo.raw.tree.RawGeometryTree;
import software.bernie.geckolib.geo.render.built.GeoBone;
import software.bernie.geckolib.geo.render.built.GeoCube;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.util.VectorUtils;

public class GeoBuilder
{
	public static GeoModel constructGeoModel(RawGeometryTree geometryTree)
	{
		GeoModel model = new GeoModel();
		model.properties = geometryTree.properties;
		for (RawBoneGroup rawBone : geometryTree.topLevelBones.values())
		{
			model.topLevelBones.add(constructBone(rawBone, geometryTree.properties, null));
		}
		return model;
	}

	private static GeoBone constructBone(RawBoneGroup bone, ModelProperties properties, GeoBone parent)
	{
		GeoBone geoBone = new GeoBone();

		Bone rawBone = bone.selfBone;
		Vector3f rotation = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(rawBone.getRotation()));
		Vector3f pivot = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(rawBone.getPivot()));
		rotation.add(-1, -1, 1);

		geoBone.mirror = rawBone.getMirror();
		geoBone.dontRender = rawBone.getNeverRender();
		geoBone.reset = rawBone.getReset();
		geoBone.inflate = rawBone.getInflate();
		geoBone.parent = parent;
		geoBone.setModelRendererName(rawBone.getName());


		geoBone.setRotationX((float) Math.toRadians(rotation.getX()));
		geoBone.setRotationY((float) Math.toRadians(rotation.getY()));
		geoBone.setRotationZ((float) Math.toRadians(rotation.getZ()));

		geoBone.rotationPointX = -pivot.getX();
		geoBone.rotationPointY = pivot.getY();
		geoBone.rotationPointZ = pivot.getZ();

		if (!ArrayUtils.isEmpty(rawBone.getCubes()))
		{
			for (Cube cube : rawBone.getCubes())
			{
				geoBone.childCubes.add(GeoCube.createFromPojoCube(cube, properties, geoBone.inflate == null ? null : geoBone.inflate / 16));
			}
		}

		for (RawBoneGroup child : bone.children.values())
		{
			geoBone.childBones.add(constructBone(child, properties, geoBone));
		}

		return geoBone;
	}
}
