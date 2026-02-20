package software.bernie.geckolib.loading.definition.animation;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animation.state.AnimationPoint;
import software.bernie.geckolib.cache.animation.Keyframe;
import software.bernie.geckolib.cache.animation.KeyframeStack;
import software.bernie.geckolib.loading.definition.animation.object.KeyframeTriplet;
import software.bernie.geckolib.loading.math.MathParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/// Container class for a single actor animation's bone animation data for a specific transformation type,
/// only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// This object represents either a singular keyframe with no associated timestamp, representing a persistent state keyframe for the entire animation, or a map of keyframes to their timestamps.
/// If the map is present, it should be pre-sorted in ascending order of the timestamp keys
///
/// @param keyframes The keyframes collection for this animation keyframe track
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_actor_animation_1.8.0?view=minecraft-bedrock-stable">Bedrock Actor Animation Spec 1.8.0</a>
@ApiStatus.Internal
public record ActorBoneAnimationEntry(Either<ActorBoneAnimationKeyframe, Double2ObjectArrayMap<ActorBoneAnimationKeyframe>> keyframes) {
    /// @return Whether this entry is a singular keyframe with no timestamp, representing a persistent state keyframe
    public boolean isSingleKeyframe() {
        return this.keyframes.left().isPresent();
    }

    /// @return The length of this animation keyframe track (in seconds)
    public double getAnimationLength() {
        return this.keyframes.map(_ -> 0d, map -> {
            double lastTimestamp = 0;

            for (Double2ObjectMap.Entry<ActorBoneAnimationKeyframe> entry : map.double2ObjectEntrySet()) {
                double keyframeTime = entry.getDoubleKey();

                if (keyframeTime >= lastTimestamp)
                    lastTimestamp = keyframeTime;
            }

            return lastTimestamp;
        });
    }

    /// @return The number of keyframes in this entry
    public int size() {
        return this.keyframes.map(_ -> 1, Double2ObjectArrayMap::size);
    }

    /// Parse an ActorBoneAnimationEntry instance from raw .json input via [Gson]
    public static JsonDeserializer<ActorBoneAnimationEntry> gsonDeserializer() throws JsonParseException {
        return (json, _, context) -> {
            if (json.isJsonPrimitive())
                return new ActorBoneAnimationEntry(Either.left(context.deserialize(json, ActorBoneAnimationKeyframe.class)));

            if (json.isJsonArray()) {
                final JsonArray jsonArray = json.getAsJsonArray();

                if (jsonArray.isEmpty())
                    throw new JsonParseException("ActorBoneAnimationEntry has an empty keyframes list. This is an invalid animation json");

                return new ActorBoneAnimationEntry(Either.left(context.deserialize(jsonArray.get(0), ActorBoneAnimationKeyframe.class)));
            }

            if (json.isJsonObject()) {
                final JsonObject obj = json.getAsJsonObject();

                if (obj.isEmpty())
                    throw new JsonParseException("ActorBoneAnimationEntry has an empty keyframes map. This is an invalid animation json");

                if (obj.has("vector"))
                    return new ActorBoneAnimationEntry(Either.left(context.deserialize(obj, ActorBoneAnimationKeyframe.class)));

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

                        //noinspection ConstantValue
                        if (keyframes.put(timestamp, context.deserialize(entry.getValue(), ActorBoneAnimationKeyframe.class)) != null)
                            GeckoLibConstants.LOGGER.warn("Animation has a duplicate timestamp! '{}'",  timestamp);

                        lastTime = timestamp;
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

                return new ActorBoneAnimationEntry(Either.right(keyframes));
            }

            throw new JsonParseException("ActorBoneAnimationEntry has an unknown keyframe type: " + json);
        };
    }

    /// Bake this `ActorBoneAnimationEntry` instance into the final [KeyframeStack] instance that GeckoLib uses for animating
    public KeyframeStack bake(AnimationPoint.Transform transformType, MathParser mathParser) {
        if (this.keyframes.right().map(Map::isEmpty).orElse(false))
            return KeyframeStack.EMPTY;

        final KeyframeTriplet[] keyframes = bakeKeyframes(transformType, mathParser);
        final Keyframe[] xKeyframes = extractAxisKeyframes(keyframes, KeyframeTriplet::x);
        final Keyframe[] yKeyframes = extractAxisKeyframes(keyframes, KeyframeTriplet::y);
        final Keyframe[] zKeyframes = extractAxisKeyframes(keyframes, KeyframeTriplet::z);

        if (keyframes.length == 0)
            return KeyframeStack.EMPTY;

        addEasingArgs(xKeyframes, mathParser);
        addEasingArgs(yKeyframes, mathParser);
        addEasingArgs(zKeyframes, mathParser);

        return new KeyframeStack(xKeyframes, yKeyframes, zKeyframes);
    }

    /// Bake a complete [KeyframeTriplet] array for this animation entry, given a specific transformation type
    private KeyframeTriplet[] bakeKeyframes(AnimationPoint.Transform transformType, MathParser mathParser) {
        return this.keyframes.map(keyframe -> keyframe.bake(0, transformType, true, null, mathParser), map -> {
            final List<KeyframeTriplet> triplets = new ObjectArrayList<>(map.size());
            final boolean forceLinearInterpolation = map.size() == 1;
            KeyframeTriplet lastTriplet = null;

            for (Double2ObjectMap.Entry<ActorBoneAnimationKeyframe> entry : map.double2ObjectEntrySet()) {
                KeyframeTriplet[] keyframeTriplets = entry.getValue().bake(entry.getDoubleKey(), transformType, forceLinearInterpolation, lastTriplet, mathParser);

                if (keyframeTriplets.length > 0) {
                    triplets.addAll(ObjectArrayList.of(keyframeTriplets));

                    lastTriplet = triplets.getLast();
                }
            }

            return triplets.toArray(new KeyframeTriplet[0]);
        });
    }

    /// Extract a [Keyframe] array for a given axis from a [KeyframeTriplet] array
    private Keyframe[] extractAxisKeyframes(KeyframeTriplet[] triplets, Function<KeyframeTriplet, Keyframe> extractor) {
        final Keyframe[] keyframes = new Keyframe[triplets.length];

        for (int i = 0; i < triplets.length; i++) {
            keyframes[i] = extractor.apply(triplets[i]);
        }

        return keyframes;
    }

    /// Allow for keyframe easings to modify the keyframes (usually to set [Keyframe#easingArgs]) as needed for animation interpolation
    private void addEasingArgs(Keyframe[] keyframes, MathParser mathParser) {
        for (int i = 0; i < keyframes.length; i++) {
            keyframes[i].easingType().modifyKeyframes(keyframes, i, mathParser);
        }
    }
}
