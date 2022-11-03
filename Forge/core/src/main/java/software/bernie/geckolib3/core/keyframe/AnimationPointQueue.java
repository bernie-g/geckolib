/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import software.bernie.geckolib3.core.model.GeoBone;

import java.util.LinkedList;

/**
 * An animation point queue holds a queue of Animation Points which are used in
 * the AnimatedEntityModel to lerp between values
 */
public class AnimationPointQueue extends LinkedList<AnimationPoint> {

	private static final long serialVersionUID = 5472797438476621193L;
	public GeoBone model;
}
