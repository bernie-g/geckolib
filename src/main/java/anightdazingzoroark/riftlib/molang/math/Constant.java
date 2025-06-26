package anightdazingzoroark.riftlib.molang.math;

public class Constant implements IValue {
    private double value;

    public Constant(double value) {
        this.value = value;
    }

    public double get() {
        return this.value;
    }

    public void set(double value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}