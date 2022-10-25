/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

public class BoneAnimation {
	public final String boneName;
	
	public VectorKeyFrameList<KeyFrame> rotationKeyFrames;
	public VectorKeyFrameList<KeyFrame> positionKeyFrames;
	public VectorKeyFrameList<KeyFrame> scaleKeyFrames;
	
	public BoneAnimation(String boneName) {
		this.boneName = boneName;
	}
}
