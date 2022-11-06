package software.bernie.geckolib3.cache.object;

import software.bernie.geckolib3.loading.json.raw.ModelProperties;

import java.util.List;
import java.util.Optional;

/**
 * Baked model object for Geckolib models.
 */
public record BakedGeoModel(List<GeoBone> topLevelBones, ModelProperties properties) implements software.bernie.geckolib3.core.animatable.model.BakedGeoModel {
	/**
	 * Gets the list of top-level bones for this model.
	 * Identical to calling {@link BakedGeoModel#topLevelBones()}
	 */
	@Override
	public List<? extends software.bernie.geckolib3.core.animatable.model.GeoBone> getBones() {
		return this.topLevelBones;
	}

	/**
	 * Gets a bone from this model by name.<br>
	 * Generally not a very efficient method, should be avoided where possible.
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link GeoBone} if one matches, otherwise an empty Optional
	 */
	public Optional<GeoBone> getBone(String name) {
		for (GeoBone bone : topLevelBones) {
			software.bernie.geckolib3.core.animatable.model.GeoBone childBone = searchForChildBone(bone, name);

			if (childBone != null)
				return Optional.of((GeoBone)childBone);
		}

		return Optional.empty();
	}
}
