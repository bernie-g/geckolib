/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.listener;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.client.renderer.entity.*;
import software.bernie.geckolib.example.registry.Entities;
import software.bernie.geckolib.registry.CommandRegistry;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBus
{
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerEntityRenderers(final FMLClientSetupEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(Entities.STING_RAY.get(), manager -> new StingrayRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(Entities.ASCENDED_LEG_FISH.get(), manager -> new AscendedLegfishRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(Entities.TIGRIS.get(), manager -> new TigrisRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(Entities.LIGHTCRYSTAL.get(), manager -> new LightCrystalRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(Entities.BROWN.get(), manager -> new BrownRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(Entities.EASING_DEMO.get(), manager -> new EasingDemoRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(Entities.ROBOT.get(), manager -> new RobotRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(Entities.COLORFUL_PIG.get(), manager -> new ColorfulPigRenderer(manager));

	}
}
