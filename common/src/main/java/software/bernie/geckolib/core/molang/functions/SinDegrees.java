package software.bernie.geckolib.core.molang.functions;

import software.bernie.mclib.math.IValue;
import software.bernie.mclib.math.functions.Function;
import software.bernie.mclib.math.functions.classic.Sin;

/**
 * Replacement function for {@link Sin}, operating in degrees rather than radians
 */
public class SinDegrees extends Function {
	public SinDegrees(IValue[] values, String name) throws Exception {
		super(values, name);
	}

	@Override
	public int getRequiredArguments() {
		return 1;
	}

	@Override
	public double get() {
		return Math.sin(getArg(0) / 180 * Math.PI);
	}
}
