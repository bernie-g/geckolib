package software.bernie.geckolib.animation.keyframe.event.builtin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.util.ClientUtil;

/**
 * Built-in helper for a {@link software.bernie.geckolib.animation.AnimationController.SoundKeyframeHandler SoundKeyframeHandler} that automatically plays the sound defined in the keyframe data
 * <p>
 * Due to an inability to determine the position of the sound for all animatables, this handler only supports {@link software.bernie.geckolib.animatable.GeoEntity GeoEntity} and {@link software.bernie.geckolib.animatable.GeoBlockEntity GeoBlockEntity}
 * <p>
 * The expected keyframe data format is one of the below:
 * <pre>{@code
 * namespace:soundid
 * namespace:soundid|volume|pitch
 * }</pre>
 */
public class AutoPlayingSoundKeyframeHandler<A extends GeoAnimatable> implements AnimationController.SoundKeyframeHandler<A> {
    @Override
    public void handle(SoundKeyframeEvent<A> event) {
        String[] segments = event.getKeyframeData().getSound().split("\\|");
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(segments[0]));

        if (sound != null) {
            Entity entity = event.getAnimatable() instanceof Entity e ? e : null;
            Vec3 position = entity != null ? entity.position() : event.getAnimatable() instanceof BlockEntity blockEntity ? blockEntity.getBlockPos().getCenter() : null;

            if (position != null) {
                float volume = segments.length > 1 ? Float.parseFloat(segments[1]) : 1;
                float pitch = segments.length > 2 ? Float.parseFloat(segments[2]) : 1;
                SoundSource source = entity == null ? SoundSource.BLOCKS : entity instanceof Enemy ? SoundSource.HOSTILE : SoundSource.NEUTRAL;

                ClientUtil.getLevel().playSound(null, position.x, position.y, position.z, sound, source, volume, pitch);
            }
        }
    }
}
