package software.bernie.geckolib.core.molang;

import com.eliotlash.mclib.math.Variable;

import java.util.function.DoubleSupplier;

/**
 * Lazy override of Variable, to allow for deferred value calculation. <br>
 * Optimises rendering as values are not touched until needed (if at all)
 */
public class LazyVariable extends Variable {
	private DoubleSupplier valueSupplier;

	public LazyVariable(String name, double value) {
		this(name, () -> value);
	}

	public LazyVariable(String name, DoubleSupplier valueSupplier) {
		super(name, 0);

		this.valueSupplier = valueSupplier;
	}

	/**
	 * Set the new value for the variable, acting as a constant
	 */
	@Override
	public void set(double value) {
		this.valueSupplier = () -> value;
	}

	/**
	 * Set the new value supplier for the variable
	 */
	public void set(DoubleSupplier valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

	/**
	 * Get the current value of the variable
	 */
	@Override
	public double get() {
		return this.valueSupplier.getAsDouble();
	}

	/**
	 * Instantiates a copy of this variable from this variable's current value and name
	 */
	public static LazyVariable from(Variable variable) {
		return new LazyVariable(variable.getName(), variable.get());
	}
}
