package software.bernie.geckolib.animation;

import software.bernie.geckolib.model.BoneSnapshotCollection;

import java.util.HashMap;

public class AnimationControllerCollection extends HashMap<String, AnimationController>
{
	public BoneSnapshotCollection boneSnapshotCollection;

	public AnimationControllerCollection()
	{
		super();
		boneSnapshotCollection = new BoneSnapshotCollection();
	}

	public AnimationController addAnimationController(AnimationController value)
	{
		return this.put(value.name, value);
	}
}
