package software.bernie.geckolib.core.molang.expressions;

import software.bernie.mclib.math.MathValue;
import software.bernie.mclib.math.value.Variable;

/**
 * Extension of {@link MolangValue} that additionally sets the value of a provided {@link Variable} when being called.
 */
public class MolangVariableHolder extends MolangValue {
	public Variable variable;

	public MolangVariableHolder(Variable variable, MathValue value) {
		super(value);

		this.variable = variable;
	}

	@Override
	public double get() {
		double value = super.get();

		this.variable.set(value);

		return value;
	}

	@Override
	public String toString() {
		return this.variable.getName() + " = " + super.toString();
	}
}
