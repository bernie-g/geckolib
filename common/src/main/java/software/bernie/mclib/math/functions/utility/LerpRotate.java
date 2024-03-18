package software.bernie.mclib.math.functions.utility;

import software.bernie.geckolib.util.RenderUtils;
import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.functions.Function;

public class LerpRotate extends Function {
	public LerpRotate(MathValue[] values, String name) throws Exception {
		super(values, name);
	}

	@Override
	public int getRequiredArguments() {
		return 3;
	}

	@Override
	public double get() {
		return RenderUtils.lerpYaw(this.getArg(2), this.getArg(0), this.getArg(1));
	}
}
