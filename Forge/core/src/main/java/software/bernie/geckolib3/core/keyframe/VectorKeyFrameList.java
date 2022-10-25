/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

/**
 * A vector key frame list is a handy class used to store 3 lists of keyframes:
 * the X, Y, and Z keyframes. The keyframes can be rotation, scale, or position.
 *
 * @param <T> the type parameter
 */
public record VectorKeyFrameList<T extends KeyFrame>(List<T> xKeyFrames, List<T> yKeyFrames, List<T> zKeyFrames) {
	/**
	 * Instantiates a new blank key frame list
	 */
	public VectorKeyFrameList() {
		this(new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>());
	}

	public double getLastKeyframeTime() {
		double xTime = 0;
		for (T frame : xKeyFrames) {
			xTime += frame.getLength();
		}

		double yTime = 0;
		for (T frame : yKeyFrames) {
			yTime += frame.getLength();
		}

		double zTime = 0;
		for (T frame : zKeyFrames) {
			zTime += frame.getLength();
		}

		return Math.max(xTime, Math.max(yTime, zTime));
	}
}
