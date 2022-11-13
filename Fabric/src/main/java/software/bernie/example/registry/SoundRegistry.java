package software.bernie.example.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import software.bernie.geckolib3.GeckoLib;

public class SoundRegistry {
	public static SoundEvent JACK_MUSIC = Registry.register(BuiltInRegistries.SOUND_EVENT, "jack_music",
			new SoundEvent(new ResourceLocation(GeckoLib.ModID, "jack_music")));
}