package software.bernie.mclib.math.functions.limit;

import net.minecraft.util.Mth;
import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.functions.Function;

public class Clamp extends Function {
    public Clamp(MathValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        return Mth.clamp(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}
