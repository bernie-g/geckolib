package anightdazingzoroark.riftlib.geo.render;

import anightdazingzoroark.riftlib.geo.raw.pojo.ModelProperties;
import anightdazingzoroark.riftlib.geo.raw.tree.RawBoneGroup;
import anightdazingzoroark.riftlib.geo.raw.tree.RawGeometryTree;
import anightdazingzoroark.riftlib.geo.render.built.GeoBone;
import anightdazingzoroark.riftlib.geo.render.built.GeoModel;

public interface IGeoBuilder {

	GeoModel constructGeoModel(RawGeometryTree geometryTree);

	GeoBone constructBone(RawBoneGroup bone, ModelProperties properties, GeoBone parent);

}
