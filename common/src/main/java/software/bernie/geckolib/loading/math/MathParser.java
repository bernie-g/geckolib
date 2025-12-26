package software.bernie.geckolib.loading.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Util;
import org.apache.logging.log4j.Level;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.loading.math.function.MathFunction;
import software.bernie.geckolib.loading.math.function.generic.*;
import software.bernie.geckolib.loading.math.function.limit.ClampFunction;
import software.bernie.geckolib.loading.math.function.limit.MaxFunction;
import software.bernie.geckolib.loading.math.function.limit.MinFunction;
import software.bernie.geckolib.loading.math.function.misc.PiFunction;
import software.bernie.geckolib.loading.math.function.misc.ToDegFunction;
import software.bernie.geckolib.loading.math.function.misc.ToRadFunction;
import software.bernie.geckolib.loading.math.function.random.DieRollFunction;
import software.bernie.geckolib.loading.math.function.random.DieRollIntegerFunction;
import software.bernie.geckolib.loading.math.function.random.RandomFunction;
import software.bernie.geckolib.loading.math.function.random.RandomIntegerFunction;
import software.bernie.geckolib.loading.math.function.round.*;
import software.bernie.geckolib.loading.math.value.*;
import software.bernie.geckolib.object.CompoundException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;

/**
 * Mathematical expression parser that breaks down String-expressions into tokenised objects that can be used for automated computation.
 * <p>
 * Original design: <a href="https://github.com/fadookie/particleman/tree/be1ce93c3cbd0f894742e3f41c0c6b23880be046/mclib">McLib - McHorse, Eliot Lash, Hiroku</a>
 * under <a href="https://github.com/fadookie/particleman/blob/be1ce93c3cbd0f894742e3f41c0c6b23880be046/LICENSE-mclib.md">MIT License</a>
 * <p>
 * Overhauled by Tslat for GeckoLib and redesigned specifically for <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/molangreference/examples/molangconcepts/molangintroduction?view=minecraft-bedrock-stable">Molang</a> use
 */
public class MathParser {
    private static final Pattern EXPRESSION_FORMAT = Pattern.compile("^[\\w\\s_+-/*%^&|<>=!?:.,()]+$");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private static final Pattern NUMERIC = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern VALID_DOUBLE = Pattern.compile("[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\d+)(\\.)?((\\d+)?)([eE][+-]?(\\d+))?)|(\\.(\\d+)([eE][+-]?(\\d+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\d+)))[fFdD]?))[\\x00-\\x20]*");
    private static final String MOLANG_RETURN = "return ";
    private static final String STATEMENT_DELIMITER = ";";
    private static final Map<String, MathFunction.Factory<?>> FUNCTION_FACTORIES = Util.make(new ConcurrentHashMap<>(18), map -> {
        map.put("math.abs", AbsFunction::new);
        map.put("math.acos", ACosFunction::new);
        map.put("math.asin", ASinFunction::new);
        map.put("math.atan", ATanFunction::new);
        map.put("math.atan2", ATan2Function::new);
        map.put("math.ceil", CeilFunction::new);
        map.put("math.clamp", ClampFunction::new);
        map.put("math.cos", CosFunction::new);
        map.put("math.die_roll", DieRollFunction::new);
        map.put("math.die_roll_integer", DieRollIntegerFunction::new);
        map.put("math.exp", ExpFunction::new);
        map.put("math.floor", FloorFunction::new);
        map.put("math.hermite_blend", HermiteBlendFunction::new);
        map.put("math.lerp", LerpFunction::new);
        map.put("math.lerprotate", LerpRotFunction::new);
        map.put("math.ln", LogFunction::new);
        map.put("math.max", MaxFunction::new);
        map.put("math.min", MinFunction::new);
        map.put("math.mod", ModFunction::new);
        map.put("math.pi", PiFunction::new);
        map.put("math.pow", PowFunction::new);
        map.put("math.random", RandomFunction::new);
        map.put("math.random_integer", RandomIntegerFunction::new);
        map.put("math.round", RoundFunction::new);
        map.put("math.sin", SinFunction::new);
        map.put("math.sqrt", SqrtFunction::new);
        map.put("math.to_deg", ToDegFunction::new);
        map.put("math.to_rad", ToRadFunction::new);
        map.put("math.trunc", TruncateFunction::new);
    });

    /**
     * @return Whether a {@link MathFunction} has been registered under the given expression name
     */
    public static boolean isFunctionRegistered(String name) {
        return FUNCTION_FACTORIES.containsKey(name);
    }

    /**
     * Register a new {@link MathFunction} to be handled by GeckoLib for parsing and internal use.
     * <p>
     * Overrides are supported, but should be avoided unless specifically needed
     *
     * @param name The string representation of the function. This will be the parsed value from input math strings.
     * @param factory The constructor-factory for the given function
     */
    public static void registerFunction(String name, MathFunction.Factory<?> factory) {
        if (FUNCTION_FACTORIES.put(name, factory) != null)
            GeckoLibConstants.LOGGER.log(Level.WARN, "Duplicate registration of MathFunction: '{}'. Ignore if intentional override", name);

        GeckoLibConstants.LOGGER.log(Level.DEBUG, "Registered MathFunction '{}'", name);
    }

    /**
     * Construct a {@link MathFunction} from the given symbol and values
     *
     * @param name The expression name of the function
     * @param values The input values for the function
     * @return A new instance of the MathFunction
     */
    @SuppressWarnings("unchecked")
    public static <T extends MathFunction> Optional<T> buildFunction(String name, MathValue... values) {
        if (!FUNCTION_FACTORIES.containsKey(name))
            return Optional.empty();

        return Optional.of((T)FUNCTION_FACTORIES.get(name).create(values));
    }

    /**
     * Register a new {@link Variable} with the math parsing system
     * <p>
     * Technically supports overriding by matching keys, though you should try to update the existing variable instances instead if possible
     */
    public static void registerVariable(Variable variable) {
        MolangQueries.registerVariable(variable);
    }

    /**
     * @return The registered {@link Variable} instance for the given name
     */
    public static Variable getVariableFor(String name) {
        return MolangQueries.getVariableFor(name);
    }

    /**
     * Set the value of a {@link Variable} in the Molang parser, creating a new Variable instance as needed
     *
     * @param name The name of the variable - this should match the name in the expression string
     * @param value The new value to set the Variable to
     */
    public static void setVariable(String name, ToDoubleFunction<ControllerState> value) {
        getVariableFor(name).set(value);
    }

    /**
     * Parse a JsonElement into a compiled {@link MathValue} instance, ready for use
     */
    public static MathValue parseJson(JsonElement element) {
        if (!(element instanceof JsonPrimitive primitive) || primitive.isBoolean())
            throw new CompoundException("Bad formatting on Molang expression, expected single value, received: " + element.getClass().getSimpleName());

        if (primitive.isNumber())
            return new Constant(primitive.getAsDouble());

        if (primitive.isString()) {
            String value = primitive.getAsString();

            if (VALID_DOUBLE.matcher(value).matches())
                return new Constant(Double.parseDouble(value));

            return compileMolang(value);
        }

        return new Constant(0);
    }

    /**
     * A wrapper around the expression parsing system to optionally support Molang-specific handling for things like compound expressions
     *
     * @param expression The math and/or Molang expression to be parsed
     * @return A compiled {@link MathValue}, ready for use
     */
    public static MathValue compileMolang(String expression) {
        if (expression.startsWith(MOLANG_RETURN)) {
            expression = expression.substring(MOLANG_RETURN.length());

            if (expression.contains(STATEMENT_DELIMITER))
                expression = expression.substring(0, expression.indexOf(STATEMENT_DELIMITER));
        }
        else if (expression.contains(STATEMENT_DELIMITER)) {
            final String[] subExpressions = expression.split(STATEMENT_DELIMITER);
            final List<MathValue> subValues = new ObjectArrayList<>(subExpressions.length);

            for (String subExpression : subExpressions) {
                boolean isReturn = subExpression.startsWith(MOLANG_RETURN);

                if (isReturn)
                    subExpression = subExpression.substring(MOLANG_RETURN.length());

                subValues.add(compileExpression(subExpression));

                if (isReturn)
                    break;
            }

            return new CompoundValue(subValues.toArray(new MathValue[0]));
        }

        return compileExpression(expression);
    }

    /**
     * Parse and compile a full expression into a single {@link MathValue} object
     */
    public static MathValue compileExpression(String expression) {
        try {
            return parseSymbols(compileSymbols(decomposeExpression(expression)));
        }
        catch (CompoundException ex) {
            throw ex.withMessage("Failed to parse expression '" + expression + "'");
        }
    }

    /**
     * Breakdown an expression into component characters, sanity-checking for invalid characters, stripping out whitespace, and pre-checking group parenthesis balancing
     */
    public static char[] decomposeExpression(String expression) throws CompoundException {
        if (!EXPRESSION_FORMAT.matcher(expression).matches())
            throw new CompoundException("Invalid characters found in expression: '" + expression + "'");

        final char[] chars = WHITESPACE.matcher(expression).replaceAll("").toLowerCase(Locale.ROOT).toCharArray();
        int groupState = 0;

        for (char character : chars) {
            if (character == '(') {
                groupState++;
            }
            else if (character == ')') {
                groupState--;
            }
            
            if (groupState < 0)
                throw new CompoundException("Closing parenthesis before opening parenthesis in expression '" + expression + "'");
        }
        
        if (groupState != 0)
            throw new CompoundException("Uneven parenthesis in expression, each opening brace must have a pairing close brace '" + expression + "'");

        return chars;
    }

    /**
     * Attempt to construct the most relevant operator given the input character array and start index
     * <p>
     * This allows for arbitrary-length operators and best-matching for partially-colliding operators
     */
    protected static @Nullable String tryMergeOperativeSymbols(char[] chars, int index) {
        char ch = chars[index];

        if (!Operator.isOperativeSymbol(ch))
            return null;

        int maxLength = Math.min(chars.length - index, Operator.maxOperatorLength());

        for (int length = maxLength; length > 0; length--) {
            String testOperator = String.copyValueOf(chars, index, length);

            if (Operator.isOperator(testOperator))
                return testOperator;
        }

        if (ch == '?' || ch == ':' || ch == ',')
            return String.valueOf(ch);

        return null;
    }

    /**
     * Compile a collection of 'symbols' from the given char array representing the expression split into individual characters
     *
     * @return A list of either string symbols or a group of pre-compiled arguments of a grouping
     * <p>
     * This list is formatted such that each entry is either:
     * <ul>
     *     <li>A self-contained value or expression</li>
     *     <li>A pre-compiled {@link MathValue} representing an expression group</li>
     *     <li>A {@link MathFunction} name immediately followed by a pre-compiled {@link MathValue} argument group</li>
     * </ul>
     */
    public static List<Either<String, List<MathValue>>> compileSymbols(char[] chars) {
        final List<Either<String, List<MathValue>>> symbols = new ObjectArrayList<>();
        final StringBuilder buffer = new StringBuilder();
        int lastSymbolIndex = -1;

        for (int i = 0; i < chars.length; i++) {
            final char ch = chars[i];

            if (ch == '-' && buffer.isEmpty() && (symbols.isEmpty() || lastSymbolIndex == symbols.size() - 1)) {
                buffer.append(ch);

                continue;
            }

            final String operator = tryMergeOperativeSymbols(chars, i);

            if (operator != null) {
                i += operator.length() - 1;

                if (!buffer.isEmpty())
                    symbols.add(Either.left(buffer.toString()));

                lastSymbolIndex = symbols.size();

                symbols.add(Either.left(operator));
                buffer.setLength(0);
            }
            else if (ch == '(') {
                if (!buffer.isEmpty()) {
                    symbols.add(Either.left(buffer.toString()));
                    buffer.setLength(0);
                }

                List<MathValue> subValues = new ObjectArrayList<>();
                int groupState = 1;

                for (int j = i + 1; j < chars.length; j++) {
                    final char groupChar = chars[j];

                    if (groupChar == '(') {
                        groupState++;
                    }
                    else if (groupChar == ')') {
                        groupState--;
                    }
                    else if (groupChar == ',' && groupState == 1) {
                        subValues.add(parseSymbols(compileSymbols(buffer.toString().toCharArray())));
                        buffer.setLength(0);

                        continue;
                    }

                    if (groupState == 0) {
                        if (!buffer.isEmpty())
                            subValues.add(parseSymbols(compileSymbols(buffer.toString().toCharArray())));

                        i = j;

                        symbols.add(Either.right(subValues));
                        buffer.setLength(0);

                        break;
                    }
                    else {
                        buffer.append(groupChar);
                    }
                }
            }
            else {
                buffer.append(ch);
            }
        }

        if (!buffer.isEmpty())
            symbols.add(Either.left(buffer.toString()));

        return symbols;
    }

    /**
     * Compiles a given raw list of {@link #compileSymbols(char[]) symbols} into a singular {@link MathValue}, ready for use
     *
     * @throws CompoundException If the given symbols list cannot be compiled down into a MathValue
     */
    public static MathValue parseSymbols(List<Either<String, List<MathValue>>> symbols) throws CompoundException {
        if (symbols.size() == 2) {
            Optional<String> prefix = symbols.getFirst().left().filter(left -> left.startsWith("-") || left.startsWith("!") || isFunctionRegistered(left));
            Optional<List<MathValue>> group = symbols.get(1).right();

            if (prefix.isPresent() && group.isPresent()) {
                Optional<? extends MathValue> value = compileFunction(prefix.get(), group.get());

                return value.orElseThrow(() -> new CompoundException("Unable to parse function '" + prefix.get() + "' with arguments: " + group.get()));
            }
        }

        return compileValue(symbols).orElseThrow(() -> new CompoundException("Unable to parse compiled symbols from expression: " + symbols));
    }

    /**
     * Compile the given {@link #compileSymbols(char[]) symbols} down into a singular {@link MathValue}, ready for use
     *
     * @return A compiled MathValue instance, or null if not applicable
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    protected static Optional<? extends MathValue> compileValue(List<Either<String, List<MathValue>>> symbols) throws CompoundException {
        if (symbols.size() == 1)
            return compileSingleValue(symbols.getFirst());

        Optional<Ternary> ternary = compileTernary(symbols);

        if (ternary.isPresent())
            return ternary;

        return compileCalculation(symbols);
    }

    /**
     * Compile a singular-argument {@link MathValue} instance from the given symbols list, if applicable
     *
     * @return A compiled MathValue value, or null if not applicable
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    protected static Optional<MathValue> compileSingleValue(Either<String, List<MathValue>> symbol) throws CompoundException {
        if (symbol.right().isPresent())
            return Optional.of(new Group(symbol.right().get().getFirst()));

        //noinspection NullableProblems
        return symbol.left().map(string -> {
            if (string.startsWith("!"))
                return compileSingleValue(Either.left(string.substring(1))).map(BooleanNegate::new).orElse(null);

            if (isNumeric(string))
                return new Constant(Double.parseDouble(string));

            if (isLikelyVariable(string)) {
                if (string.startsWith("-"))
                    return new Negative(getVariableFor(string.substring(1)));

                return getVariableFor(string);
            }

            if (isFunctionRegistered(string))
                return compileFunction(string, List.of()).orElse(null);

            return null;
        });
    }

    /**
     * Compile a MathValue value instance from the given symbols list, if applicable
     *
     * @return A compiled {@link Calculation} or {@link VariableAssignment} value, or empty if a calculation is not applicable to the provided symbols
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    protected static Optional<MathValue> compileCalculation(List<Either<String, List<MathValue>>> symbols) throws CompoundException  {
        final int symbolCount = symbols.size();
        int operatorIndex = -1;
        Operator lastOperator = null;

        for (int i = 1; i < symbolCount; i++) {
            Operator operator = symbols.get(i).left()
                    .filter(Operator::isOperator)
                    .map(MathParser::getOperatorFor).orElse(null);

            if (operator == null)
                continue;

            if (operator == Operator.ASSIGN_VARIABLE) {
                if (!(parseSymbols(symbols.subList(0, i)) instanceof Variable variable))
                    throw new CompoundException("Attempted to assign a value to a non-variable");

                return Optional.of(new VariableAssignment(variable, parseSymbols(symbols.subList(i + 1, symbolCount))));
            }

            if (lastOperator == null || !operator.takesPrecedenceOver(lastOperator)) {
                operatorIndex = i;
                lastOperator = operator;
            }
            else {
                break;
            }
        }

        return lastOperator == null ? Optional.empty() : Optional.of(new Calculation(lastOperator, parseSymbols(symbols.subList(0, operatorIndex)), parseSymbols(symbols.subList(operatorIndex + 1, symbolCount))));
    }

    /**
     * Compile a {@link Ternary} value instance from the given symbols list, if applicable
     *
     * @return A compiled Ternary value, or empty if a ternary does not apply to the provided symbols
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    protected static Optional<Ternary> compileTernary(List<Either<String, List<MathValue>>> symbols) throws CompoundException  {
        final int symbolCount = symbols.size();

        if (symbolCount < 3)
            return Optional.empty();

        Supplier<MathValue> condition = null;
        Supplier<MathValue> ifTrue = null;
        int ternaryState = 0;
        int lastColon = -1;
        int queryIndex = -1;

        for (int i = 0; i < symbolCount; i++) {
            final int i2 = i;
            final String string = symbols.get(i).left().orElse(null);

            if ("?".equals(string)) {
                if (condition == null) {
                    condition = () -> parseSymbols(symbols.subList(0, i2));
                    queryIndex = i2 + 1;
                }

                ternaryState++;
            }
            else if (":".equals(string)) {
                if (ternaryState == 1 && ifTrue == null && queryIndex > 0) {
                    final int queryIndex2 = queryIndex;
                    ifTrue = () -> parseSymbols(symbols.subList(queryIndex2, i2));
                }

                ternaryState--;
                lastColon = i;
            }
        }

        if (ternaryState == 0 && condition != null && ifTrue != null && lastColon < symbolCount - 1)
            return Optional.of(new Ternary(condition.get(), ifTrue.get(), parseSymbols(symbols.subList(lastColon + 1, symbolCount))));

        return Optional.empty();
    }

    /**
     * Compiles a {@link MathValue} for the given symbols list, if applicable.
     * <p>
     * Note that due to parsing flexibility, this method doesn't necessarily generate a {@link MathFunction}, as some calls may be for value-value pairs instead
     *
     * @param name The name of the function or value
     * @param args The symbols list for the value
     * @return A compiled MathValue, or empty if not function is registered by the given name
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    protected static Optional<? extends MathValue> compileFunction(String name, List<MathValue> args) throws CompoundException {
        if (name.startsWith("!")) {
            if (name.length() == 1)
                return Optional.of(new BooleanNegate(args.getFirst()));

            return compileFunction(name.substring(1), args).map(BooleanNegate::new);
        }

        if (name.startsWith("-")) {
            if (name.length() == 1)
                return Optional.of(new Negative(args.getFirst()));

            return compileFunction(name.substring(1), args).map(Negative::new);
        }

        if (!isFunctionRegistered(name))
            return Optional.empty();

        return buildFunction(name, args.toArray(new MathValue[0]));
    }

    /**
     * Determine if the given string can be considered numeric, supporting both negative values and decimal values, but not strings omitting a preceding digit before a decimal point
     *
     * @return Whether the string is numeric
     */
    public static boolean isNumeric(String string) {
        return NUMERIC.matcher(string).matches();
    }

    /**
     * Get an {@link Operator} for a given operator string, throwing an exception if one does not exist
     */
    protected static Operator getOperatorFor(String op) throws CompoundException {
        return Operator.getOperatorFor(op).orElseThrow(() -> new CompoundException("Unknown operator symbol '" + op + "'"));
    }

    /**
     * Determine if the given string is likely to be an existing or new variable declaration
     * <p>
     * Functionally, this is just a confirmation-by-elimination check since names don't really have a defined form
     */
    protected static boolean isLikelyVariable(String string) {
        if (MolangQueries.isExistingVariable(string))
            return true;

        return !isNumeric(string) && !isFunctionRegistered(string) && !Operator.isOperator(string) && !string.equals("?") && !string.equals(":");
    }
}
