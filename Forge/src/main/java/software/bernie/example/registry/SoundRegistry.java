package software.bernie.example.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.GeckoLib;

public final class SoundRegistry {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
			GeckoLib.MOD_ID);

	public static RegistryObject<SoundEvent> JACK_MUSIC = SOUNDS.register("jack_in_the_box_music",
			() -> new SoundEvent(new ResourceLocation(GeckoLib.MOD_ID, "jack_in_the_box_music")));
}
