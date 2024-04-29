package software.bernie.geckolib.loading.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.loading.json.raw.MinecraftGeometry;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.json.raw.ModelProperties;

import java.util.Map;

/**
 * Container class for a {@link Bone} structure, used at startup during deserialization
 */
public record GeometryTree(Map<String, BoneStructure> topLevelBones, ModelProperties properties) {

	public static GeometryTree fromModel(Model model){
		final Map<String, BoneStructure> topLevelBones = new Object2ObjectOpenHashMap<>();
		final MinecraftGeometry geometry = model.minecraftGeometry()[0];
		final Bone[] bones = geometry.bones();
		final Map<String, BoneStructure> lookup = new Object2ObjectOpenHashMap<>();
		for (Bone bone : bones) {
			final BoneStructure boneStructure = new BoneStructure(bone);
			lookup.put(bone.name(), boneStructure);
			if (bone.parent() == null)
				topLevelBones.put(bone.name(), boneStructure);
		}
		for (Bone bone : bones) {
			final String parentName = bone.parent();
			final String boneName = bone.name();
			final BoneStructure boneStructure = lookup.get(boneName);
			if (parentName != null) {
				lookup.get(parentName).children().put(boneName, boneStructure);
			}
		}
		return new GeometryTree(topLevelBones, geometry.modelProperties());
	}
}
