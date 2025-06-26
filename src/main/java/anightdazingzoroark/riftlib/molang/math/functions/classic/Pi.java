package anightdazingzoroark.riftlib.molang.math.functions.classic;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

public class Pi extends Function {
    public Pi(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public double get() {
        return Math.PI;
    }
}
