package com.geckolib.loading.math.function.generic;

import org.jspecify.annotations.Nullable;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;
import com.geckolib.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the remainder value of the input value when modulo'd by the modulus value
public final class ModFunction extends MathFunction {
    private final MathValue value;
    private final MathValue modulus;

    public ModFunction(MathValue... values) {
        super(values);

        this.value = values[0];
        this.modulus = values[1];
    }

    @Override
    public String getName() {
        return "math.mod";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        final double modulo = this.modulus.get(controllerState);

        return modulo == 0 ? 0 : this.value.get(controllerState) % modulo;
    }

    @Override
    public void validate(MathValue... inputs) throws IllegalArgumentException {
        super.validate(inputs);

        if (!this.modulus.isMutable() && this.modulus.get(null) == 0)
            throw new IllegalArgumentException(String.format("Modulo function %s uses an invalid modulus!", this));
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.value, this.modulus};
    }
}
