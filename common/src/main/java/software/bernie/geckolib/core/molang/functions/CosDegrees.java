package software.bernie.geckolib.core.molang.functions;

import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.functions.Function;
import software.bernie.mclib.math.functions.classic.Cos;

/**
 * Replacement function for {@link Cos}, operating in degrees rather than radians.
 */
public class CosDegrees extends Function {
	public CosDegrees(MathValue[] values, String name) throws Exception {
		super(values, name);
	}

	@Override
	public int getRequiredArguments() {
		return 1;
	}

	@Override
	public double get() {
		return Math.cos(this.getArg(0) / 180 * Math.PI);
	}
}
