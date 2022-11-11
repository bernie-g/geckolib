package software.bernie.geckolib3.loading.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib3.loading.json.raw.Bone;
import software.bernie.geckolib3.loading.json.raw.MinecraftGeometry;
import software.bernie.geckolib3.loading.json.raw.Model;
import software.bernie.geckolib3.loading.json.raw.ModelProperties;

import java.util.List;
import java.util.Map;

/**
 * Container class for a {@link Bone} structure, used at startup during deserialization
 */
public record GeometryTree(Map<String, BoneStructure> topLevelBones, ModelProperties properties) {
	public static GeometryTree fromModel(Model model) {
		Map<String, BoneStructure> topLevelBones = new Object2ObjectOpenHashMap<>();
		MinecraftGeometry geometry = model.minecraftGeometry()[0];
		List<Bone> bones = new ObjectArrayList<>(geometry.bones());
		int index = bones.size() - 1;

		while (true) {
			Bone bone = bones.get(index);

			if (bone.parent() == null) {
				topLevelBones.put(bone.name(), new BoneStructure(bone));
				bones.remove(index);
			}
			else {
				BoneStructure structure = findBoneStructureInTree(topLevelBones, bone.parent());

				if (structure != null) {
					structure.children().put(bone.name(), new BoneStructure(bone));
					bones.remove(index);
				}
			}

			if (index == 0) {
				index = bones.size() - 1;

				if (index == -1)
					break;
			}
			else {
				index--;
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
