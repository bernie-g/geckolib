package anightdazingzoroark.riftlib.molang.math.functions.rounding;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class Ceil extends Function {
    public Ceil(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 1;
    }

    public double get() {
        return Math.ceil(this.getArg(0));
    }
}
