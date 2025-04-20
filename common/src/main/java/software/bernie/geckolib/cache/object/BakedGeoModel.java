package software.bernie.geckolib.cache.object;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib.loading.json.raw.ModelProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Baked model object for Geckolib models
 */
public record BakedGeoModel(List<GeoBone> topLevelBones, ModelProperties properties, Supplier<Map<String, GeoBone>> boneMap) {
	public BakedGeoModel(List<GeoBone> topLevelBones, ModelProperties properties) {
		this(topLevelBones, properties, createBoneMap(topLevelBones));
	}

	/**
	 * Gets a bone from this model by name
	 *
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link GeoBone} if one matches, otherwise an empty Optional
	 */
	public Optional<GeoBone> getBone(String name) {
		return Optional.ofNullable(this.boneMap.get().get(name));
	}

	/**
	 * Create the bone map for this model, memoizing it as most models won't need it at all
	 */
	private static Supplier<Map<String, GeoBone>> createBoneMap(List<GeoBone> topLevelBones) {
		return Suppliers.memoize(() -> {
			Object2ReferenceMap<String, GeoBone> boneMap = new Object2ReferenceOpenHashMap<>();

			for (GeoBone bone : topLevelBones) {
				boneMap.put(bone.getName(), bone);

				for (GeoBone child : collectChildBones(bone)) {
					boneMap.put(child.getName(), child);
				}
			}

			return boneMap;
		});
	}

	/**
	 * Recursively collect all child bones of a bone
	 */
	private static List<GeoBone> collectChildBones(GeoBone bone) {
		List<GeoBone> bones = new ObjectArrayList<>();

		for (GeoBone child : bone.getChildBones()) {
			bones.add(child);
			bones.addAll(collectChildBones(child));
		}

		return bones;
	}
}
