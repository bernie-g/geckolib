package com.eliotlash.mclib.math;

/**
 * Constant class
 *
 * This class simply returns supplied in the constructor value
 */
public class Constant implements IValue
{
    private double value;

    public Constant(double value)
    {
        this.value = value;
    }

    @Override
    public double get()
    {
        return this.value;
    }

    public void set(double value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.value);
    }
}
