package software.bernie.geckolib.animation.keyframe;

import java.util.ArrayList;
import java.util.List;

public class VectorKeyFrameList<T extends KeyFrame>
{
	public List<T> xKeyFrames;
	public List<T> yKeyFrames;
	public List<T> zKeyFrames;

	public VectorKeyFrameList(List<T> XKeyFrames, List<T> YKeyFrames, List<T> ZKeyFrames)
	{
		xKeyFrames = XKeyFrames;
		yKeyFrames = YKeyFrames;
		zKeyFrames = ZKeyFrames;
	}

	public VectorKeyFrameList()
	{
		xKeyFrames = new ArrayList<T>();
		yKeyFrames = new ArrayList<T>();
		zKeyFrames = new ArrayList<T>();
	}

}
