package software.bernie.mclib.math;

/**
 * Ternary operator class
 *
 * This value implementation allows to return different values depending on
 * given condition value
 */
public class Ternary implements MathValue {
	public MathValue condition;
	public MathValue ifTrue;
	public MathValue ifFalse;

	public Ternary(MathValue condition, MathValue ifTrue, MathValue ifFalse) {
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	@Override
	public double get() {
		return this.condition.get() != 0 ? this.ifTrue.get() : this.ifFalse.get();
	}

	@Override
	public String toString() {
		return this.condition.toString() + " ? " + this.ifTrue.toString() + " : " + this.ifFalse.toString();
	}
}
