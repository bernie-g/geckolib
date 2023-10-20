package software.bernie.mclib.math.functions.rounding;

import software.bernie.mclib.math.IValue;
import software.bernie.mclib.math.functions.Function;

public class Trunc extends Function
{
    public Trunc(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double get()
    {
        double value = this.getArg(0);

        return value < 0 ? Math.ceil(value) : Math.floor(value);
    }
}
