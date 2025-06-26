package anightdazingzoroark.riftlib.molang.math.functions.limit;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;
import anightdazingzoroark.riftlib.molang.utils.MathUtils;

public class Clamp extends Function {
    public Clamp(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 3;
    }

    public double get() {
        return MathUtils.clamp(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}
