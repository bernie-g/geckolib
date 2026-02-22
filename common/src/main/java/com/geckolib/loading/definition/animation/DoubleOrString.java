package com.geckolib.loading.definition.animation;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import org.jetbrains.annotations.ApiStatus;

/// A primitive-supporting [Either] for either a `double` or `String` value
@ApiStatus.Internal
public sealed interface DoubleOrString permits DoubleOrString.DoubleValue, DoubleOrString.StringValue {
    /// @return If this instance contains a `double` can safely call [#doubleValue()]
    boolean isDouble();

    /// @return The `double` value for this instance
    /// @throws IllegalStateException If attempting to call for a value that doesn't exist. Use
    /// [#isDouble]
    double doubleValue();

    /// @return The `String` value for this instance
    /// @throws IllegalStateException If attempting to call for a value that doesn't exist. Use
    /// [#isDouble]
    String stringValue();

    /// Parse an DoubleOrString instance from raw .json input via [Gson]
    static JsonDeserializer<DoubleOrString> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            if (!(json instanceof JsonPrimitive primitive))
                throw new JsonParseException("DoubleOrString encountered invalid format, expected either String or double: " + json);

            if (primitive.isString())
                return new StringValue(primitive.getAsString());

            return new DoubleOrString.DoubleValue(primitive.getAsDouble());
        };
    }

    record DoubleValue(double doubleValue) implements DoubleOrString {
        @Override
        public boolean isDouble() {
            return true;
        }

        @Override
        public String stringValue() {
            throw new IllegalStateException("Attempted to retrieve a String value from a double-type DoubleOrString!");
        }
    }

    record StringValue(String stringValue) implements DoubleOrString {
        @Override
        public boolean isDouble() {
            return false;
        }

        @Override
        public double doubleValue() {
            throw new IllegalStateException("Attempted to retrieve a double value from a String-type DoubleOrString!");
        }
    }
}