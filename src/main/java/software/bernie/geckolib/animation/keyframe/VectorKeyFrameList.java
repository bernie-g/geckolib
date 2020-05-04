package software.bernie.geckolib.animation.keyframe;

import java.util.ArrayList;
import java.util.List;

public class VectorKeyFrameList<T extends KeyFrame>
{
	private final List<T> xKeyFrames;
	private final List<T> yKeyFrames;
	private final List<T> zKeyFrames;

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

	public List<T> getXKeyFrames()
	{
		return xKeyFrames;
	}

	public List<T> getYKeyFrames()
	{
		return yKeyFrames;
	}

	public List<T> getZKeyFrames()
	{
		return zKeyFrames;
	}
}
