package anightdazingzoroark.riftlib.molang.math.functions.utility;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

import java.util.Random;

public class HermiteBlend extends Function {
    public Random random = new Random();

    public HermiteBlend(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 1;
    }

    public double get() {
        double min = Math.ceil(this.getArg(0));
        return Math.floor((double)3.0F * Math.pow(min, (double)2.0F) - (double)2.0F * Math.pow(min, (double)3.0F));
    }
}
