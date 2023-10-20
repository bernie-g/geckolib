package com.eliotlash.mclib.math;

/**
 * Negate operator class
 *
 * This class is responsible for negating given value
 */
public class Negate implements IValue
{
	public IValue value;

	public Negate(IValue value)
	{
		this.value = value;
	}

	@Override
	public double get()
	{
		return this.value.get() == 0 ? 1 : 0;
	}

	@Override
	public String toString()
	{
		return "!" + this.value.toString();
	}
}
