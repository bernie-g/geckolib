package software.bernie.geckolib3q.geo.raw.tree;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib3q.geo.raw.pojo.Bone;

public class RawBoneGroup {
	public Map<String, RawBoneGroup> children = new Object2ObjectOpenHashMap<>();
	public Bone selfBone;

	public RawBoneGroup(Bone bone) {
		this.selfBone = bone;
	}
}
