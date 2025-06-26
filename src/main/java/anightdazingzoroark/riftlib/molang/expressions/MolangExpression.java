package anightdazingzoroark.riftlib.molang.expressions;

import anightdazingzoroark.riftlib.molang.math.Constant;
import anightdazingzoroark.riftlib.molang.math.IValue;
import anightdazingzoroark.riftlib.molang.math.Operation;
import anightdazingzoroark.riftlib.molang.MolangParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class MolangExpression implements IValue {
    public MolangParser context;

    public static boolean isZero(anightdazingzoroark.riftlib.molang.expressions.MolangExpression expression) {
        return isConstant(expression, (double)0.0F);
    }

    public static boolean isOne(anightdazingzoroark.riftlib.molang.expressions.MolangExpression expression) {
        return isConstant(expression, (double)1.0F);
    }

    public static boolean isConstant(anightdazingzoroark.riftlib.molang.expressions.MolangExpression expression, double x) {
        if (!(expression instanceof MolangValue)) {
            return false;
        } else {
            MolangValue value = (MolangValue)expression;
            return value.value instanceof Constant && Operation.equals(value.value.get(), x);
        }
    }

    public static boolean isExpressionConstant(anightdazingzoroark.riftlib.molang.expressions.MolangExpression expression) {
        if (expression instanceof MolangValue) {
            MolangValue value = (MolangValue)expression;
            return value.value instanceof Constant;
        } else {
            return false;
        }
    }

    public MolangExpression(MolangParser context) {
        this.context = context;
    }

    public JsonElement toJson() {
        return new JsonPrimitive(this.toString());
    }
}
