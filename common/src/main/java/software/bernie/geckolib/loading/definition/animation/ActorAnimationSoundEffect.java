package software.bernie.geckolib.loading.definition.animation;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.animation.keyframeevent.SoundKeyframeData;
import software.bernie.geckolib.loading.definition.geometry.GeometryLocator;
import software.bernie.geckolib.util.JsonUtil;

/// Container class for a single actor animation's sound effects track keyframe marker,
/// only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// Note that the timestamp for this effect isn't contained here, but instead in the [ActorAnimation#soundEffects()] map
///
/// The Bedrock specification does not explicitly support anything other than the effect string, however, Blockbench itself supports
/// the other parameters, and so GeckoLib does too
///
/// @param effect The name of the effect; typically the id of the sound, but can be anything
/// @param locator An optional [GeometryLocator] name to position this effect at
/// @param preEffectScript An optional Molang expression to run immediately prior to this effect being called. Not used by GeckoLib
/// @param bindToActor An optional Boolean override to force the effect to run in world-space, rather than bound to the actor. Not used by GeckoLib
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_actor_animation_1.8.0?view=minecraft-bedrock-stable">Bedrock Actor Animation Spec 1.8.0</a>
@ApiStatus.Internal
public record ActorAnimationSoundEffect(String effect, @Nullable String locator, @Nullable String preEffectScript, @Nullable Boolean bindToActor) {
    /// Parse an ActorAnimationSoundEffect instance from raw .json input via [Gson]
    public static JsonDeserializer<ActorAnimationSoundEffect> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final String effect = GsonHelper.getAsString(obj, "effect");
            final String locator = GsonHelper.getAsString(obj, "locator", null);
            final String preEffect = GsonHelper.getAsString(obj, "pre_effect_script", null);
            final Boolean bindToActor = JsonUtil.getOptionalBoolean(obj, "bind_to_actor");

            return new ActorAnimationSoundEffect(effect, locator, preEffect, bindToActor);
        };
    }

    /// Bake this `ActorAnimationSoundEffect` instance into the final [SoundKeyframeData] instance that GeckoLib uses for animating
    public SoundKeyframeData bake(double timestamp) {
        return new SoundKeyframeData(timestamp, this.effect, this.locator);
    }
}
