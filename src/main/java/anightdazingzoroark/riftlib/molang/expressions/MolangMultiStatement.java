package anightdazingzoroark.riftlib.molang.expressions;

import anightdazingzoroark.riftlib.molang.math.Variable;
import anightdazingzoroark.riftlib.molang.MolangParser;

import java.util.*;

public class MolangMultiStatement extends MolangExpression {
    public List<anightdazingzoroark.riftlib.molang.expressions.MolangExpression> expressions = new ArrayList();
    public Map<String, Variable> locals = new HashMap();

    public MolangMultiStatement(MolangParser context) {
        super(context);
    }

    public double get() {
        double value = (double)0.0F;

        for(anightdazingzoroark.riftlib.molang.expressions.MolangExpression expression : this.expressions) {
            value = expression.get();
        }

        return value;
    }

    public String toString() {
        StringJoiner builder = new StringJoiner("; ");

        for(MolangExpression expression : this.expressions) {
            builder.add(expression.toString());
            if (expression instanceof MolangValue && ((MolangValue)expression).returns) {
                break;
            }
        }

        return builder.toString();
    }
}
