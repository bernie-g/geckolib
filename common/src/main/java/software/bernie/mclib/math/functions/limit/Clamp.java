package software.bernie.mclib.math.functions.limit;

import software.bernie.mclib.math.IValue;
import software.bernie.mclib.math.functions.Function;
import software.bernie.mclib.utils.MathUtils;

public class Clamp extends Function
{
    public Clamp(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 3;
    }

    @Override
    public double get()
    {
        return MathUtils.clamp(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}
