/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib.example.client.renderer.entity.AscendedLegfishRenderer;
import software.bernie.geckolib.example.client.renderer.entity.StingrayRenderer;
import software.bernie.geckolib.example.registry.Entities;
import software.bernie.geckolib.example.client.renderer.entity.TigrisRenderer;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.MOD, value = EnvType.CLIENT)
public class ModEventBus
{
	@Environment(EnvType.CLIENT)
	@SubscribeEvent
	public static void doClientStuff(final FMLClientSetupEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(Entities.STING_RAY.get(), manager -> new StingrayRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(Entities.ASCENDED_LEG_FISH.get(), manager -> new AscendedLegfishRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(Entities.TIGRIS.get(), manager -> new TigrisRenderer(manager));
	}
}
