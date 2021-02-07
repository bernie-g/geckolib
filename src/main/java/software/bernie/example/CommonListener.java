package software.bernie.example;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonListener {
	@SubscribeEvent
	public static void registerEntityAttributes(FMLCommonSetupEvent event) {
		if (!FMLEnvironment.production && !GeckoLibMod.DISABLE_IN_DEV) {
			event.enqueueWork(() -> {
				// TODO: Update these with new mappings
				GlobalEntityTypeAttributes.put(EntityRegistry.BIKE_ENTITY.get(),
						MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 1.0D).create());
				GlobalEntityTypeAttributes.put(EntityRegistry.GEO_EXAMPLE_ENTITY.get(),
						MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 1.0D).create());
			});
		}
	}
}
