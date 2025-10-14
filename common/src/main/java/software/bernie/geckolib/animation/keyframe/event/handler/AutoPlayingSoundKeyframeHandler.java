package software.bernie.geckolib.animation.keyframe.event.handler;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.keyframe.event.KeyFrameEvent;
import software.bernie.geckolib.animation.keyframe.event.data.SoundKeyframeData;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.ClientUtil;

/**
 * Built-in helper for a {@link AnimationController.KeyframeEventHandler SoundKeyframeHandler} that automatically plays the sound defined in the keyframe data
 * <p>
 * Due to an inability to determine the position of the sound for all animatables, this handler only supports {@link software.bernie.geckolib.animatable.GeoEntity GeoEntity} and {@link software.bernie.geckolib.animatable.GeoBlockEntity GeoBlockEntity}
 * <p>
 * The expected keyframe data format is one of the below:
 * <pre>{@code
 * namespace:soundid
 * namespace:soundid|volume|pitch
 * }</pre>
 */
public class AutoPlayingSoundKeyframeHandler<A extends GeoAnimatable> implements AnimationController.KeyframeEventHandler<A, SoundKeyframeData> {
    @Override
    public void handle(KeyFrameEvent<A, SoundKeyframeData> event) {
        String[] segments = event.keyframeData().getSound().split("\\|");

        BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.read(segments[0]).getOrThrow()).ifPresent(sound -> {
            Vec3 position = event.animationState().getDataOrDefault(DataTickets.POSITION, event.animationState().renderState() instanceof EntityRenderState entityState ?
                                                                                          new Vec3(entityState.x, entityState.y, entityState.z) : null);
            Class<?> animatableClass = event.animationState().getDataOrDefault(DataTickets.ANIMATABLE_CLASS, Object.class);

            if (position != null) {
                float volume = segments.length > 1 ? Float.parseFloat(segments[1]) : 1;
                float pitch = segments.length > 2 ? Float.parseFloat(segments[2]) : 1;
                SoundSource source = animatableClass.isAssignableFrom(BlockEntity.class) ? SoundSource.BLOCKS :
                                     animatableClass.isAssignableFrom(Enemy.class) ? SoundSource.HOSTILE : SoundSource.NEUTRAL;

                ClientUtil.getLevel().playLocalSound(position.x, position.y, position.z, sound.value(), source, volume, pitch, false);
            }
            else {
                GeckoLibConstants.LOGGER.warn("Found sound keyframe handler, but AnimationState had no position data for animatable: {}", animatableClass.getName());
            }
        });
    }
}
