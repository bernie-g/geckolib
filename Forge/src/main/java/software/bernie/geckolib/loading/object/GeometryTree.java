package software.bernie.geckolib.loading.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.loading.json.raw.MinecraftGeometry;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.json.raw.ModelProperties;

import java.util.List;
import java.util.Map;

/**
 * Container class for a {@link Bone} structure, used at startup during deserialization
 */
public record GeometryTree(Map<String, BoneStructure> topLevelBones, ModelProperties properties) {
	public static GeometryTree fromModel(Model model) {
		final Map<String, BoneStructure> topLevelBones = new Object2ObjectOpenHashMap<>();
		final MinecraftGeometry geometry = model.minecraftGeometry()[0];
		final Bone[] bones = geometry.bones();
		final Map<String, BoneStructure> lookup = new Object2ObjectOpenHashMap<>(bones.length);

		for (Bone bone : bones) {
			final BoneStructure boneStructure = new BoneStructure(bone);

			lookup.put(bone.name(), boneStructure);

			if (bone.parent() == null)
				topLevelBones.put(bone.name(), boneStructure);
		}

		for (Bone bone : bones) {
			final String parentName = bone.parent();

			if (parentName != null) {
				final String boneName = bone.name();

				if (parentName.equals(boneName))
					throw new IllegalArgumentException("Invalid model definition. Bone has defined itself as its own parent: " + boneName);

				final BoneStructure parentStructure = lookup.get(parentName);

				if (parentStructure == null)
					throw new IllegalArgumentException("Invalid model definition. Found bone with undefined parent (child -> parent): " + boneName + " -> " + parentName);

				parentStructure.children().put(boneName, lookup.get(boneName));
			}
		}

		return new GeometryTree(topLevelBones, geometry.modelProperties());
	}

	@Deprecated(forRemoval = true)
	private static BoneStructure findBoneStructureInTree(Map<String, BoneStructure> bones, String boneName) {
		for (BoneStructure entry : bones.values()) {
			if (boneName.equals(entry.self().name()))
				return entry;

			BoneStructure subStructure = findBoneStructureInTree(entry.children(), boneName);

			if (subStructure != null)
				return subStructure;
		}

		return null;
	}
}
