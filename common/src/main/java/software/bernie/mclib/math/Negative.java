package software.bernie.mclib.math;

/**
 * Negative operator class
 *
 * This class is responsible for inverting given value
 */
public class Negative implements software.bernie.mclib.math.IValue
{
	public software.bernie.mclib.math.IValue value;

	public Negative(IValue value)
	{
		this.value = value;
	}

	@Override
	public double get()
	{
		return -this.value.get();
	}

	@Override
	public String toString()
	{
		return "-" + this.value.toString();
	}
}