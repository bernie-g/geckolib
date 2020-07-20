/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.keyframe;

import software.bernie.geckolib.animation.render.AnimatedModelRenderer;

import java.util.LinkedList;

/**
 * An animation point queue holds a queue of Animation Points which are used in the AnimatedEntityModel to lerp between values
 */
public class AnimationPointQueue extends LinkedList<AnimationPoint>
{
	public AnimatedModelRenderer model;
}
