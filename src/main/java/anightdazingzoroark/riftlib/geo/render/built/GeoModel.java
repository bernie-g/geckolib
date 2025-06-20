package anightdazingzoroark.riftlib.geo.render.built;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import anightdazingzoroark.riftlib.geo.raw.pojo.ModelProperties;

public class GeoModel {
	public List<GeoBone> topLevelBones = new ArrayList<>();
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

	public List<GeoBone> getAllBones() {
		List<GeoBone> listToReturn = new ArrayList<>();
		for (GeoBone bone : this.topLevelBones) {
			this.collectAll(bone, listToReturn);
		}
		return listToReturn;
	}

	private void collectAll(GeoBone current, List<GeoBone> result) {
		result.add(current);
		for (GeoBone child : current.childBones) {
			collectAll(child, result);
		}
	}

	public List<GeoLocator> getAllLocators() {
		List<GeoLocator> listToReturn = new ArrayList<>();
		for (GeoBone bone : this.getAllBones()) {
			for (GeoLocator locator : bone.childLocators) {
				if (!listToReturn.contains(locator)) listToReturn.add(locator);
			}
		}
		return listToReturn;
	}
}
