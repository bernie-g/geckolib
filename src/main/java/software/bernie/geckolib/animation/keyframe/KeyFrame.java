package software.bernie.geckolib.animation.keyframe;

public class KeyFrame<T>
{
	private float KeyFrameLength;
	private T StartValue;
	private T EndValue;
	public boolean isIntermediaryKeyFrame = false;
	public KeyFrame(float keyFrameLength, T startValue, T endValue)
	{
		KeyFrameLength = keyFrameLength;
		StartValue = startValue;
		EndValue = endValue;
	}
	
	public float getKeyFrameLength()
	{
		return KeyFrameLength;
	}

	public void setKeyFrameLength(float keyFrameLength)
	{
		KeyFrameLength = keyFrameLength;
	}

	public T getStartValue()
	{
		return StartValue;
	}

	public void setStartValue(T startValue)
	{
		StartValue = startValue;
	}

	public T getEndValue()
	{
		return EndValue;
	}

	public void setEndValue(T endValue)
	{
		EndValue = endValue;
	}
}
