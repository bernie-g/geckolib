package software.bernie.geckolib.animation;

public class AnimationPoint
{
	public final Double currentTick;
	public final Double animationEndTick;
	public final Float animationStartValue;
	public final Float animationEndValue;
	public AnimationPoint(Double currentTick, Double animationEndTick, Double animationStartValue, Double animationEndValue)
	{
		this.currentTick = currentTick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = animationStartValue.floatValue();
		this.animationEndValue = animationEndValue.floatValue();
	}

	public AnimationPoint(Double currentTick, Double animationEndTick, Float animationStartValue, Float animationEndValue)
	{
		this.currentTick = currentTick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = animationStartValue;
		this.animationEndValue = animationEndValue;
	}

	public AnimationPoint(Double currentTick, Double animationEndTick, Float animationStartValue, Double animationEndValue)
	{
		this.currentTick = currentTick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = animationStartValue;
		this.animationEndValue = animationEndValue.floatValue();
	}

	@Override
	public String toString()
	{
		return "Tick: " + currentTick + " | End Tick: " + animationEndTick + " | Start Value: " + animationStartValue + " | End Value: " + animationEndValue;
	}
}
