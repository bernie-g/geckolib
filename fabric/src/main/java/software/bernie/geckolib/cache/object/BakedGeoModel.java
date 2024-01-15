package software.bernie.geckolib.cache.object;

import software.bernie.geckolib.core.animatable.model.CoreBakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.loading.json.raw.ModelProperties;

import java.util.List;
import java.util.Optional;

/**
 * Baked model object for Geckolib models.
 */
public record BakedGeoModel(List<GeoBone> topLevelBones, ModelProperties properties) implements CoreBakedGeoModel {
	/**
	 * Gets the list of top-level bones for this model.
	 * Identical to calling {@link BakedGeoModel#topLevelBones()}
	 */
	@Override
	public List<? extends CoreGeoBone> getBones() {
		return this.topLevelBones;
	}

	/**
	 * Gets a bone from this model by name.<br>
	 * Generally not a very efficient method, should be avoided where possible.
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link GeoBone} if one matches, otherwise an empty Optional
	 */
	public Optional<GeoBone> getBone(String name) {
		for (GeoBone bone : this.topLevelBones) {
			CoreGeoBone childBone = searchForChildBone(bone, name);

			if (childBone != null)
				return Optional.of((GeoBone)childBone);
		}

		return Optional.empty();
	}
}
