package software.bernie.mclib.math.functions.utility;

import software.bernie.mclib.math.IValue;
import software.bernie.mclib.math.functions.Function;
import software.bernie.mclib.utils.Interpolations;

public class LerpRotate extends Function
{
	public LerpRotate(IValue[] values, String name) throws Exception
	{
		super(values, name);
	}

	@Override
	public int getRequiredArguments()
	{
		return 3;
	}

	@Override
	public double get()
	{
		return Interpolations.lerpYaw(this.getArg(0), this.getArg(1), this.getArg(2));
	}
}
