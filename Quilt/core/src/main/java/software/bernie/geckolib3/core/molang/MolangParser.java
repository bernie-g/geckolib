package software.bernie.geckolib3.core.molang;

import com.eliotlash.mclib.math.Constant;
import com.eliotlash.mclib.math.IValue;
import com.eliotlash.mclib.math.MathBuilder;
import com.eliotlash.mclib.math.Variable;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib3.core.molang.expressions.MolangAssignment;
import software.bernie.geckolib3.core.molang.expressions.MolangExpression;
import software.bernie.geckolib3.core.molang.expressions.MolangMultiStatement;
import software.bernie.geckolib3.core.molang.expressions.MolangValue;
import software.bernie.geckolib3.core.molang.functions.CosDegrees;
import software.bernie.geckolib3.core.molang.functions.SinDegrees;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;

/**
 * MoLang parser
 * This bad boy parses Molang expressions
 * https://bedrock.dev/docs/1.19.0.0/1.19.30.23/Molang#Math%20Functions
 */
public class MolangParser extends MathBuilder {
	// Replace base variables map
	public static final Map<String, LazyVariable> VARIABLES = new Object2ObjectOpenHashMap<>();
	public static final MolangExpression ZERO = new MolangValue(null, new Constant(0));
	public static final MolangExpression ONE = new MolangValue(null, new Constant(1));
	public static final String RETURN = "return ";

	public MolangParser() {
		super();

		// Remap functions to be intact with Molang specification
		doCoreRemaps();
		registerAdditionalVariables();
	}

	private void doCoreRemaps() {
		// Replace radian based sin and cos with degree-based functions
		this.functions.put("cos", CosDegrees.class);
		this.functions.put("sin", SinDegrees.class);

		remap("abs", "math.abs");
		remap("acos", "math.acos");
		remap("asin", "math.asin");
		remap("atan", "math.atan");
		remap("atan2", "math.atan2");
		remap("ceil", "math.ceil");
		remap("clamp", "math.clamp");
		remap("cos", "math.cos");
		remap("die_roll", "math.die_roll");
		remap("die_roll_integer", "math.die_roll_integer");
		remap("exp", "math.exp");
		remap("floor", "math.floor");
		remap("hermite_blend", "math.hermite_blend");
		remap("lerp", "math.lerp");
		remap("lerprotate", "math.lerprotate");
		remap("ln", "math.ln");
		remap("max", "math.max");
		remap("min", "math.min");
		remap("mod", "math.mod");
		remap("pi", "math.pi");
		remap("pow", "math.pow");
		remap("random", "math.random");
		remap("random_integer", "math.random_integer");
		remap("round", "math.round");
		remap("sin", "math.sin");
		remap("sqrt", "math.sqrt");
		remap("trunc", "math.trunc");
	}

	private void registerAdditionalVariables() {
		register(new LazyVariable("query.anim_time", 0));
		register(new LazyVariable("query.actor_count", 0));
		register(new LazyVariable("query.health", 0));
		register(new LazyVariable("query.max_health", 0));
		register(new LazyVariable("query.distance_from_camera", 0));
		register(new LazyVariable("query.yaw_speed", 0));
		register(new LazyVariable("query.is_in_water_or_rain", 0));
		register(new LazyVariable("query.is_in_water", 0));
		register(new LazyVariable("query.is_on_ground", 0));
		register(new LazyVariable("query.time_of_day", 0));
		register(new LazyVariable("query.is_on_fire", 0));
		register(new LazyVariable("query.ground_speed", 0));
	}

	@Override
	public void register(Variable variable) {
		if (!(variable instanceof LazyVariable))
			variable = LazyVariable.from(variable);

		VARIABLES.put(variable.getName(), (LazyVariable)variable);
	}

	/**
	 * Remap function names
	 */
	public void remap(String old, String newName) {
		this.functions.put(newName, this.functions.remove(old));
	}

	/**
	 * Deprecated, use {@link MolangParser#setValue(String, DoubleSupplier)}
	 */
	@Deprecated(forRemoval = true)
	public void setValue(String name, double value) {
		setValue(name, () -> value);
	}

	public void setValue(String name, DoubleSupplier value) {
		LazyVariable variable = getVariable(name);

		if (variable != null)
			variable.set(value);
	}

	@Override
	protected LazyVariable getVariable(String name) {
		return VARIABLES.computeIfAbsent(name, key -> new LazyVariable(key, 0));
	}

	public LazyVariable getVariable(String name, MolangMultiStatement currentStatement) {
		LazyVariable variable;

		if (currentStatement != null) {
			variable = currentStatement.locals.get(name);

			if (variable != null)
				return variable;
		}

		return getVariable(name);
	}

	public MolangExpression parseJson(JsonElement element) throws MolangException {
		if (!element.isJsonPrimitive())
			return ZERO;

		JsonPrimitive primitive = element.getAsJsonPrimitive();

		if (primitive.isNumber())
			return new MolangValue(this, new Constant(primitive.getAsDouble()));

		if (primitive.isString()) {
			String string = primitive.getAsString();

			try {
				return new MolangValue(this, new Constant(Double.parseDouble(string)));
			}
			catch (NumberFormatException ex) {
				return parseExpression(string);
			}
		}

		return ZERO;
	}

	/**
	 * Parse a molang expression
	 */
	public MolangExpression parseExpression(String expression) throws MolangException {
		MolangMultiStatement result = null;

		for (String split : expression.toLowerCase().trim().split(";")) {
			String trimmed = split.trim();

			if (!trimmed.isEmpty()) {
				if (result == null)
					result = new MolangMultiStatement(this);

				result.expressions.add(parseOneLine(trimmed, result));
			}
		}

		if (result == null)
			throw new MolangException("Molang expression cannot be blank!");

		return result;
	}

	/**
	 * Parse a single Molang statement
	 */
	protected MolangExpression parseOneLine(String expression, MolangMultiStatement currentStatement) throws MolangException {
		if (expression.startsWith(RETURN)) {
			try {
				return new MolangValue(this, parse(expression.substring(RETURN.length()))).addReturn();
			}
			catch (Exception e) {
				throw new MolangException("Couldn't parse return '" + expression + "' expression!");
			}
		}

		try {
			List<Object> symbols = breakdownChars(this.breakdown(expression));

			/* Assignment it is */
			if (symbols.size() >= 3 && symbols.get(0) instanceof String && isVariable(symbols.get(0)) && symbols.get(1).equals("=")) {
				String name = (String)symbols.get(0);
				symbols = symbols.subList(2, symbols.size());
				LazyVariable variable;

				if (!VARIABLES.containsKey(name) && !currentStatement.locals.containsKey(name)) {
					currentStatement.locals.put(name, (variable = new LazyVariable(name, 0)));
				}
				else {
					variable = getVariable(name, currentStatement);
				}

				return new MolangAssignment(this, variable, parseSymbolsMolang(symbols));
			}

			return new MolangValue(this, parseSymbolsMolang(symbols));
		}
		catch (Exception e) {
			throw new MolangException("Couldn't parse '" + expression + "' expression!");
		}
	}

	/**
	 * Wrapper around {@link #parseSymbols(List)} to throw {@link MolangException}
	 */
	private IValue parseSymbolsMolang(List<Object> symbols) throws MolangException {
		try {
			return this.parseSymbols(symbols);
		}
		catch (Exception e) {
			e.printStackTrace();

			throw new MolangException("Couldn't parse an expression!");
		}
	}

	/**
	 * Extend this method to allow {@link #breakdownChars(String[])} to capture "="
	 * as an operator, so it was easier to parse assignment statements
	 */
	@Override
	protected boolean isOperator(String s) {
		return super.isOperator(s) || s.equals("=");
	}
}
