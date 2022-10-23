package software.bernie.geckolib3.geo.render.built;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;

import java.util.List;
import java.util.Optional;

public class GeoModel {
	public List<GeoBone> topLevelBones = new ObjectArrayList<>();
	public ModelProperties properties;

	public Optional<GeoBone> getBone(String name) {
		for (GeoBone bone : topLevelBones) {
			GeoBone optionalBone = getBoneRecursively(name, bone);
			if (optionalBone != null) {
				return Optional.of(optionalBone);
			}
		}
		return Optional.empty();
	}

	private GeoBone getBoneRecursively(String name, GeoBone bone) {
		if (bone.name.equals(name)) {
			return bone;
		}
		for (GeoBone childBone : bone.childBones) {
			if (childBone.name.equals(name)) {
				return childBone;
			}
			GeoBone optionalBone = getBoneRecursively(name, childBone);
			if (optionalBone != null) {
				return optionalBone;
			}
		}
		return null;
	}
}
