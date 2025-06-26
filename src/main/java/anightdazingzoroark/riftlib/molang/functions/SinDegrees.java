package anightdazingzoroark.riftlib.molang.functions;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class SinDegrees extends Function {
    public SinDegrees(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 1;
    }

    public double get() {
        return Math.sin(this.getArg(0) / (double)180.0F * Math.PI);
    }
}
