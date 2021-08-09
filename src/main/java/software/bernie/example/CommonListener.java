package software.bernie.example;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonListener {
	@SubscribeEvent
	public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		if (!FMLEnvironment.production && !GeckoLibMod.DISABLE_IN_DEV) {
			event.put(EntityRegistry.BIKE_ENTITY.get(),
					MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0D).build());
			event.put(EntityRegistry.GEO_EXAMPLE_ENTITY.get(),
					MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0D).build());
		}
	}
}
