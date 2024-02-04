package software.bernie.example.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.GeckoLibConstants;

public final class SoundRegistry {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT,
			GeckoLibConstants.MODID);

	public static DeferredHolder<SoundEvent, SoundEvent> JACK_MUSIC = SOUNDS.register("jack_in_the_box_music",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GeckoLibConstants.MODID, "jack_in_the_box_music")));
}
