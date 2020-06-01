package software.bernie.geckolib.animation.keyframe;

import software.bernie.geckolib.model.BoneSnapshot;

import java.util.List;

public class BoneAnimation
{
	public BoneSnapshot recentSnapshot = null;
	public String boneName;
	public VectorKeyFrameList<KeyFrame<Double>> rotationKeyFrames;
	public VectorKeyFrameList<KeyFrame<Double>> positionKeyFrames;
	public VectorKeyFrameList<KeyFrame<Double>> scaleKeyFrames;
}
