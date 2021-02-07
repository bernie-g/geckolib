package software.bernie.example.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.GeckoLib;

public class SoundRegistry {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
			GeckoLib.ModID);

	public static RegistryObject<SoundEvent> JACK_MUSIC = SOUNDS.register("jack_music",
			() -> new SoundEvent(new ResourceLocation(GeckoLib.ModID, "jack_music")));
}
