package software.bernie.mclib.math.functions.classic;

import software.bernie.mclib.math.IValue;
import software.bernie.mclib.math.functions.Function;

public class Pow extends Function
{
    public Pow(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 2;
    }

    @Override
    public double get()
    {
        return Math.pow(this.getArg(0), this.getArg(1));
    }
}
