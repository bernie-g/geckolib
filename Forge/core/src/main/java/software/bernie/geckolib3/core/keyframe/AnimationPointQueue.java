/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import java.util.LinkedList;

import software.bernie.geckolib3.core.processor.IBone;

/**
 * An animation point queue holds a queue of Animation Points which are used in
 * the AnimatedEntityModel to lerp between values
 */
@SuppressWarnings("serial")
public class AnimationPointQueue extends LinkedList<AnimationPoint> {
	public IBone model;
}
