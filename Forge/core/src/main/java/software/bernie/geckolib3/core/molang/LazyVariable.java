package software.bernie.geckolib3.core.molang;

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

	@Override
	public void set(double value) {
		this.valueSupplier = () -> value;
	}

	public void set(DoubleSupplier valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

	@Override
	public double get() {
		return this.valueSupplier.getAsDouble();
	}

	public static LazyVariable from(Variable variable) {
		return new LazyVariable(variable.getName(), variable.get());
	}
}
