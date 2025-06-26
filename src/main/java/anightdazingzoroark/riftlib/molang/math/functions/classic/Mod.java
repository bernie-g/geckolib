package anightdazingzoroark.riftlib.molang.math.functions.classic;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class Mod extends Function {
    public Mod(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 2;
    }

    public double get() {
        return this.getArg(0) % this.getArg(1);
    }
}
