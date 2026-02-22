package com.geckolib.loading.definition.animation;

import com.google.gson.*;
import org.jetbrains.annotations.ApiStatus;

/// Container class for a single actor animation's bone animation keyframe,
/// only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// @param xValue The x-axis value for this keyframe, either a constant `double`, or a [String] representing a value or mathematical expression
/// @param yValue The y-axis value for this keyframe, either a constant `double`, or a [String] representing a value or mathematical expression
/// @param zValue The z-axis value for this keyframe, either a constant `double`, or a [String] representing a value or mathematical expression
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_actor_animation_1.8.0?view=minecraft-bedrock-stable">Bedrock Actor Animation Spec 1.8.0</a>
@ApiStatus.Internal
public record ActorBoneAnimationKeyframeValues(DoubleOrString xValue, DoubleOrString yValue, DoubleOrString zValue) {
    /// Parse an ActorBoneAnimationKeyframeValues instance from raw .json input via [Gson]
    public static JsonDeserializer<ActorBoneAnimationKeyframeValues> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            if (json instanceof JsonArray array) {
                if (array.isEmpty())
                    throw new JsonParseException("ActorBoneAnimationKeyframeValues has an empty keyframe, must contain some value!");

                if (array.size() == 1) {
                    final DoubleOrString value = context.deserialize(array.get(0), DoubleOrString.class);

                    return new ActorBoneAnimationKeyframeValues(value, value, value);
                }

                if (array.size() == 3) {
                    final DoubleOrString xValue = context.deserialize(array.get(0), DoubleOrString.class);
                    final DoubleOrString yValue = context.deserialize(array.get(1), DoubleOrString.class);
                    final DoubleOrString zValue = context.deserialize(array.get(2), DoubleOrString.class);

                    return new ActorBoneAnimationKeyframeValues(xValue, yValue, zValue);
                }

                throw new JsonParseException("ActorBoneAnimationKeyframeValues has " + array.size() + " values, must be either 1 or 3: " + array);
            }

            final DoubleOrString value = context.deserialize(json, DoubleOrString.class);

            return new ActorBoneAnimationKeyframeValues(value, value, value);
        };
    }
}
