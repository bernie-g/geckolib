package com.eliotlash.mclib.math;

/**
 * Group class
 *
 * Simply wraps given {@link IValue} into parenthesis in the
 * {@link #toString()} method.
 */
public class Group implements IValue
{
    private IValue value;

    public Group(IValue value)
    {
        this.value = value;
    }

    @Override
    public double get()
    {
        return this.value.get();
    }

    @Override
    public String toString()
    {
        return "(" + this.value.toString() + ")";
    }
}
