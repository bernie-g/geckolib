package anightdazingzoroark.riftlib.geo.raw.tree;

import anightdazingzoroark.riftlib.geo.raw.pojo.Bone;

import java.util.HashMap;

public class RawBoneGroup
{
	public HashMap<String, RawBoneGroup> children = new HashMap<>();
	public Bone selfBone;

	public RawBoneGroup(Bone bone)
	{
		this.selfBone = bone;
	}
}
