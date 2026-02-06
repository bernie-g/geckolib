package software.bernie.geckolib.loading.definition.animation;

import com.google.gson.*;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;

import java.util.Arrays;
import java.util.Map;

/// Container class for a single actor animation's bone animation data for a specific transformation type,
/// only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// This object represents a three-way 'Either' implementation, reduced to nullable values to reduce code footprint
///
/// @param soleKeyframe A singular keyframe with no associated timestamp, representing a persistent state keyframe for the entire animation. `null` if [#keyframes] is present
/// @param keyframes The map of keyframes to their timestamps. `null` if [#soleKeyframe] is present
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_actor_animation_1.8.0?view=minecraft-bedrock-stable">Bedrock Actor Animation Spec 1.8.0</a>
@ApiStatus.Internal
public record ActorBoneAnimationEntry(@Nullable ActorBoneAnimationKeyframe soleKeyframe, @Nullable Double2ObjectArrayMap<ActorBoneAnimationKeyframe> keyframes) {
    /// @return Whether this entry is a singular keyframe with no timestamp, representing a persistent state keyframe
    public boolean isSingleKeyframe() {
        return this.soleKeyframe != null;
    }

    /// @return The number of keyframes in this entry
    public int size() {
        if (isSingleKeyframe())
            return 1;

        if (this.keyframes != null)
            return this.keyframes.size();

        throw new IllegalStateException("ActorBoneAnimationEntry has neither a singular keyframe or a keyframe map. This should never happen!");
    }

    /// Map the contents of this bone animation entry to a singular type result, regardless of contents
    public <T> T mapBoth(Function<ActorBoneAnimationKeyframe, T> soleKeyframeFunction, Function<Double2ObjectArrayMap<ActorBoneAnimationKeyframe>, T> keyframeListFunction) {
        if (isSingleKeyframe())
            return  soleKeyframeFunction.apply(this.soleKeyframe);

        if (this.keyframes != null)
            return keyframeListFunction.apply(this.keyframes);

        throw new IllegalStateException("ActorBoneAnimationEntry has neither a singular keyframe or a keyframe map. This should never happen!");
    }

    /// Parse an ActorBoneAnimationEntry instance from raw .json input via [Gson]
    public static JsonDeserializer<ActorBoneAnimationEntry> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            if (json.isJsonObject()) {
                final JsonObject obj = json.getAsJsonObject();

                if (obj.has("vector"))
                    return new ActorBoneAnimationEntry(context.deserialize(obj, ActorBoneAnimationKeyframe.class), null);

                Double2ObjectArrayMap<ActorBoneAnimationKeyframe> keyframes = new Double2ObjectArrayMap<>(obj.size());
                boolean needsSort = false;
                double lastTime = 0;

                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    try {
                        double timestamp = Double.parseDouble(entry.getKey());

                        if (timestamp < lastTime) {
                            GeckoLibConstants.LOGGER.warn("Animation timestamp {} is out of order! Previous timestamp: {}",  timestamp, lastTime);

                            needsSort = true;
                        }

                        lastTime = timestamp;
                        keyframes.put(timestamp, context.deserialize(entry.getValue(), ActorBoneAnimationKeyframe.class));
                    }
                    catch (NumberFormatException ex) {
                        throw new JsonParseException("Invalid timestamp, must be a numerical value: " + entry);
                    }
                }

                if (needsSort) {
                    Double2ObjectArrayMap<ActorBoneAnimationKeyframe> sortedKeyframes = new Double2ObjectArrayMap<>(keyframes.size());
                    double[] timestamps = sortedKeyframes.keySet().toDoubleArray();

                    Arrays.sort(timestamps);

                    for (double timestamp : timestamps) {
                        sortedKeyframes.put(timestamp, keyframes.get(timestamp));
                    }

                    keyframes = sortedKeyframes;
                }

                return new ActorBoneAnimationEntry(null, keyframes);
            }

            if (json.isJsonPrimitive())
                return new ActorBoneAnimationEntry(context.deserialize(json, ActorBoneAnimationKeyframe.class), null);

            if (json.isJsonArray()) {
                final JsonArray jsonArray = json.getAsJsonArray();

                if (jsonArray.isEmpty())
                    throw new JsonParseException("ActorBoneAnimationEntry has an empty keyframes list. This is an invalid animation json");

                return new ActorBoneAnimationEntry(context.deserialize(jsonArray.get(0), ActorBoneAnimationKeyframe.class), null);
            }

            throw new JsonParseException("ActorBoneAnimationEntry has an unknown keyframe type: " + json);
        };
    }
}
