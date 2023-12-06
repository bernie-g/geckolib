package software.bernie.example.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import software.bernie.geckolib.GeckoLib;

public final class SoundRegistry {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT,
			GeckoLib.MOD_ID);

	public static DeferredHolder<SoundEvent, SoundEvent> JACK_MUSIC = SOUNDS.register("jack_in_the_box_music",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GeckoLib.MOD_ID, "jack_in_the_box_music")));
}
