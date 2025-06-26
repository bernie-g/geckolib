package anightdazingzoroark.riftlib.molang.math.functions.classic;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class ATan2 extends Function {
    public ATan2(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 2;
    }

    public double get() {
        return Math.atan2(this.getArg(0), this.getArg(1));
    }
}
