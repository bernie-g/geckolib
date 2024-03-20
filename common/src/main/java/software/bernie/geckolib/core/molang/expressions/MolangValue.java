package software.bernie.geckolib.core.molang.expressions;

import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.mclib.math.MathValue;

/**
 * Molang extension for the {@link MathValue} system.
 * Used to handle values and expressions specific to Molang deserialization
 */
public class MolangValue implements MathValue {
	private final MathValue value;
	private final boolean returns;

	public MolangValue(MathValue value) {
		this(value, false);
	}

	public MolangValue(MathValue value, boolean isReturn) {
		this.value = value;
		this.returns = isReturn;
	}

	@Override
	public double get() {
		return this.value.get();
	}

	public MathValue getValueHolder() {
		return this.value;
	}

	public boolean isReturnValue() {
		return this.returns;
	}

	public boolean isConstant() {
		return getClass() == MolangValue.class && value instanceof Constant;
	}

	@Override
	public String toString() {
		return (this.returns ? MolangParser.RETURN : "") + this.value.toString();
	}
}
