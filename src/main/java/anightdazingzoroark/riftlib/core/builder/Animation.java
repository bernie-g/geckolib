/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package anightdazingzoroark.riftlib.core.builder;

import java.util.ArrayList;
import java.util.List;

import anightdazingzoroark.riftlib.core.builder.ILoopType.EDefaultLoopTypes;
import anightdazingzoroark.riftlib.core.keyframe.BoneAnimation;
import anightdazingzoroark.riftlib.core.keyframe.EventKeyFrame;
import anightdazingzoroark.riftlib.core.keyframe.ParticleEventKeyFrame;

/**
 * A specific animation instance
 */
public class Animation {
	public String animationName;
	public Double animationLength;
	public ILoopType loop = EDefaultLoopTypes.LOOP;
	public List<BoneAnimation> boneAnimations;
	public List<EventKeyFrame<String>> soundKeyFrames = new ArrayList<>();
	public List<ParticleEventKeyFrame> particleKeyFrames = new ArrayList<>();
	public List<EventKeyFrame<String>> customInstructionKeyframes = new ArrayList<>();

}
