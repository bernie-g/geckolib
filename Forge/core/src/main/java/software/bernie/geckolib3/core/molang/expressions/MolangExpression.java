package software.bernie.geckolib3.core.molang.expressions;

import com.eliotlash.mclib.math.Constant;
import com.eliotlash.mclib.math.IValue;
import com.eliotlash.mclib.math.Operation;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class MolangExpression implements IValue {
	public static boolean isZero(MolangExpression expression) {
		return isConstantValue(expression, 0);
	}

	public static boolean isOne(MolangExpression expression) {
		return isConstantValue(expression, 1);
	}

	public static boolean isConstantValue(MolangExpression expression, double value) {
		return isConstant(expression) && Operation.equals(expression.get(), value);
	}

	public static boolean isConstant(MolangExpression expression) {
		return expression instanceof MolangValue molangValue && molangValue.value instanceof Constant;
	}

	public JsonElement toJson() {
		return new JsonPrimitive(toString());
	}
}
