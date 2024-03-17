package software.bernie.mclib.math.functions.utility;

import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.functions.Function;
import software.bernie.mclib.math.utils.Interpolations;

public class Lerp extends Function {
	public Lerp(MathValue[] values, String name) throws Exception {
		super(values, name);
	}

	@Override
	public int getRequiredArguments() {
		return 3;
	}

	@Override
	public double get() {
		return Interpolations.lerp(this.getArg(0), this.getArg(1), this.getArg(2));
	}
}
