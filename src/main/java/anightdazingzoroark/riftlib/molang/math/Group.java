package anightdazingzoroark.riftlib.molang.math;

public class Group implements IValue {
    private IValue value;

    public Group(IValue value) {
        this.value = value;
    }

    public double get() {
        return this.value.get();
    }

    public String toString() {
        return "(" + this.value.toString() + ")";
    }
}
