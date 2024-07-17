package software.bernie.geckolib.loading.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
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
import software.bernie.geckolib.util.CompoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
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
    private static final Pattern STATEMENT_DELIMITER = Pattern.compile(";");
    private static final Pattern NUMERIC = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern VALID_DOUBLE = Pattern.compile("[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\d+)(\\.)?((\\d+)?)([eE][+-]?(\\d+))?)|(\\.(\\d+)([eE][+-]?(\\d+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\d+)))[fFdD]?))[\\x00-\\x20]*");
    private static final Pattern OPERATIVE_SYMBOLS = Pattern.compile("[?:]");
    private static final String MOLANG_RETURN = "return ";
    private static final Map<String, MathFunction.Factory<?>> FUNCTION_FACTORIES = Util.make(new ConcurrentHashMap<>(18), map -> {
        map.put("math.abs", AbsFunction::new);
        map.put("math.acos", ACosFunction::new);
        map.put("math.asin", ASinFunction::new);
        map.put("math.atan", ATanFunction::new);
        map.put("math.atan2", ATan2Function::new);
        map.put("math.cos", CosFunction::new);
        map.put("math.exp", ExpFunction::new);
        map.put("math.ln", LogFunction::new);
        map.put("math.mod", ModFunction::new);
        map.put("math.pow", PowFunction::new);
        map.put("math.sin", SinFunction::new);
        map.put("math.sqrt", SqrtFunction::new);
        map.put("math.clamp", ClampFunction::new);
        map.put("math.max", MaxFunction::new);
        map.put("math.min", MinFunction::new);
        map.put("math.die_roll", DieRollFunction::new);
        map.put("math.die_roll_integer", DieRollIntegerFunction::new);
        map.put("math.random", RandomFunction::new);
        map.put("math.random_integer", RandomIntegerFunction::new);
        map.put("math.ceil", CeilFunction::new);
        map.put("math.floor", FloorFunction::new);
        map.put("math.hermite_blend", HermiteBlendFunction::new);
        map.put("math.lerp", LerpFunction::new);
        map.put("math.lerprotate", LerpRotFunction::new);
        map.put("math.round", RoundFunction::new);
        map.put("math.trunc", TruncateFunction::new);
        map.put("math.pi", PiFunction::new);
        map.put("math.to_deg", ToDegFunction::new);
        map.put("math.to_rad", ToRadFunction::new);
    });
    private static final Map<String, Variable> VARIABLES = Util.make(new ConcurrentHashMap<>(), map -> {
        map.put("PI", new Variable("PI", Math.PI));
        map.put("E", new Variable("E", Math.E));
        map.put(MolangQueries.ANIM_TIME, new Variable(MolangQueries.ANIM_TIME, 0));
        map.put(MolangQueries.LIFE_TIME, new Variable(MolangQueries.LIFE_TIME, 0));
        map.put(MolangQueries.ACTOR_COUNT, new Variable(MolangQueries.ACTOR_COUNT, 0));
        map.put(MolangQueries.TIME_OF_DAY, new Variable(MolangQueries.TIME_OF_DAY, 0));
        map.put(MolangQueries.MOON_PHASE, new Variable(MolangQueries.MOON_PHASE, 0));
        map.put(MolangQueries.DISTANCE_FROM_CAMERA, new Variable(MolangQueries.DISTANCE_FROM_CAMERA, 0));
        map.put(MolangQueries.IS_ON_GROUND, new Variable(MolangQueries.IS_ON_GROUND, 0));
        map.put(MolangQueries.IS_IN_WATER, new Variable(MolangQueries.IS_IN_WATER, 0));
        map.put(MolangQueries.IS_IN_WATER_OR_RAIN, new Variable(MolangQueries.IS_IN_WATER_OR_RAIN, 0));
        map.put(MolangQueries.HEALTH, new Variable(MolangQueries.HEALTH, 0));
        map.put(MolangQueries.MAX_HEALTH, new Variable(MolangQueries.MAX_HEALTH, 0));
        map.put(MolangQueries.IS_ON_FIRE, new Variable(MolangQueries.IS_ON_FIRE, 0));
        map.put(MolangQueries.GROUND_SPEED, new Variable(MolangQueries.GROUND_SPEED, 0));
        map.put(MolangQueries.YAW_SPEED, new Variable(MolangQueries.YAW_SPEED, 0));
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
    public static Variable getVariableFor(String name) {
        return VARIABLES.computeIfAbsent(name, key -> new Variable(key, 0));
    }

    /**
     * Set the value of a {@link Variable} in the Molang parser, creating a new Variable instance as needed
     *
     * @param name The name of the variable - this should match the name in the expression string
     * @param value The new value to set the Variable to
     */
    public static void setVariable(String name, DoubleSupplier value) {
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

            if (STATEMENT_DELIMITER.matcher(expression).matches())
                expression = expression.substring(0, expression.indexOf(";"));
        }
        else if (STATEMENT_DELIMITER.matcher(expression).matches()) {
            final String[] subExpressions = expression.split(";");
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
     * Compile a collection of 'symbols' from the given char array representing the expression split into individual characters
     *
     * @return A list of either string symbols, or a group of pre-compiled arguments of a grouping
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

        for (int i = 0; i < chars.length; i++) {
            final char ch = chars[i];
            final boolean isMultiCharOperator = i > 0 && isOperativeSymbol(chars[i - 1] + "" + ch);

            if (isOperativeSymbol(ch) || isMultiCharOperator || ch == ',') {
                if (ch == '-' && buffer.isEmpty()) {
                    String lastSymbol;

                    if (symbols.isEmpty() || isOperativeSymbol(lastSymbol = symbols.getLast().left().orElse("")) || ",".equals(lastSymbol)) {
                        buffer.append(ch);

                        continue;
                    }
                }

                if (isMultiCharOperator) {
                    if (!buffer.isEmpty())
                        symbols.add(Either.left(buffer.substring(0, buffer.length() - 1)));

                    symbols.add(Either.left(chars[i - 1] + "" + ch));
                }
                else {
                    if (!buffer.isEmpty())
                        symbols.add(Either.left(buffer.toString()));

                    symbols.add(Either.left(String.valueOf(ch)));
                }

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
            Optional<String> prefix = symbols.get(0).left().filter(left -> isQueryOrFunctionName(left) || left.equals("-"));
            Optional<List<MathValue>> group = symbols.get(1).right();

            if (prefix.isPresent() && group.isPresent())
                return compileFunction(prefix.get(), group.get());
        }

        MathValue value = compileValue(symbols);

        if (value != null)
            return value;

        throw new CompoundException("Unable to parse compiled symbols from expression: " + symbols);
    }

    /**
     * Compile the given {@link #compileSymbols(char[]) symbols} down into a singular {@link MathValue}, ready for use
     *
     * @return A compiled MathValue instance, or null if not applicable
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static MathValue compileValue(List<Either<String, List<MathValue>>> symbols) throws CompoundException {
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
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static MathValue compileSingleValue(Either<String, List<MathValue>> symbol) throws CompoundException {
        if (symbol.right().isPresent())
            return new Group(symbol.right().get().get(0));

        return symbol.left().map(string -> {
            if (string.startsWith("!"))
                return new BooleanNegate(compileSingleValue(Either.left(string.substring(1))));

            if (isNumeric(string))
                return new Constant(Double.parseDouble(string));

            if (isQueryOrFunctionName(string)) {
                if (string.startsWith("-"))
                    return new Negative(getVariableFor(string.substring(1)));

                return getVariableFor(string);
            }

            return null;
        }).orElse(null);
    }

    /**
     * Compile a MathValue value instance from the given symbols list, if applicable
     *
     * @return A compiled {@link Calculation} or {@link VariableAssignment} value, or null if not applicable
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static MathValue compileCalculation(List<Either<String, List<MathValue>>> symbols) throws CompoundException  {
        final int symbolCount = symbols.size();
        int operatorIndex = -1;
        Operator lastOperator = null;

        for (int i = 0; i < symbolCount; i++) {
            Operator operator = symbols.get(i).left()
                    .filter(MathParser::isOperativeSymbol)
                    .map(MathParser::getOperatorFor).orElse(null);

            if (operator == null)
                continue;

            if (operator == Operator.ASSIGN_VARIABLE) {
                if (!(parseSymbols(symbols.subList(0, i)) instanceof Variable variable))
                    throw new CompoundException("Attempted to assign a value to a non-variable");

                return new VariableAssignment(variable, parseSymbols(symbols.subList(i + 1, symbolCount)));
            }

            if (lastOperator == null || !operator.takesPrecedenceOver(lastOperator)) {
                operatorIndex = i;
                lastOperator = operator;
            }
            else {
                break;
            }
        }

        return lastOperator == null ? null : new Calculation(lastOperator, parseSymbols(symbols.subList(0, operatorIndex)), parseSymbols(symbols.subList(operatorIndex + 1, symbolCount)));
    }

    /**
     * Compile a {@link Ternary} value instance from the given symbols list, if applicable
     *
     * @return A compiled Ternary value, or null if not applicable
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static Ternary compileTernary(List<Either<String, List<MathValue>>> symbols) throws CompoundException  {
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
                    condition = () -> parseSymbols(symbols.subList(0, i2));

                ternaryState++;
            }
            else if (":".equals(string)) {
                if (ternaryState == 1 && ifTrue == null)
                    ifTrue = () -> parseSymbols(symbols.subList(0, i2));

                ternaryState--;
                lastColon = i;
            }
        }

        if (ternaryState == 0 && condition != null && ifTrue != null && lastColon < symbolCount - 1)
            return new Ternary(condition.get(), ifTrue.get(), parseSymbols(symbols.subList(lastColon + 1, symbolCount)));

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
     * @throws CompoundException If there is a parsing failure for any of the contents of the symbols
     */
    @Nullable
    protected static MathValue compileFunction(String name, List<MathValue> args) throws CompoundException {
        if (name.startsWith("!")) {
            if (name.length() == 1)
                return new BooleanNegate(args.get(0));

            return new BooleanNegate(compileFunction(name.substring(1), args));
        }

        if (name.startsWith("-")) {
            if (name.length() == 1)
                return new Negative(args.get(0));

            return new Negative(compileFunction(name.substring(1), args));
        }

        if (!isFunctionRegistered(name))
            return null;

        return buildFunction(name, args.toArray(new MathValue[0]));
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
    public static boolean isOperativeSymbol(@NotNull String symbol) {
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
    protected static Operator getOperatorFor(String op) throws CompoundException {
        return Operator.getOperatorFor(op).orElseThrow(() -> new CompoundException("Unknown operator symbol '" + op + "'"));
    }

    /**
     * Determine if the given string is likely to be a variable/function of some kind.
     * <p>
     * Functionally this is just a confirmation-by-elimination check, since names don't really have a defined form
     */
    protected static boolean isQueryOrFunctionName(String string) {
        return !isNumeric(string) && !isOperativeSymbol(string);
    }
}
