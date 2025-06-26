package anightdazingzoroark.riftlib.molang.math.functions.utility;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

import java.util.Random;

public class RandomInteger extends Function {
    public java.util.Random random = new Random();

    public RandomInteger(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 2;
    }

    public double get() {
        double min = Math.ceil(this.getArg(0));
        double max = Math.floor(this.getArg(1));
        return Math.floor(Math.random() * (max - min) + min);
    }
}
