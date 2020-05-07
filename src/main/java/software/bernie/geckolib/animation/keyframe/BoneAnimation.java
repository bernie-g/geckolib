package software.bernie.geckolib.animation.keyframe;

import software.bernie.geckolib.model.BoneSnapshot;

import java.util.List;

public class BoneAnimation
{
	public BoneSnapshot recentSnapshot = null;
	public String boneName;
	public VectorKeyFrameList<KeyFrame<Float>> rotationKeyFrames;
	public VectorKeyFrameList<KeyFrame<Float>> positionKeyFrames;
	public VectorKeyFrameList<KeyFrame<Float>> scaleKeyFrames;
}
