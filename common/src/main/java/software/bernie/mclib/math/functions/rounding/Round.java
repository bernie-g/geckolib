package software.bernie.mclib.math.functions.rounding;

import software.bernie.mclib.math.IValue;
import software.bernie.mclib.math.functions.Function;

public class Round extends Function
{
    public Round(IValue[] values, String name) throws Exception
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
        return Math.round(this.getArg(0));
    }
}
