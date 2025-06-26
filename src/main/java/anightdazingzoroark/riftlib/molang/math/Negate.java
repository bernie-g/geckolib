package anightdazingzoroark.riftlib.molang.math;

public class Negate implements anightdazingzoroark.riftlib.molang.math.IValue {
    public anightdazingzoroark.riftlib.molang.math.IValue value;

    public Negate(IValue value) {
        this.value = value;
    }

    public double get() {
        return this.value.get() == (double)0.0F ? (double)1.0F : (double)0.0F;
    }

    public String toString() {
        return "!" + this.value.toString();
    }
}
