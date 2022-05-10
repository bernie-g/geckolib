/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.LERenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.example.entity.GeoExampleEntityLayer;
import software.bernie.example.entity.ReplacedCreeperEntity;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;

@Mod(modid = GeckoLib.ModID, version = GeckoLib.VERSION)
public class GeckoLibMod {
	public static boolean DISABLE_IN_DEV = false;
	private static CreativeTabs geckolibItemGroup;
	private boolean deobfuscatedEnvironment;

	public static CreativeTabs getGeckolibItemGroup() {
		if (geckolibItemGroup == null) {
			geckolibItemGroup = new CreativeTabs(CreativeTabs.getNextID(), "geckolib_examples") {
				@Override
				public ItemStack getTabIconItem() {
					return new ItemStack(ItemRegistry.JACK_IN_THE_BOX);
				}
			};
		}

		return geckolibItemGroup;
	}

	public GeckoLibMod() {
		deobfuscatedEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
		if (deobfuscatedEnvironment && !DISABLE_IN_DEV) {
			MinecraftForge.EVENT_BUS.register(new CommonListener());
		}
	}

	@SideOnly(Side.CLIENT)
	@Mod.EventHandler
	public void registerRenderers(FMLPreInitializationEvent event) {
		if (deobfuscatedEnvironment && !DISABLE_IN_DEV) {
			RenderingRegistry.registerEntityRenderingHandler(GeoExampleEntityLayer.class,
					LERenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(GeoExampleEntity.class, ExampleGeoRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(BikeEntity.class, BikeGeoRenderer::new);

			GeoArmorRenderer.registerArmorRenderer(PotatoArmorItem.class, new PotatoArmorRenderer());

			ClientRegistry.bindTileEntitySpecialRenderer(BotariumTileEntity.class, new BotariumTileRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(FertilizerTileEntity.class, new FertilizerTileRenderer());
		}
	}

	@SideOnly(Side.CLIENT)
	@Mod.EventHandler
	public void registerReplacedRenderers(FMLInitializationEvent event) {
		if (deobfuscatedEnvironment && !DISABLE_IN_DEV) {
			GeckoLib.initialize();
			RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
			ReplacedCreeperRenderer creeperRenderer = new ReplacedCreeperRenderer(renderManager);
			renderManager.entityRenderMap.put(EntityCreeper.class, creeperRenderer);
			GeoReplacedEntityRenderer.registerReplacedEntity(ReplacedCreeperEntity.class, creeperRenderer);
		}
	}
}
