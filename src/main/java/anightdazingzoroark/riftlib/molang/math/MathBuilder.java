package anightdazingzoroark.riftlib.molang.math;

import anightdazingzoroark.riftlib.molang.math.functions.Function;
import anightdazingzoroark.riftlib.molang.math.functions.classic.*;
import anightdazingzoroark.riftlib.molang.math.functions.limit.*;
import anightdazingzoroark.riftlib.molang.math.functions.rounding.Ceil;
import anightdazingzoroark.riftlib.molang.math.functions.rounding.Floor;
import anightdazingzoroark.riftlib.molang.math.functions.rounding.Round;
import anightdazingzoroark.riftlib.molang.math.functions.rounding.Trunc;
import anightdazingzoroark.riftlib.molang.math.functions.utility.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathBuilder {
    public Map<String, Variable> variables = new HashMap();
    public Map<String, Class<? extends Function>> functions = new HashMap();

    public MathBuilder() {
        this.register(new Variable("PI", Math.PI));
        this.register(new Variable("E", Math.E));
        this.functions.put("floor", Floor.class);
        this.functions.put("round", Round.class);
        this.functions.put("ceil", Ceil.class);
        this.functions.put("trunc", Trunc.class);
        this.functions.put("clamp", Clamp.class);
        this.functions.put("max", Max.class);
        this.functions.put("min", Min.class);
        this.functions.put("abs", Abs.class);
        this.functions.put("acos", ACos.class);
        this.functions.put("asin", ASin.class);
        this.functions.put("atan", ATan.class);
        this.functions.put("atan2", ATan2.class);
        this.functions.put("cos", Cos.class);
        this.functions.put("sin", Sin.class);
        this.functions.put("exp", Exp.class);
        this.functions.put("ln", Ln.class);
        this.functions.put("sqrt", Sqrt.class);
        this.functions.put("mod", Mod.class);
        this.functions.put("pow", Pow.class);
        this.functions.put("lerp", Lerp.class);
        this.functions.put("lerprotate", LerpRotate.class);
        this.functions.put("hermite_blend", HermiteBlend.class);
        this.functions.put("die_roll", DieRoll.class);
        this.functions.put("die_roll_integer", DieRollInteger.class);
        this.functions.put("random", Random.class);
        this.functions.put("random_integer", RandomInteger.class);
    }

    public void register(anightdazingzoroark.riftlib.molang.math.Variable variable) {
        this.variables.put(variable.getName(), variable);
    }

    public anightdazingzoroark.riftlib.molang.math.IValue parse(String expression) throws Exception {
        return this.parseSymbols(this.breakdownChars(this.breakdown(expression)));
    }

    public String[] breakdown(String expression) throws Exception {
        if (!expression.matches("^[\\w\\d\\s_+-/*%^&|<>=!?:.,()]+$")) {
            throw new Exception("Given expression '" + expression + "' contains illegal characters!");
        } else {
            expression = expression.replaceAll("\\s+", "");
            String[] chars = expression.split("(?!^)");
            int left = 0;
            int right = 0;

            for(String s : chars) {
                if (s.equals("(")) {
                    ++left;
                } else if (s.equals(")")) {
                    ++right;
                }
            }

            if (left != right) {
                throw new Exception("Given expression '" + expression + "' has more uneven amount of parenthesis, there are " + left + " open and " + right + " closed!");
            } else {
                return chars;
            }
        }
    }

    public List<Object> breakdownChars(String[] chars) {
        List<Object> symbols = new ArrayList();
        String buffer = "";
        int len = chars.length;

        for(int i = 0; i < len; ++i) {
            String s = chars[i];
            boolean longOperator = i > 0 && this.isOperator(chars[i - 1] + s);
            if (!this.isOperator(s) && !longOperator && !s.equals(",")) {
                if (s.equals("(")) {
                    if (!buffer.isEmpty()) {
                        symbols.add(buffer);
                        buffer = "";
                    }

                    int counter = 1;

                    for(int j = i + 1; j < len; ++j) {
                        String c = chars[j];
                        if (c.equals("(")) {
                            ++counter;
                        } else if (c.equals(")")) {
                            --counter;
                        }

                        if (counter == 0) {
                            symbols.add(this.breakdownChars(buffer.split("(?!^)")));
                            i = j;
                            buffer = "";
                            break;
                        }

                        buffer = buffer + c;
                    }
                } else {
                    buffer = buffer + s;
                }
            } else {
                if (s.equals("-")) {
                    int size = symbols.size();
                    boolean isFirst = size == 0 && buffer.isEmpty();
                    boolean isOperatorBehind = size > 0 && (this.isOperator(symbols.get(size - 1)) || symbols.get(size - 1).equals(",")) && buffer.isEmpty();
                    if (isFirst || isOperatorBehind) {
                        buffer = buffer + s;
                        continue;
                    }
                }

                if (longOperator) {
                    s = chars[i - 1] + s;
                    buffer = buffer.substring(0, buffer.length() - 1);
                }

                if (!buffer.isEmpty()) {
                    symbols.add(buffer);
                    buffer = "";
                }

                symbols.add(s);
            }
        }

        if (!buffer.isEmpty()) {
            symbols.add(buffer);
        }

        return symbols;
    }

    public anightdazingzoroark.riftlib.molang.math.IValue parseSymbols(List<Object> symbols) throws Exception {
        anightdazingzoroark.riftlib.molang.math.IValue ternary = this.tryTernary(symbols);
        if (ternary != null) {
            return ternary;
        } else {
            int size = symbols.size();
            if (size == 1) {
                return this.valueFromObject(symbols.get(0));
            } else {
                if (size == 2) {
                    Object first = symbols.get(0);
                    Object second = symbols.get(1);
                    if ((this.isVariable(first) || first.equals("-")) && second instanceof List) {
                        return this.createFunction((String)first, (List)second);
                    }
                }

                int lastOp = this.seekLastOperator(symbols);

                int leftOp;
                for(int op = lastOp; op != -1; op = leftOp) {
                    leftOp = this.seekLastOperator(symbols, op - 1);
                    if (leftOp != -1) {
                        Operation left = this.operationForOperator((String)symbols.get(leftOp));
                        Operation right = this.operationForOperator((String)symbols.get(op));
                        if (right.value > left.value) {
                            anightdazingzoroark.riftlib.molang.math.IValue leftValue = this.parseSymbols(symbols.subList(0, leftOp));
                            anightdazingzoroark.riftlib.molang.math.IValue rightValue = this.parseSymbols(symbols.subList(leftOp + 1, size));
                            return new Operator(left, leftValue, rightValue);
                        }

                        if (left.value > right.value) {
                            Operation initial = this.operationForOperator((String)symbols.get(lastOp));
                            if (initial.value < left.value) {
                                anightdazingzoroark.riftlib.molang.math.IValue leftValue = this.parseSymbols(symbols.subList(0, lastOp));
                                anightdazingzoroark.riftlib.molang.math.IValue rightValue = this.parseSymbols(symbols.subList(lastOp + 1, size));
                                return new Operator(initial, leftValue, rightValue);
                            }

                            anightdazingzoroark.riftlib.molang.math.IValue leftValue = this.parseSymbols(symbols.subList(0, op));
                            anightdazingzoroark.riftlib.molang.math.IValue rightValue = this.parseSymbols(symbols.subList(op + 1, size));
                            return new Operator(right, leftValue, rightValue);
                        }
                    }
                }

                Operation operation = this.operationForOperator((String)symbols.get(lastOp));
                return new Operator(operation, this.parseSymbols(symbols.subList(0, lastOp)), this.parseSymbols(symbols.subList(lastOp + 1, size)));
            }
        }
    }

    protected int seekLastOperator(List<Object> symbols) {
        return this.seekLastOperator(symbols, symbols.size() - 1);
    }

    protected int seekLastOperator(List<Object> symbols, int offset) {
        for(int i = offset; i >= 0; --i) {
            Object o = symbols.get(i);
            if (this.isOperator(o)) {
                return i;
            }
        }

        return -1;
    }

    protected int seekFirstOperator(List<Object> symbols) {
        return this.seekFirstOperator(symbols, 0);
    }

    protected int seekFirstOperator(List<Object> symbols, int offset) {
        int i = offset;

        for(int size = symbols.size(); i < size; ++i) {
            Object o = symbols.get(i);
            if (this.isOperator(o)) {
                return i;
            }
        }

        return -1;
    }

    protected anightdazingzoroark.riftlib.molang.math.IValue tryTernary(List<Object> symbols) throws Exception {
        int question = -1;
        int questions = 0;
        int colon = -1;
        int colons = 0;
        int size = symbols.size();

        for(int i = 0; i < size; ++i) {
            Object object = symbols.get(i);
            if (object instanceof String) {
                if (object.equals("?")) {
                    if (question == -1) {
                        question = i;
                    }

                    ++questions;
                } else if (object.equals(":")) {
                    if (colons + 1 == questions && colon == -1) {
                        colon = i;
                    }

                    ++colons;
                }
            }
        }

        if (questions == colons && question > 0 && question + 1 < colon && colon < size - 1) {
            return new Ternary(this.parseSymbols(symbols.subList(0, question)), this.parseSymbols(symbols.subList(question + 1, colon)), this.parseSymbols(symbols.subList(colon + 1, size)));
        } else {
            return null;
        }
    }

    protected anightdazingzoroark.riftlib.molang.math.IValue createFunction(String first, List<Object> args) throws Exception {
        if (first.equals("!")) {
            return new Negate(this.parseSymbols(args));
        } else if (first.startsWith("!") && first.length() > 1) {
            return new Negate(this.createFunction(first.substring(1), args));
        } else if (first.equals("-")) {
            return new Negative(new Group(this.parseSymbols(args)));
        } else if (first.startsWith("-") && first.length() > 1) {
            return new Negative(this.createFunction(first.substring(1), args));
        } else if (!this.functions.containsKey(first)) {
            throw new Exception("Function '" + first + "' couldn't be found!");
        } else {
            List<anightdazingzoroark.riftlib.molang.math.IValue> values = new ArrayList();
            List<Object> buffer = new ArrayList();

            for(Object o : args) {
                if (o.equals(",")) {
                    values.add(this.parseSymbols(buffer));
                    buffer.clear();
                } else {
                    buffer.add(o);
                }
            }

            if (!buffer.isEmpty()) {
                values.add(this.parseSymbols(buffer));
            }

            Class<? extends Function> function = (Class)this.functions.get(first);
            Constructor<? extends Function> ctor = function.getConstructor(anightdazingzoroark.riftlib.molang.math.IValue[].class, String.class);
            Function func = (Function)ctor.newInstance(values.toArray(new anightdazingzoroark.riftlib.molang.math.IValue[values.size()]), first);
            return func;
        }
    }

    public anightdazingzoroark.riftlib.molang.math.IValue valueFromObject(Object object) throws Exception {
        if (object instanceof String) {
            String symbol = (String)object;
            if (symbol.startsWith("!")) {
                return new Negate(this.valueFromObject(symbol.substring(1)));
            }

            if (this.isDecimal(symbol)) {
                return new Constant(Double.parseDouble(symbol));
            }

            if (this.isVariable(symbol)) {
                if (symbol.startsWith("-")) {
                    symbol = symbol.substring(1);
                    anightdazingzoroark.riftlib.molang.math.Variable value = this.getVariable(symbol);
                    if (value != null) {
                        return new Negative(value);
                    }
                } else {
                    IValue value = this.getVariable(symbol);
                    if (value != null) {
                        return value;
                    }
                }
            }
        } else if (object instanceof List) {
            return new Group(this.parseSymbols((List)object));
        }

        throw new Exception("Given object couldn't be converted to value! " + object);
    }

    protected anightdazingzoroark.riftlib.molang.math.Variable getVariable(String name) {
        return (Variable)this.variables.get(name);
    }

    protected Operation operationForOperator(String op) throws Exception {
        for(Operation operation : Operation.values()) {
            if (operation.sign.equals(op)) {
                return operation;
            }
        }

        throw new Exception("There is no such operator '" + op + "'!");
    }

    protected boolean isVariable(Object o) {
        return o instanceof String && !this.isDecimal((String)o) && !this.isOperator((String)o);
    }

    protected boolean isOperator(Object o) {
        return o instanceof String && this.isOperator((String)o);
    }

    protected boolean isOperator(String s) {
        return Operation.OPERATORS.contains(s) || s.equals("?") || s.equals(":");
    }

    protected boolean isDecimal(String s) {
        return s.matches("^-?\\d+(\\.\\d+)?$");
    }
}