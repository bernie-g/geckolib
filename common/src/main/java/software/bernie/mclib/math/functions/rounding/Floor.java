package software.bernie.mclib.math.functions.rounding;

import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.functions.Function;

public class Floor extends Function {
    public Floor(MathValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.floor(this.getArg(0));
    }
}
