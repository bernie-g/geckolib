package software.bernie.mclib.math.functions.classic;

import software.bernie.mclib.math.IValue;
import software.bernie.mclib.math.functions.Function;

public class Mod extends Function
{
    public Mod(IValue[] values, String name) throws Exception
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
        return this.getArg(0) % this.getArg(1);
    }
}
