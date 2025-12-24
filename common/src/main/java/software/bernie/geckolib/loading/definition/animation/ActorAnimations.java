package software.bernie.geckolib.loading.definition.animation;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.util.JsonUtil;

import java.util.Map;

/**
 * Container class for a full animation file definition, only used for intermediary steps between .json deserialization and GeckoLib object creation
 * <p>
 * This is the root-level object for a fully processed .animation file
 *
 * @param formatVersion The format specification version of this animations instance
 * @param animations The map of actor animations to their defined names
 * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_actor_animation_1.8.0?view=minecraft-bedrock-stable">Bedrock Actor Animation Spec 1.8.0</a>
 */
@ApiStatus.Internal
public record ActorAnimations(String formatVersion, Map<String, ActorAnimation> animations) {
    /**
     * Publicly accessible GSON parser for GeckoLib animation .json files
     */
    public static final Gson GSON = new GsonBuilder().setStrictness(Strictness.LENIENT)
            .registerTypeAdapter(ActorAnimations.class, gsonDeserializer())
            .registerTypeAdapter(ActorAnimation.class, ActorAnimations.gsonDeserializer())
            .registerTypeAdapter(ActorAnimationParticleEffect.class, ActorAnimationParticleEffect.gsonDeserializer())
            .registerTypeAdapter(ActorAnimationSoundEffect.class, ActorAnimationSoundEffect.gsonDeserializer())
            .registerTypeAdapter(ActorBoneAnimation.class, ActorBoneAnimation.gsonDeserializer())
            .registerTypeAdapter(ActorBoneAnimationEntry.class, ActorBoneAnimationEntry.gsonDeserializer())
            .registerTypeAdapter(ActorBoneAnimationKeyframe.class, ActorBoneAnimationKeyframe.gsonDeserializer())
            .registerTypeAdapter(ActorBoneAnimationKeyframeValues.class, ActorBoneAnimationKeyframeValues.gsonDeserializer())
            .registerTypeAdapter(DoubleOrString.class, DoubleOrString.gsonDeserializer())
            .create();

    /**
     * Parse an ActorAnimations instance from raw .json input via {@link Gson}
     */
    public static JsonDeserializer<ActorAnimations> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final String formatVersion = GsonHelper.getAsString(obj, "format_version");
            final Map<String, ActorAnimation> animations = JsonUtil.jsonObjToMap(GsonHelper.getAsJsonObject(obj, "animations"), context, ActorAnimation.class);

            if (animations == null)
                throw new JsonParseException("Animations map missing from animations json!");

            if (animations.isEmpty())
                GeckoLibConstants.LOGGER.warn("No animation definitions found in animation file!");

            return new ActorAnimations(formatVersion, animations);
        };
    }


}