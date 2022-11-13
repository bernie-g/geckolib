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
import software.bernie.example.client.renderer.block.FertilizerBlockRenderer;
import software.bernie.example.client.renderer.block.HabitatBlockRenderer;
import software.bernie.example.client.renderer.entity.*;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod.EventBusSubscriber(modid = GeckoLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientListener {

	@SubscribeEvent
	public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		if (GeckoLibMod.shouldRegisterExamples()) {
			event.registerEntityRenderer(EntityRegistry.GEO_EXAMPLE_ENTITY.get(), BatRenderer::new);
			event.registerEntityRenderer(EntityRegistry.BIKE_ENTITY.get(), BikeRenderer::new);
			event.registerEntityRenderer(EntityRegistry.CAR_ENTITY.get(), RaceCarRenderer::new);
			event.registerEntityRenderer(EntityRegistry.PARASITE.get(), ParasiteRenderer::new);
			event.registerEntityRenderer(EntityRegistry.GEOLAYERENTITY.get(), LERenderer::new);
			//event.registerEntityRenderer(EntityRegistry.EXTENDED_RENDERER_EXAMPLE.get(), ExampleExtendedRendererEntityRenderer::new);
			//event.registerEntityRenderer(EntityRegistry.TEXTURE_PER_BONE_EXAMPLE.get(), TexturePerBoneTestEntityRenderer::new);

			event.registerEntityRenderer(EntityType.CREEPER, ReplacedCreeperRenderer::new);

			event.registerBlockEntityRenderer(TileRegistry.HABITAT_TILE.get(), context -> new HabitatBlockRenderer());
			event.registerBlockEntityRenderer(TileRegistry.FERTILIZER.get(), context -> new FertilizerBlockRenderer());
		}
	}

	@SubscribeEvent
	public static void registerRenderers(final FMLClientSetupEvent event) {
		if (GeckoLibMod.shouldRegisterExamples())
			ItemBlockRenderTypes.setRenderLayer(BlockRegistry.HABITAT_BLOCK.get(), RenderType.translucent());
	}
}
