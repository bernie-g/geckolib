package software.bernie.geckolib3.geo.render.built;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;

import java.util.List;
import java.util.Optional;

/**
 * Baked model object for Geckolib models.
 */
public record BakedGeoModel(List<GeoBone> topLevelBones, ModelProperties properties) implements software.bernie.geckolib3.core.animatable.model.BakedGeoModel {
	public BakedGeoModel() {
		this(new ObjectArrayList<>(), new ModelProperties());
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
