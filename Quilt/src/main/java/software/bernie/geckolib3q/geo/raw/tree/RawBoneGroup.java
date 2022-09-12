package software.bernie.geckolib3q.geo.raw.tree;

import software.bernie.geckolib3q.geo.raw.pojo.Bone;

import java.util.HashMap;

public class RawBoneGroup {
	public HashMap<String, RawBoneGroup> children = new HashMap<>();
	public Bone selfBone;

	public RawBoneGroup(Bone bone) {
		this.selfBone = bone;
	}
}
