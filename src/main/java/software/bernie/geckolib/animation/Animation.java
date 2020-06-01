package software.bernie.geckolib.animation;

import software.bernie.geckolib.animation.keyframe.*;
import java.util.List;

public class Animation
{
	public String animationName;
	public double animationLength;
	public boolean loop = true;
	public List<BoneAnimation> boneAnimations;
}
