/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.core.builder;

import software.bernie.geckolib.core.keyframe.BoneAnimation;
import software.bernie.geckolib.core.keyframe.EventKeyFrame;
import software.bernie.geckolib.core.keyframe.ParticleEventKeyFrame;

import java.util.ArrayList;
import java.util.List;

/**
 * A specific animation instance
 */
public class Animation {
    public String animationName;
    public double animationLength;
    public boolean loop = true;
    public List<BoneAnimation> boneAnimations;
    public List<EventKeyFrame<String>> soundKeyFrames = new ArrayList<>();
    public List<ParticleEventKeyFrame> particleKeyFrames = new ArrayList<>();
    public List<EventKeyFrame<List<String>>> customInstructionKeyframes = new ArrayList<>();

}
