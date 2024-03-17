package software.bernie.mclib.math;

/**
 * Group class
 *
 * Simply wraps given {@link MathValue} into parenthesis in the
 * {@link #toString()} method.
 */
public class Group implements MathValue {
    private MathValue value;

    public Group(MathValue value) {
        this.value = value;
    }

    @Override
    public double get() {
        return this.value.get();
    }

    @Override
    public String toString() {
        return "(" + this.value.toString() + ")";
    }
}
