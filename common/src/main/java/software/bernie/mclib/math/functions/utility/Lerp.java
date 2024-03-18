package software.bernie.mclib.math.functions.utility;

import net.minecraft.util.Mth;
import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.functions.Function;

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
		return Mth.lerp(this.getArg(2), this.getArg(0), this.getArg(1));
	}
}
