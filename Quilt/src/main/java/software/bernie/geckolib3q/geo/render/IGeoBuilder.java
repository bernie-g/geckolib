package software.bernie.geckolib3q.geo.render;

import software.bernie.geckolib3q.geo.raw.pojo.ModelProperties;
import software.bernie.geckolib3q.geo.raw.tree.RawBoneGroup;
import software.bernie.geckolib3q.geo.raw.tree.RawGeometryTree;
import software.bernie.geckolib3q.geo.render.built.GeoBone;
import software.bernie.geckolib3q.geo.render.built.GeoModel;

public interface IGeoBuilder {
    GeoModel constructGeoModel(RawGeometryTree geometryTree);

    GeoBone constructBone(RawBoneGroup bone, ModelProperties properties, GeoBone parent);
}
