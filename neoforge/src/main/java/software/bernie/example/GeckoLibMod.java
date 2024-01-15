/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import software.bernie.example.registry.*;
import software.bernie.geckolib.GeckoLib;

@Mod(GeckoLib.MOD_ID)
public final class GeckoLibMod {
	public static final String DISABLE_EXAMPLES_PROPERTY_KEY = "geckolib.disable_examples";

	public GeckoLibMod(IEventBus modBus) {
		GeckoLib.initialize(modBus);

		if (shouldRegisterExamples()) {
			EntityRegistry.ENTITIES.register(modBus);
			ItemRegistry.ITEMS.register(modBus);
			ItemRegistry.TABS.register(modBus);
			BlockEntityRegistry.TILES.register(modBus);
			BlockRegistry.BLOCKS.register(modBus);
			SoundRegistry.SOUNDS.register(modBus);
		}
	}

	/**
	 * By default, GeckoLib will register and activate several example entities,
	 * items, and blocks when in dev.<br>
	 * These examples are <u>not</u> present when in a production environment
	 * (normal players).<br>
	 * This can be disabled by setting the
	 * {@link GeckoLibMod#DISABLE_EXAMPLES_PROPERTY_KEY} to false in your run args
	 */
	static boolean shouldRegisterExamples() {
		return !FMLEnvironment.production && !Boolean.getBoolean(DISABLE_EXAMPLES_PROPERTY_KEY);
	}
}
