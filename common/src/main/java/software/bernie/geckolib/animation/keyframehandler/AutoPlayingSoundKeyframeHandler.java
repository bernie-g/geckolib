package software.bernie.geckolib.animation.keyframehandler;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.state.KeyFrameEvent;
import software.bernie.geckolib.cache.animation.keyframeevent.SoundKeyframeData;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.ClientUtil;

/// Built-in helper for a [SoundKeyframeHandler][AnimationController.KeyframeEventHandler] that automatically plays the sound defined in the keyframe data
///
/// Due to an inability to determine the position of the sound for all animatables, this handler only supports [GeoEntity][software.bernie.geckolib.animatable.GeoEntity] and [GeoBlockEntity][software.bernie.geckolib.animatable.GeoBlockEntity]
///
/// The expected keyframe data format is one of the below:
/// <pre>
/// `namespace:soundidnamespace:soundid|volume|pitch`</pre>
///
/// @param <A> Animatable class type
public class AutoPlayingSoundKeyframeHandler<A extends GeoAnimatable> implements AnimationController.KeyframeEventHandler<A, SoundKeyframeData> {
    @Override
    public void handle(KeyFrameEvent<A, SoundKeyframeData> event) {
        final Level level = ClientUtil.getLevel();

        if (level == null)
            return;

        String[] segments = event.keyframeData().getSound().split("\\|");

        BuiltInRegistries.SOUND_EVENT.get(Identifier.read(segments[0]).getOrThrow()).ifPresent(sound -> {
            Vec3 position = event.renderState().getOrDefaultGeckolibData(DataTickets.POSITION, event.renderState() instanceof EntityRenderState entityState ?
                                                                                          new Vec3(entityState.x, entityState.y, entityState.z) : null);
            Class<? extends GeoAnimatable> animatableClass = event.renderState().getOrDefaultGeckolibData(DataTickets.ANIMATABLE_CLASS, GeoAnimatable.class);

            if (position != null) {
                float volume = segments.length > 1 ? Float.parseFloat(segments[1]) : 1;
                float pitch = segments.length > 2 ? Float.parseFloat(segments[2]) : 1;
                SoundSource source = animatableClass.isAssignableFrom(BlockEntity.class) ? SoundSource.BLOCKS :
                                     animatableClass.isAssignableFrom(Enemy.class) ? SoundSource.HOSTILE : SoundSource.NEUTRAL;

                level.playLocalSound(position.x, position.y, position.z, sound.value(), source, volume, pitch, false);
            }
            else {
                GeckoLibConstants.LOGGER.warn("Found sound keyframe handler, but AnimationState had no position data for animatable: {}", animatableClass.getName());
            }
        });
    }
}
