/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import software.bernie.example.registry.*;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.network.GeckoLibNetwork;

@Mod(GeckoLibConstants.MODID)
public final class GeckoLibMod {

	public GeckoLibMod(IEventBus modBus) {
		if (FMLEnvironment.dist.isClient())
			GeckoLibCache.registerReloadListener();

		GeckoLibNetwork.init(modBus);

		if (GeckoLibConstants.shouldRegisterExamples()) {
			EntityRegistry.ENTITIES.register(modBus);
			ItemRegistry.ITEMS.register(modBus);
			ItemRegistry.TABS.register(modBus);
			BlockEntityRegistry.TILES.register(modBus);
			BlockRegistry.BLOCKS.register(modBus);
			SoundRegistry.SOUNDS.register(modBus);
		}
	}
}
