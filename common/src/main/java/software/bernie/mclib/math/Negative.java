package software.bernie.mclib.math;

/**
 * Negative operator class
 *
 * This class is responsible for inverting given value
 */
public class Negative implements MathValue {
	public MathValue value;

	public Negative(MathValue value) {
		this.value = value;
	}

	@Override
	public double get() {
		return -this.value.get();
	}

	@Override
	public String toString() {
		return "-" + this.value.toString();
	}
}
