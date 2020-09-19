/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib.GeckoLib;
import software.bernie.example.client.renderer.entity.*;
import software.bernie.example.registry.EntityRegistry;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBus
{
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerEntityRenderers(final FMLClientSetupEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.STING_RAY.get(), manager -> new StingrayRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.ASCENDED_LEG_FISH.get(), manager -> new AscendedLegfishRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.TIGRIS.get(), manager -> new TigrisRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.LIGHTCRYSTAL.get(), manager -> new LightCrystalRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BROWN.get(), manager -> new BrownRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.EASING_DEMO.get(), manager -> new EasingDemoRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.ROBOT.get(), manager -> new RobotRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.COLORFUL_PIG.get(), manager -> new ColorfulPigRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BAT.get(), manager -> new BatRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BOTARIUM_TEST_ENTITY.get(), manager -> new EntityBotariumRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.GEO_EXAMPLE_ENTITY.get(), manager -> new ExampleGeoRenderer(manager));
	}
}
