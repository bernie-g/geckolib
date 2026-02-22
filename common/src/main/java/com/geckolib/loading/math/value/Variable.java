package com.geckolib.loading.math.value;

import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.loading.math.MathValue;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.ToDoubleFunction;

/// [MathValue] value supplier
///
/// **Contract:**
///
/// Returns the currently stored value, which may be modified at any given time via [#set]. Values may be lazily evaluated to eliminate wasteful usage
public record Variable(String name, AtomicReference<ToDoubleFunction<ControllerState>> value) implements MathValue {
    public Variable(String name, double value) {
        this(name, animationState -> value);
    }

    public Variable(String name, ToDoubleFunction<ControllerState> value) {
        this(name, new AtomicReference<>(value));
    }

    @Override
    public double get(@Nullable ControllerState controllerState) {
        if (controllerState == null)
            return 0;

        try {
            return this.value.get().applyAsDouble(controllerState);
        }
        catch (Exception ex) {
            GeckoLibConstants.LOGGER.error("Attempted to use Molang variable for incompatible animatable type ({}). An animation json needs to be fixed", this.name);
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();

            return 0;
        }
    }

    public void set(final double value) {
        this.value.set(controllerState -> value);
    }

    public void set(final ToDoubleFunction<ControllerState> value) {
        this.value.set(value);
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return Set.of(this);
    }

    @Override
    public String toString() {
        return "variable(" + this.name + ")";
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
