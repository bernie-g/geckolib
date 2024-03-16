/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.example.registry.*;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.network.GeckoLibNetwork;

@Mod(GeckoLibConstants.MODID)
public final class GeckoLibMod {

	public GeckoLibMod() {
		if (FMLEnvironment.dist.isClient())
			GeckoLibCache.registerReloadListener();

		GeckoLibNetwork.init();

		if (GeckoLibConstants.shouldRegisterExamples()) {
			IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

			EntityRegistry.ENTITIES.register(bus);
			ItemRegistry.ITEMS.register(bus);
			ItemRegistry.TABS.register(bus);
			BlockEntityRegistry.TILES.register(bus);
			BlockRegistry.BLOCKS.register(bus);
			SoundRegistry.SOUNDS.register(bus);
		}
	}
}
