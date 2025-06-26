package anightdazingzoroark.riftlib.molang.math.functions.rounding;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class Trunc extends Function {
    public Trunc(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 1;
    }

    public double get() {
        double value = this.getArg(0);
        return value < (double)0.0F ? Math.ceil(value) : Math.floor(value);
    }
}
