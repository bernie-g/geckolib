package anightdazingzoroark.riftlib.molang.math.functions.utility;

import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.functions.Function;

import java.util.Random;

public class DieRollInteger extends Function {
    public Random random = new Random();

    public DieRollInteger(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 3;
    }

    public double get() {
        double i = (double)0.0F;

        double total;
        for(total = (double)0.0F; i < this.getArg(0); total += (double)Math.round(this.getArg(1) + Math.random() * (this.getArg(2) - this.getArg(1)))) {
        }

        return total;
    }
}
