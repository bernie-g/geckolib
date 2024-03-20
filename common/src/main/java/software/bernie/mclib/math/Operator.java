package software.bernie.mclib.math;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * <a href="https://en.wikipedia.org/wiki/Operator_(mathematics)">Mathematical operator</a> representing a single operation
 * <p>
 * Each record should represent a distinct mathematical function for computational purposes, with each function being deterministic and immutable.
 */
public record Operator(String symbol, int precedence, Operation operation) implements Comparable<Operator> {
    private static final Map<String, Operator> OPERATORS = new HashMap<>(14);

    public static final Operator ADD = register("+", 1, (a, b) -> a + b);
    public static final Operator SUB = register("-", 1, (a, b) -> a - b);
    public static final Operator MUL = register("*", 2, (a, b) -> a * b);
    public static final Operator DIV = register("/", 2, (a, b) -> b == 0 ? a : a / b);
    public static final Operator MOD = register("%", 2, (a, b) -> b == 0 ? a : a % b);
    public static final Operator POW = register("^", 3, Math::pow);
    public static final Operator AND = register("&&", 5, (a, b) -> a != 0 && b != 0 ? 1 : 0);
    public static final Operator OR = register("||", 5, (a, b) -> a != 0 || b != 0 ? 1 : 0);
    public static final Operator LT = register("<", 5, (a, b) -> a < b ? 1 : 0);
    public static final Operator LTE = register("<=", 5, (a, b) -> a <= b ? 1 : 0);
    public static final Operator GT = register(">", 5, (a, b) -> a > b ? 1 : 0);
    public static final Operator GTE = register(">=", 5, (a, b) -> a >= b ? 1 : 0);
    public static final Operator EQUAL = register("==", 5, (a, b) -> Math.abs(a - b) < 0.00001 ? 1 : 0);
    public static final Operator NOT_EQUAL = register("!=", 5, (a, b) -> Math.abs(a - b) >= 0.00001 ? 1 : 0);

    /**
     * Instantiate and register a new mathematical operator.
     * Note that it should be a functionally distinct operator from other existing operators.
     *
     * @param symbol The expressed mathematical symbol for this operator
     * @param precedence The precedence value for this operator, in relation to other operators
     * @param operation The computational function for this operator
     */
    public static Operator register(String symbol, int precedence, Operation operation) {
        final Operator operator = new Operator(symbol, precedence, operation);

        if (OPERATORS.put(symbol, operator) != null)
            throw new IllegalArgumentException("Attempting to register an already existing operator! '" + symbol + "'");

        return operator;
    }

    /**
     * @param symbol The mathematical/expression symbol representing an Operator
     * @return Whether an Operator has been registered for the given symbol
     */
    public static boolean isOperator(String symbol) {
        return OPERATORS.containsKey(symbol);
    }

    /**
     * @param symbol The mathematical/expression symbol representing an Operator
     * @return An {@link Optional} potentially containing the Operator for the given symbol
     */
    public static Optional<Operator> getOperatorFor(String symbol) {
        return Optional.ofNullable(OPERATORS.get(symbol));
    }

    /**
     * Compute the resultant value of the two input values for this operation
     * @param argA The first input argument
     * @param argB The second input argument
     * @return The computed value of the two inputs
     */
    public double compute(double argA, double argB) {
        return this.operation.compute(argA, argB);
    }

    @Override
    public int compareTo(@NotNull Operator operator) {
        return Integer.compare(this.precedence, operator.precedence);
    }

    /**
     * Determine whether this operator takes mathematical precedence over the other operator
     */
    public boolean takesPrecedenceOver(Operator operator) {
        return compareTo(operator) > 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.symbol);
    }

    /**
     * Functional interface representing the computational work of an {@link Operator}
     */
    @FunctionalInterface
    public interface Operation {
        /**
         * Unboxed equivalent of {@link BiFunction} for computing the mathematical result of two input arguments
         *
         * @param argA The first input argument
         * @param argB The second input argument
         * @return The computed value of the two inputs
         */
        double compute(double argA, double argB);
    }
}
