package software.bernie.geckolib.loading.definition.animation;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.util.JsonUtil;

/// Container class for a single actor animation's bone animation keyframe,
/// only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// Note that the timestamp for this keyframe is not contained here, and is instead held by the [ActorBoneAnimationEntry#keyframes()] map, if present
///
/// @param values The actual value for this keyframe. Can technically be null if only [#preKeyframe] or [#postKeyframe] are present
/// @param interpolationType An optional interpolation/easing type declaration
/// @param easingArgs An optional array of values to be used by the interpolation type for further customization
/// @param preKeyframe An optional keyframe to insert directly prior to this keyframe, usually for interpolation purposes
/// @param postKeyframe An optional keyframe to insert directly after this keyframe, usually for interpolation purposes
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_actor_animation_1.8.0?view=minecraft-bedrock-stable">Bedrock Actor Animation Spec 1.8.0</a>
@ApiStatus.Internal
public record ActorBoneAnimationKeyframe(@Nullable ActorBoneAnimationKeyframeValues values, @Nullable String interpolationType, DoubleOrString @Nullable [] easingArgs,
                                         @Nullable ActorBoneAnimationKeyframe preKeyframe, @Nullable ActorBoneAnimationKeyframe postKeyframe) {
    /// Parse an ActorBoneAnimationKeyframe instance from raw .json input via [Gson]
    public static JsonDeserializer<ActorBoneAnimationKeyframe> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            if (json.isJsonObject()) {
                final JsonObject obj = json.getAsJsonObject();
                final ActorBoneAnimationKeyframeValues values = GsonHelper.getAsObject(obj, "vector", null, context, ActorBoneAnimationKeyframeValues.class);
                final String interpolationType = obj.has("lerp_mode") ? obj.get("lerp_mode").getAsString() : obj.has("easing") ? obj.get("easing").getAsString() : null;
                final DoubleOrString[] easingArgs = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "easingArgs", null), context, DoubleOrString.class);
                final ActorBoneAnimationKeyframe preKeyframe = GsonHelper.getAsObject(obj, "pre", null, context, ActorBoneAnimationKeyframe.class);
                final ActorBoneAnimationKeyframe postKeyframe = GsonHelper.getAsObject(obj, "post", null, context, ActorBoneAnimationKeyframe.class);

                return new ActorBoneAnimationKeyframe(values, interpolationType, easingArgs, preKeyframe, postKeyframe);
            }

            if (json.isJsonPrimitive() || json.isJsonArray())
                return new ActorBoneAnimationKeyframe(context.deserialize(json, ActorBoneAnimationKeyframeValues.class), null, null, null, null);

            throw new JsonParseException("ActorBoneAnimationKeyframe has invalid format, expected either String, Array or JsonObject: " + json);
        };
    }
}
