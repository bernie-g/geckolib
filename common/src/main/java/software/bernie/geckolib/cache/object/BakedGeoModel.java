package software.bernie.geckolib.cache.object;

import software.bernie.geckolib.loading.json.raw.ModelProperties;

import java.util.List;
import java.util.Optional;

/**
 * Baked model object for Geckolib models
 */
public record BakedGeoModel(List<GeoBone> topLevelBones, ModelProperties properties) {
	/**
	 * Gets a bone from this model by name
	 * <p>
	 * Generally not a very efficient method, should be avoided where possible
	 *
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link GeoBone} if one matches, otherwise an empty Optional
	 */
	public Optional<GeoBone> getBone(String name) {
		for (GeoBone bone : this.topLevelBones) {
			GeoBone childBone = searchForChildBone(bone, name);

			if (childBone != null)
				return Optional.of(childBone);
		}

		return Optional.empty();
	}

	/**
	 * Search a given {@link GeoBone}'s child bones and see if any of them match the given name, then return it
	 *
	 * @param parent The parent bone to search the children of
	 * @param name The name of the child bone to find
	 * @return The {@code GeoBone} found in the parent's children list, or null if not found
	 */
	public GeoBone searchForChildBone(GeoBone parent, String name) {
		if (parent.getName().equals(name))
			return parent;

		for (GeoBone bone : parent.getChildBones()) {
			if (bone.getName().equals(name))
				return bone;

			GeoBone subChildBone = searchForChildBone(bone, name);

			if (subChildBone != null)
				return subChildBone;
		}

		return null;
	}
}
