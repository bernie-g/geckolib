package software.bernie.geckolib.core.molang.expressions;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib.core.molang.LazyVariable;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * An extension of the {@link MolangValue} class, allowing for compound expressions.
 */
public class MolangCompoundValue extends MolangValue {
	public final List<MolangValue> values = new ObjectArrayList<>();
	public final Map<String, LazyVariable> locals = new Object2ObjectOpenHashMap<>();

	public MolangCompoundValue(MolangValue baseValue) {
		super(baseValue);

		this.values.add(baseValue);
	}

	@Override
	public double get() {
		double value = 0;

		for (MolangValue molangValue : this.values) {
			value = molangValue.get();
		}

		return value;
	}

	@Override
	public String toString() {
		StringJoiner builder = new StringJoiner("; ");

		for (MolangValue molangValue : this.values) {
			builder.add(molangValue.toString());

			if (molangValue.isReturnValue())
				break;
		}

		return builder.toString();
	}
}
