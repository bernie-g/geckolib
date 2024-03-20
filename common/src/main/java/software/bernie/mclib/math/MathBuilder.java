package software.bernie.mclib.math;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.mclib.math.function.MathFunction;
import software.bernie.mclib.math.function.generic.*;
import software.bernie.mclib.math.function.limit.ClampFunction;
import software.bernie.mclib.math.function.limit.MaxFunction;
import software.bernie.mclib.math.function.limit.MinFunction;
import software.bernie.mclib.math.function.round.*;
import software.bernie.mclib.math.value.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Mathematical expression parser that breaks down String-expressions into tokenised objects that can be used for automated computation.
 * <p>
 * Original design: <a href="https://github.com/fadookie/particleman/tree/be1ce93c3cbd0f894742e3f41c0c6b23880be046/mclib">McLib - McHorse, Eliot Lash, Hiroku</a>
 * under <a href="https://github.com/fadookie/particleman/blob/be1ce93c3cbd0f894742e3f41c0c6b23880be046/LICENSE-mclib.md">MIT License</a>
 * <p>
 * Overhauled by Tslat for GeckoLib and redesigned specifically for <a href="https://bedrock.dev/docs/1.20.0.0/1.20.80.21/Molang">Molang</a> use
 */
public class MathBuilder {
    private static final Pattern EXPRESSION_FORMAT = Pattern.compile("^[\\w\\s_+-/*%^&|<>=!?:.,()]+$");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private static final Pattern NUMERIC = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern OPERATIVE_SYMBOLS = Pattern.compile("[?:=]");
    private static final Map<String, MathFunction.Factory<?>> FUNCTION_FACTORIES = Util.make(new HashMap<>(18), map -> {
        map.put("math.abs", AbsFunction::new);
        map.put("math.cos", CosFunction::new);
        map.put("math.exp", ExpFunction::new);
        map.put("math.ln", LogFunction::new);
        map.put("math.mod", ModFunction::new);
        map.put("math.pow", PowFunction::new);
        map.put("math.random", RandomFunction::new);
        map.put("math.sin", SinFunction::new);
        map.put("math.sqrt", SqrtFunction::new);
        map.put("math.clamp", ClampFunction::new);
        map.put("math.max", MaxFunction::new);
        map.put("math.min", MinFunction::new);
        map.put("math.ceil", CeilFunction::new);
        map.put("math.floor", FloorFunction::new);
        map.put("math.lerp", LerpFunction::new);
        map.put("math.lerprotate", LerpRotFunction::new);
        map.put("math.round", RoundFunction::new);
        map.put("math.trunc", TruncateFunction::new);
    });
    private static final Map<String, Variable> VARIABLES = Util.make(new HashMap<>(), map -> {
        map.put("PI", new Variable("PI", Math.PI));
        map.put("E", new Variable("E", Math.E));
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
            GeckoLibConstants.LOGGER.log(Level.WARN, "Duplicate registration of MathFunction: '" + name + "'. Ignore if intentional override");

        GeckoLibConstants.LOGGER.log(Level.DEBUG, "Registered MathFunction '" + name + "'");
    }

    /**
     * Construct a {@link MathFunction} from the given symbol and values
     *
     * @param name The expression name of the function
     * @param values The input values for the function
     * @return A new instance of the MathFunction
     */
    @Nullable
    public static <T extends MathFunction> T buildFunction(String name, MathValue... values) {
        if (!FUNCTION_FACTORIES.containsKey(name))
            return null;

        return (T)FUNCTION_FACTORIES.get(name).create(values);
    }

    /**
     * Register a new {@link Variable} with the math parsing system
     * <p>
     * Technically supports overriding by matching keys, though you should try to update the existing variable instances instead if possible
     */
    public static void registerVariable(Variable variable) {
        VARIABLES.put(variable.name(), variable);
    }

    /**
     * @return The registered {@link Variable} instance for the given name
     */
    public static Optional<Variable> getVariableFor(String name) {
        return Optional.ofNullable(VARIABLES.get(name));
    }

    /**
     * Parse and compile a full expression into a single {@link MathValue} object
     */
    public static <L extends List<Either<String, L>>> MathValue compileExpression(String expression) {
        return parseSymbols(compileSymbols(decomposeExpression(expression)));
    }

    /**
     * Breakdown an expression into component characters, sanity-checking for invalid characters, stripping out whitespace, and pre-checking group parenthesis balancing
     */
    public static char[] decomposeExpression(String expression) throws IllegalArgumentException {
        if (!EXPRESSION_FORMAT.matcher(expression).matches())
            throw new IllegalArgumentException("Invalid characters found in expression: '" + expression + "'");

        final char[] chars = WHITESPACE.matcher(expression).replaceAll("").toCharArray();
        int groupState = 0;

        for (char character : chars) {
            if (character == '(') {
                groupState++;
            }
            else if (character == ')') {
                groupState--;
            }
            
            if (groupState < 0)
                throw new IllegalArgumentException("Closing parenthesis before opening parenthesis in expression '" + expression + "'");
        }
        
        if (groupState != 0)
            throw new IllegalArgumentException("Uneven parenthesis in expression, each opening brace must have a pairing close brace '" + expression + "'");

        return chars;
    }

    /**
     * Compile a collection of 'symbols' from the given char array representing the expression split into individual characters
     *
     * @return A list of either string symbols, or a recursively nested collection of compiled symbols
     */
    public static <L extends List<Either<String, L>>> L compileSymbols(char[] chars) {
        final L symbols = (L)new ObjectArrayList<Either<String, L>>();
        final StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            final char ch = chars[i];
            final boolean isMultiCharOperator = i > 0 && isOperativeSymbol(chars[i - 1] + "" + ch);

            if (isOperativeSymbol(ch) || isMultiCharOperator || ch == ',') {
                if (ch == '-' && buffer.isEmpty()) {
                    Object lastSymbol;

                    if (symbols.isEmpty() || isOperativeSymbol(lastSymbol = symbols.get(symbols.size() - 1).left().orElse(null)) || ",".equals(lastSymbol)) {
                        buffer.append(ch);

                        continue;
                    }
                }

                if (isMultiCharOperator) {
                    symbols.add(Either.left(buffer.substring(0, buffer.length() - 1)));
                    symbols.add(Either.left(chars[i - 1] + "" + ch));
                }
                else {
                    symbols.add(Either.left(buffer.toString()));
                }

                buffer.setLength(0);
            }
            else if (ch == '(') {
                if (!buffer.isEmpty()) {
                    symbols.add(Either.left(buffer.toString()));
                    buffer.setLength(0);
                }

                int groupState = 1;

                for (int j = i + 1; i < chars.length; i++) {
                    final char groupChar = chars[j];

                    if (groupChar == '(') {
                        groupState++;
                    }
                    else if (groupChar == ')') {
                        groupState--;
                    }

                    if (groupState == 0) {
                        symbols.add(compileSymbols(buffer.toString().toCharArray()));
                        i = j;
                        buffer.setLength(0);
                    }
                    else {
                        buffer.append(ch);
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
     * @throws IllegalArgumentException If the given symbols list cannot be compiled down into a MathValue
     */
    public static <L extends List<Either<String, L>>> MathValue parseSymbols(L symbols) throws IllegalArgumentException {
        if (symbols.size() == 2) {
            Optional<String> prefix = symbols.get(0).left().filter(left -> isVariable(left) || left.equals("-"));
            Optional<L> group = symbols.get(1).right();

            if (prefix.isPresent() && group.isPresent())
                return compileFunction(prefix.get(), group.get());
        }

        MathValue value = compileValue(symbols);

        if (value != null)
            return value;

        throw new IllegalArgumentException("Unable to parse compiled symbols from expression: " + symbols);
    }

    /**
     * Compile the given {@link #compileSymbols(char[]) symbols} down into a singular {@link MathValue}, ready for use
     *
     * @return A compiled MathValue instance, or null if not applicable
     * @throws IllegalArgumentException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static <L extends List<Either<String, L>>> MathValue compileValue(L symbols) throws IllegalArgumentException {
        if (symbols.size() == 1)
            return compileSingleValue(symbols.get(0));

        Ternary ternary = compileTernary(symbols);

        if (ternary != null)
            return ternary;

        return compileCalculation(symbols);
    }

    /**
     * Compile a singular-argument {@link MathValue} instance from the given symbols list, if applicable
     *
     * @return A compiled MathValue value, or null if not applicable
     * @throws IllegalArgumentException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static <L extends List<Either<String, L>>> MathValue compileSingleValue(Either<String, L> symbol) throws IllegalArgumentException {
        if (symbol.right().isPresent())
            return new Group(parseSymbols(symbol.right().get()));

        return symbol.left().map(string -> {
            if (string.startsWith("!"))
                return new BooleanNegate(compileSingleValue(Either.left(string.substring(1))));

            if (isNumeric(string))
                return new Constant(Double.parseDouble(string));

            if (isVariable(string)) {
                if (string.startsWith("-"))
                    return getVariableFor(string.substring(1)).orElse(null);

                return getVariableFor(string).orElse(null);
            }

            return null;
        }).orElse(null);
    }

    /**
     * Compile a {@link Calculation} value instance from the given symbols list, if applicable
     *
     * @return A compiled Calculation value, or null if not applicable
     * @throws IllegalArgumentException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static <L extends List<Either<String, L>>> Calculation compileCalculation(L symbols) throws IllegalArgumentException  {
        final int symbolCount = symbols.size();
        int firstOperatorIndex = -1;
        int secondOperatorIndex = -1;

        for (int i = 0; i < symbolCount; i++) {
            final Either<String, L> symbol = symbols.get(i);

            if (symbol.left().filter(MathBuilder::isOperativeSymbol).isPresent()) {
                if (firstOperatorIndex == -1) {
                    firstOperatorIndex = i;
                }
                else {
                    secondOperatorIndex = i;

                    break;
                }
            }
        }

        if (firstOperatorIndex == -1)
            return null;

        Operator firstOperator = getOperatorFor(symbols.get(firstOperatorIndex).left().get());

        if (secondOperatorIndex == -1) {
            MathValue left = parseSymbols((L)symbols.subList(0, firstOperatorIndex));
            MathValue right = parseSymbols((L)symbols.subList(firstOperatorIndex + 1, Mth.clamp(firstOperatorIndex + 3, 0, symbolCount)));

            return new Calculation(firstOperator, left, right);
        }

        Operator secondOperator = getOperatorFor(symbols.get(secondOperatorIndex).left().get());
        MathValue left = parseSymbols((L)symbols.subList(0, firstOperatorIndex));

        if (secondOperator.takesPrecedenceOver(firstOperator))
            return new Calculation(firstOperator, left, parseSymbols((L)symbols.subList(firstOperatorIndex + 1, symbolCount)));

        MathValue right = parseSymbols((L)symbols.subList(firstOperatorIndex + 1, secondOperatorIndex));

        return new Calculation(secondOperator, new Calculation(firstOperator, left, right), parseSymbols((L)symbols.subList(secondOperatorIndex + 1, symbolCount)));
    }

    /**
     * Compile a {@link Ternary} value instance from the given symbols list, if applicable
     *
     * @return A compiled Ternary value, or null if not applicable
     * @throws IllegalArgumentException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static <L extends List<Either<String, L>>> Ternary compileTernary(L symbols) throws IllegalArgumentException  {
        final int symbolCount = symbols.size();

        if (symbolCount < 3)
            return null;

        Supplier<MathValue> condition = null;
        Supplier<MathValue> ifTrue = null;
        int ternaryState = 0;
        int lastColon = -1;

        for (int i = 0; i < symbolCount; i++) {
            final int i2 = i;
            final String string = symbols.get(i).left().orElse(null);

            if ("?".equals(string)) {
                if (condition == null)
                    condition = () -> parseSymbols((L)symbols.subList(0, i2));

                ternaryState++;
            }
            else if (":".equals(string)) {
                if (ternaryState == 1 && ifTrue == null)
                    ifTrue = () -> parseSymbols((L)symbols.subList(0, i2));

                ternaryState--;
                lastColon = i;
            }
        }

        if (ternaryState == 0 && condition != null && ifTrue != null && lastColon < symbolCount - 1)
            return new Ternary(condition.get(), ifTrue.get(), parseSymbols((L)symbols.subList(lastColon + 1, symbolCount)));

        return null;
    }

    /**
     * Compiles a {@link MathValue} for the given symbols list, if applicable.
     * <p>
     * Note that due to parsing flexibility, this method doesn't necessarily generate a {@link MathFunction}, as some calls may be for value-value pairs instead
     *
     * @param name The name of the function or value
     * @param args The symbols list for the value
     * @return A compiled MathValue, or null if not applicable
     * @throws IllegalArgumentException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static <L extends List<Either<String, L>>> MathValue compileFunction(String name, L args) throws IllegalArgumentException {
        if (name.startsWith("!")) {
            if (name.length() == 1)
                return new BooleanNegate(parseSymbols(args));

            return new BooleanNegate(compileFunction(name.substring(1), args));
        }

        if (name.startsWith("-")) {
            if (name.length() == 1)
                return new Negative(parseSymbols(args));

            return new Negative(compileFunction(name.substring(1), args));
        }

        if (!isFunctionRegistered(name))
            return null;

        final List<MathValue> values = new ObjectArrayList<>();
        final L buffer = (L)new ObjectArrayList<Either<String, L>>();

        for (Either<String, L> arg : args) {
            if (arg.left().filter(","::equals).isPresent()) {
                values.add(parseSymbols(buffer));
                buffer.clear();
            }
            else {
                buffer.add(arg);
            }
        }

        if (!buffer.isEmpty())
            values.add(parseSymbols(buffer));

        return buildFunction(name, values.toArray(new MathValue[0]));
    }

    /**
     * @return Whether the given String should be considered an operator or operator-like symbol
     */
    public static boolean isOperativeSymbol(char symbol) {
        return isOperativeSymbol(String.valueOf(symbol));
    }

    /**
     * @return Whether the given String should be considered an operator or operator-like symbol
     */
    public static boolean isOperativeSymbol(Object symbol) {
        return symbol instanceof String st && isOperativeSymbol(st);
    }

    /**
     * @return Whether the given String should be considered an operator or operator-like symbol
     */
    public static boolean isOperativeSymbol(String symbol) {
        return Operator.isOperator(symbol) || OPERATIVE_SYMBOLS.matcher(symbol).matches();
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
    protected static Operator getOperatorFor(String op) throws IllegalArgumentException {
        return Operator.getOperatorFor(op).orElseThrow(() -> new IllegalArgumentException("Unknown operator symbol '" + op + "'"));
    }

    /**
     * Determine if the given string is likely to be a variable of some kind.
     * <p>
     * Functionally this is just a confirmation-by-elimination check, since variables don't really have a defined form
     */
    protected static boolean isVariable(String string) {
        return !isNumeric(string) && !isOperativeSymbol(string);
    }
}
