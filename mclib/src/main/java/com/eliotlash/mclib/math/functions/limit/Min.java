package com.eliotlash.mclib.math.functions.limit;

import com.eliotlash.mclib.math.IValue;
import com.eliotlash.mclib.math.functions.Function;

public class Min extends Function
{
	public Min(IValue[] values, String name) throws Exception
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
		return Math.min(this.getArg(0), this.getArg(1));
	}
}
