package anightdazingzoroark.riftlib.molang.math.functions.classic;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class ATan extends Function {
    public ATan(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 1;
    }

    public double get() {
        return Math.atan(this.getArg(0));
    }
}
