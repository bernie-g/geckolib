package software.bernie.mclib.math.functions.classic;

import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.functions.Function;

public class Sin extends Function {
    public Sin(MathValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.sin(this.getArg(0));
    }
}
