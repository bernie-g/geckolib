package anightdazingzoroark.riftlib.molang.functions;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class CosDegrees extends Function {
    public CosDegrees(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 1;
    }

    public double get() {
        return Math.cos(this.getArg(0) / (double)180.0F * Math.PI);
    }
}

