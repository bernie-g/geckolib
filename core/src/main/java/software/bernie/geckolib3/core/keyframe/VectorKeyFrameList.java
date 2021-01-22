/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import java.util.ArrayList;
import java.util.List;

/**
 * A vector key frame list is a handy class used to store 3 lists of keyframes: the X, Y, and Z keyframes. The keyframes can be rotation, scale, or position.
 *
 * @param <T> the type parameter
 */
public class VectorKeyFrameList<T extends KeyFrame>
{
	/**
	 * The X key frames.
	 */
	public List<T> xKeyFrames;
	/**
	 * The Y key frames.
	 */
	public List<T> yKeyFrames;
	/**
	 * The Z key frames.
	 */
	public List<T> zKeyFrames;

	/**
	 * Instantiates a new vector key frame list from 3 lists of keyframes
	 *
	 * @param XKeyFrames the x key frames
	 * @param YKeyFrames the y key frames
	 * @param ZKeyFrames the z key frames
	 */
	public VectorKeyFrameList(List<T> XKeyFrames, List<T> YKeyFrames, List<T> ZKeyFrames)
	{
		xKeyFrames = XKeyFrames;
		yKeyFrames = YKeyFrames;
		zKeyFrames = ZKeyFrames;
	}

	/**
	 * Instantiates a new blank key frame list
	 */
	public VectorKeyFrameList()
	{
		xKeyFrames = new ArrayList<>();
		yKeyFrames = new ArrayList<>();
		zKeyFrames = new ArrayList<>();
	}

	public double getLastKeyframeTime()
	{
		double xTime = 0;
		for (T frame : xKeyFrames)
		{
			xTime += frame.getLength();
		}

		double yTime = 0;
		for (T frame : yKeyFrames)
		{
			yTime += frame.getLength();
		}

		double zTime = 0;
		for (T frame : zKeyFrames)
		{
			zTime += frame.getLength();
		}

		return Math.max(xTime, Math.max(yTime, zTime));
	}
}
