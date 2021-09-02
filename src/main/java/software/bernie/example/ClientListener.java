/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientListener {

	@SubscribeEvent
	public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistry.GEO_EXAMPLE_ENTITY.get(), ExampleGeoRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BIKE_ENTITY.get(), BikeGeoRenderer::new);

		event.registerBlockEntityRenderer(TileRegistry.BOTARIUM_TILE.get(), BotariumTileRenderer::new);
		event.registerBlockEntityRenderer(TileRegistry.FERTILIZER.get(), FertilizerTileRenderer::new);

		event.registerEntityRenderer(EntityType.CREEPER, ReplacedCreeperRenderer::new);
	}

	@SubscribeEvent
	public static void registerRenderers(final EntityRenderersEvent.AddLayers event) {
		if (!FMLEnvironment.production && !GeckoLibMod.DISABLE_IN_DEV) {
			GeoArmorRenderer.registerArmorRenderer(PotatoArmorItem.class, new PotatoArmorRenderer());
		}
	}

	@SubscribeEvent
	public static void registerRenderers(final FMLClientSetupEvent event) {
		if (!FMLEnvironment.production && !GeckoLibMod.DISABLE_IN_DEV) {
			ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BOTARIUM_BLOCK.get(), RenderType.cutout());
		}
	}
}
