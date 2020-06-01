package software.bernie.geckolib.animation.keyframe;

import java.util.Objects;

public class KeyFrame<T>
{
	private Double length;
	private T startValue;
	private T endValue;
	public KeyFrame(Double length, T startValue, T endValue)
	{
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
	}

	public Double getLength()
	{
		return length;
	}

	public void setLength(Double length)
	{
		this.length = length;
	}

	public T getStartValue()
	{
		return startValue;
	}

	public void setStartValue(T startValue)
	{
		this.startValue = startValue;
	}

	public T getEndValue()
	{
		return endValue;
	}

	public void setEndValue(T endValue)
	{
		this.endValue = endValue;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(length, startValue, endValue);
	}
}
