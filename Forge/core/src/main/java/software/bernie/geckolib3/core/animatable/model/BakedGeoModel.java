package software.bernie.geckolib3.core.animatable.model;

import java.util.List;
import java.util.Optional;

/**
 * Baked model object for Geckolib models.<br>
 * Mostly an internal placeholder to allow for splitting up core (non-Minecraft) libraries
 */
public interface BakedGeoModel {
	List<? extends GeoBone> getBones();
	/**
	 * Gets a bone from this model by name.<br>
	 * Generally not a very efficient method, should be avoided where possible.
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link GeoBone} if one matches, otherwise an empty Optional
	 */
	Optional<? extends GeoBone> getBone(String name);

	/**
	 * Search a given {@link GeoBone}'s child bones and see if any of them match the given name, then return it.<br>
	 * @param parent The parent bone to search the children of
	 * @param name The name of the child bone to find
	 * @return The {@code GeoBone} found in the parent's children list, or null if not found
	 */
	default GeoBone searchForChildBone(GeoBone parent, String name) {
		if (parent.getName().equals(name))
			return parent;

		for (GeoBone bone : parent.getChildBones()) {
			if (bone.getName().equals(name))
				return bone;

			GeoBone subChildBone = searchForChildBone(parent, name);

			if (subChildBone != null)
				return subChildBone;
		}

		return null;
	}
}
