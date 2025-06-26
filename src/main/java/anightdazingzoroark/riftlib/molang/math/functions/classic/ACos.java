package anightdazingzoroark.riftlib.molang.math.functions.classic;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class ACos extends Function {
    public ACos(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 1;
    }

    public double get() {
        return Math.acos(this.getArg(0));
    }
}
