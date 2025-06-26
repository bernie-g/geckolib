package anightdazingzoroark.riftlib.molang.math.functions.limit;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class Max extends Function {
    public Max(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 2;
    }

    public double get() {
        return Math.max(this.getArg(0), this.getArg(1));
    }
}
