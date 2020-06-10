/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation;

import software.bernie.geckolib.animation.keyframe.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A specific animation instance
 */
public class Animation
{
	public String animationName;
	public double animationLength;
	public boolean loop = true;
	public List<BoneAnimation> boneAnimations;
	public List<EventKeyFrame> soundKeyFrames = new ArrayList<>();
}
