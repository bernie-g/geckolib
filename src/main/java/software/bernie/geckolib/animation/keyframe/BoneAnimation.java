package software.bernie.geckolib.animation.keyframe;

import java.util.List;

public class BoneAnimation
{
	public String boneName;
	public VectorKeyFrameList<KeyFrame<Float>> rotationKeyFrames;
	public VectorKeyFrameList<KeyFrame<Float>> positionKeyFrames;
	public VectorKeyFrameList<KeyFrame<Float>> scaleKeyFrames;
}
