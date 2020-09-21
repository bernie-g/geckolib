/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib.listener.ClientListener;
import software.bernie.geckolib.resource.ResourceListener;

@Mod(GeckoLib.ModID)
public class GeckoLib
{
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String ModID = "geckolib";

	public GeckoLib()
	{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.register(ClientListener.class);
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ResourceListener::registerReloadListener);
		EntityRegistry.ENTITIES.register(bus);
		ItemRegistry.ITEMS.register(bus);
		TileRegistry.TILES.register(bus);
		BlockRegistry.BLOCKS.register(bus);
		MinecraftForge.EVENT_BUS.register(this);
	}
}
