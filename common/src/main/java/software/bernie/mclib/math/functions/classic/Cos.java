package software.bernie.mclib.math.functions.classic;

import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.functions.Function;

public class Cos extends Function {
    public Cos(MathValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.cos(this.getArg(0));
    }
}
