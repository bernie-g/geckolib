/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.example.entity.ReplacedCreeperEntity;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID)
public class ClientListener
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(FMLLoadEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(GeoExampleEntity.class, (manager) -> new ExampleGeoRenderer(manager));
		RenderingRegistry.registerEntityRenderingHandler(BikeEntity.class, (manager) -> new BikeGeoRenderer(manager));

		GeoArmorRenderer.registerArmorRenderer(PotatoArmorItem.class, new PotatoArmorRenderer());

		ClientRegistry.bindTileEntitySpecialRenderer(BotariumTileEntity.class, new BotariumTileRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(FertilizerTileEntity.class, new FertilizerTileRenderer());

		RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
		ReplacedCreeperRenderer creeperRenderer = new ReplacedCreeperRenderer(renderManager);
		renderManager.entityRenderMap.put(EntityCreeper.class, creeperRenderer);
		GeoReplacedEntityRenderer.registerReplacedEntity(ReplacedCreeperEntity.class, creeperRenderer);
	}
}
