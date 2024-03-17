package software.bernie.mclib.math;

/**
 * Operator class
 *
 * This class is responsible for performing a calculation of two values
 * based on given operation.
 */
public class Operator implements MathValue {
    public Operation operation;
    public MathValue a;
    public MathValue b;

    public Operator(Operation op, MathValue a, MathValue b) {
        this.operation = op;
        this.a = a;
        this.b = b;
    }

    @Override
    public double get() {
        return this.operation.calculate(a.get(), b.get());
    }

    @Override
    public String toString() {
        return a.toString() + " " + this.operation.sign + " " + b.toString();
    }
}
