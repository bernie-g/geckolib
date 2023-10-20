package com.eliotlash.mclib.math.functions.classic;

import com.eliotlash.mclib.math.IValue;
import com.eliotlash.mclib.math.functions.Function;

public class Ln extends Function
{
	public Ln(IValue[] values, String name) throws Exception
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
		return Math.log(this.getArg(0));
	}
}
