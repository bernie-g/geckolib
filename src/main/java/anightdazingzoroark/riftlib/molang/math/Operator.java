package anightdazingzoroark.riftlib.molang.math;

public class Operator implements IValue {
    public Operation operation;
    public IValue a;
    public IValue b;

    public Operator(Operation op, IValue a, IValue b) {
        this.operation = op;
        this.a = a;
        this.b = b;
    }

    public double get() {
        return this.operation.calculate(this.a.get(), this.b.get());
    }

    public String toString() {
        return this.a.toString() + " " + this.operation.sign + " " + this.b.toString();
    }
}
