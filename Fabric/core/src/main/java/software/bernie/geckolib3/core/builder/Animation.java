/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.builder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.EventKeyFrame;
import software.bernie.geckolib3.core.keyframe.ParticleEventKeyFrame;

import java.util.List;

/**
 * A specific animation instance
 */
public class Animation {
	public String animationName;
	public double animationLength = -1;
	public ILoopType loop = EDefaultLoopTypes.LOOP;
	public List<BoneAnimation> boneAnimations;
	public List<EventKeyFrame<String>> soundKeyFrames = new ObjectArrayList<>();
	public List<ParticleEventKeyFrame> particleKeyFrames = new ObjectArrayList<>();
	public List<EventKeyFrame<String>> customInstructionKeyframes = new ObjectArrayList<>();

}
