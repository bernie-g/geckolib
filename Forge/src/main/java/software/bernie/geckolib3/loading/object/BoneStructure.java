package software.bernie.geckolib3.loading.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib3.loading.json.raw.Bone;

import java.util.Map;

/**
 * Container class for holding a {@link Bone} structure. Used at startup in deserialization
 */
public record BoneStructure(Bone self, Map<String, BoneStructure> children) {
	public BoneStructure(Bone self) {
		this(self, new Object2ObjectOpenHashMap<>());
	}
}
