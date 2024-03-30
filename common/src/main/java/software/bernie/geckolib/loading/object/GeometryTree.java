package software.bernie.geckolib.loading.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.loading.json.raw.MinecraftGeometry;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.json.raw.ModelProperties;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Container class for a {@link Bone} structure, used at startup during deserialization
 */
public record GeometryTree(Map<String, BoneStructure> topLevelBones, ModelProperties properties) {
	public static GeometryTree fromModel(Model model) {
		final Map<String, BoneStructure> topLevelBones = new Object2ObjectOpenHashMap<>();
		final MinecraftGeometry geometry = model.minecraftGeometry()[0];
		final List<Bone> bones = new ObjectArrayList<>(geometry.bones());

		while (true) {
			if (!bones.removeIf(bone -> {
				if (bone.parent() == null) {
					topLevelBones.put(bone.name(), new BoneStructure(bone));

					return true;
				}

				BoneStructure parent = findBoneStructureInTree(topLevelBones, bone.parent());

				if (parent != null) {
					parent.children().put(bone.name(), new BoneStructure(bone));

					return true;
				}

				return false;
			})) {
				if (!bones.isEmpty()) {
					StringJoiner joiner = new StringJoiner(", ");

					for (Bone remainingBone : bones) {
						joiner.add(remainingBone.name() + " -> " + remainingBone.parent());
					}

					throw new IllegalArgumentException("Invalid model definition. Found bone(s) with undefined parent (child -> parent): " + joiner);
				}

				break;
			}
		}

		return new GeometryTree(topLevelBones, geometry.modelProperties());
	}

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
