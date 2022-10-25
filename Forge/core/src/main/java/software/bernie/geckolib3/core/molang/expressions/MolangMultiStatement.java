package software.bernie.geckolib3.core.molang.expressions;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.eliotlash.mclib.math.Variable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib3.core.molang.MolangParser;

public class MolangMultiStatement extends MolangExpression {
	public List<MolangExpression> expressions = new ObjectArrayList<MolangExpression>();
	public Map<String, Variable> locals = new Object2ObjectOpenHashMap<String, Variable>();

	public MolangMultiStatement(MolangParser context) {
		super(context);
	}

	@Override
	public double get() {
		double value = 0;

		for (MolangExpression expression : this.expressions) {
			value = expression.get();
		}

		return value;
	}

	@Override
	public String toString() {
		StringJoiner builder = new StringJoiner("; ");

		for (MolangExpression expression : this.expressions) {
			builder.add(expression.toString());

			if (expression instanceof MolangValue && ((MolangValue) expression).returns) {
				break;
			}
		}

		return builder.toString();
	}
}
