package software.bernie.geckolib.loading.definition.animation;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/**
 * Container class for a single actor animation's bone animation data,
 * only used for intermediary steps between .json deserialization and GeckoLib object creation
 * <p>
 * Note that the name for this bone isn't contained here, but instead in the {@link ActorAnimation#boneAnimations()} map
 *
 * @param relativeTo An optional object marking this bone animation as being relative to the root object rather than the bone's parent. Not used by GeckoLib
 * @param positionKeyframes The position keyframes for this bone animation, if present
 * @param rotationKeyframes The rotation keyframes for this bone animation, if present
 * @param scaleKeyframes The scale keyframes for this bone animation, if present
 * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_actor_animation_1.8.0?view=minecraft-bedrock-stable">Bedrock Actor Animation Spec 1.8.0</a>
 */
@ApiStatus.Internal
public record ActorBoneAnimation(@Nullable String relativeTo,
                                 @Nullable ActorBoneAnimationEntry positionKeyframes, @Nullable ActorBoneAnimationEntry rotationKeyframes, @Nullable ActorBoneAnimationEntry scaleKeyframes) {
    /**
     * Parse an ActorBoneAnimation instance from raw .json input via {@link Gson}
     */
    public static JsonDeserializer<ActorBoneAnimation> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final String relativeTo = GsonHelper.getAsString(obj, "relative_to", null);
            final ActorBoneAnimationEntry positionKeyframes = GsonHelper.getAsObject(obj, "position", null, context, ActorBoneAnimationEntry.class);
            final ActorBoneAnimationEntry rotationKeyframes = GsonHelper.getAsObject(obj, "rotation", null, context, ActorBoneAnimationEntry.class);
            final ActorBoneAnimationEntry scaleKeyframes = GsonHelper.getAsObject(obj, "scale", null, context, ActorBoneAnimationEntry.class);

            return new ActorBoneAnimation(relativeTo, positionKeyframes, rotationKeyframes, scaleKeyframes);
        };
    }
}
