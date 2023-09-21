package software.bernie.geckolib.core.animatable.model;

import java.util.List;
import java.util.Optional;

/**
 * Baked model object for Geckolib models.<br>
 * Mostly an internal placeholder to allow for splitting up core (non-Minecraft) libraries
 */
public interface CoreBakedGeoModel {
	List<? extends CoreGeoBone> getBones();
	/**
	 * Gets a bone from this model by name.<br>
	 * Generally not a very efficient method, should be avoided where possible.
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link CoreGeoBone} if one matches, otherwise an empty Optional
	 */
	Optional<? extends CoreGeoBone> getBone(String name);

	/**
	 * Search a given {@link CoreGeoBone}'s child bones and see if any of them match the given name, then return it.<br>
	 * @param parent The parent bone to search the children of
	 * @param name The name of the child bone to find
	 * @return The {@code GeoBone} found in the parent's children list, or null if not found
	 */
	default CoreGeoBone searchForChildBone(CoreGeoBone parent, String name) {
		if (parent.getName().equals(name))
			return parent;

		for (CoreGeoBone bone : parent.getChildBones()) {
			if (bone.getName().equals(name))
				return bone;

			CoreGeoBone subChildBone = searchForChildBone(bone, name);

			if (subChildBone != null)
				return subChildBone;
		}

		return null;
	}
}
