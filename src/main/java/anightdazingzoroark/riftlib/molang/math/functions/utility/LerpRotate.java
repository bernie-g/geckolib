package anightdazingzoroark.riftlib.molang.math.functions.utility;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;
import anightdazingzoroark.riftlib.molang.utils.Interpolations;

public class LerpRotate extends Function {
    public LerpRotate(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 3;
    }

    public double get() {
        return Interpolations.lerpYaw(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}
