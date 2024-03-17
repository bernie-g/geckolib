package software.bernie.mclib.math.functions.classic;

import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.functions.Function;

public class Ln extends Function {
	public Ln(MathValue[] values, String name) throws Exception {
		super(values, name);
	}

	@Override
	public int getRequiredArguments() {
		return 1;
	}

	@Override
	public double get() {
		return Math.log(this.getArg(0));
	}
}
