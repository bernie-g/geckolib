/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.keyframe;

import software.bernie.geckolib.animation.processor.IBone;

import java.util.LinkedList;

/**
 * An animation point queue holds a queue of Animation Points which are used in the AnimatedEntityModel to lerp between values
 */
public class AnimationPointQueue extends LinkedList<AnimationPoint>
{
	public IBone model;
}
