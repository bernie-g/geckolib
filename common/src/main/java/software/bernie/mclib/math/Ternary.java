package software.bernie.mclib.math;

/**
 * Ternary operator class
 *
 * This value implementation allows to return different values depending on
 * given condition value
 */
public class Ternary implements software.bernie.mclib.math.IValue
{
	public software.bernie.mclib.math.IValue condition;
	public software.bernie.mclib.math.IValue ifTrue;
	public software.bernie.mclib.math.IValue ifFalse;

	public Ternary(software.bernie.mclib.math.IValue condition, software.bernie.mclib.math.IValue ifTrue, IValue ifFalse)
	{
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	@Override
	public double get()
	{
		return this.condition.get() != 0 ? this.ifTrue.get() : this.ifFalse.get();
	}

	@Override
	public String toString()
	{
		return this.condition.toString() + " ? " + this.ifTrue.toString() + " : " + this.ifFalse.toString();
	}
}
