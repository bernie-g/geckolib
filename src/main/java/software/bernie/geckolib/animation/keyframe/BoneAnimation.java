package software.bernie.geckolib.animation.keyframe;

import java.util.List;

public class BoneAnimation
{
	public String boneName;
	public VectorKeyFrameList<RotationKeyFrame> rotationKeyFrames;
	public VectorKeyFrameList<PositionKeyFrame> positionKeyFrames;
	public VectorKeyFrameList<ScaleKeyFrame> scaleKeyFrames;
}
