package anightdazingzoroark.riftlib.molang.math.functions.limit;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class Min extends Function {
    public Min(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 2;
    }

    public double get() {
        return Math.min(this.getArg(0), this.getArg(1));
    }
}
