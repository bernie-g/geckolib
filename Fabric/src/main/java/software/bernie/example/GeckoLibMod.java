/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import software.bernie.example.registry.*;
import software.bernie.geckolib3.GeckoLib;

public class GeckoLibMod implements ModInitializer {
	public static boolean DISABLE_IN_DEV = false;
	boolean isDevelopmentEnvironment = FabricLoader.getInstance().isDevelopmentEnvironment();

	@Override
	public void onInitialize() {
		GeckoLib.initialize();
		if (isDevelopmentEnvironment && !GeckoLibMod.DISABLE_IN_DEV) {
			new EntityRegistry();
			FabricDefaultAttributeRegistry.register(EntityRegistry.GEO_EXAMPLE_ENTITY,
					EntityUtils.createGenericEntityAttributes());
			FabricDefaultAttributeRegistry.register(EntityRegistry.BIKE_ENTITY,
					EntityUtils.createGenericEntityAttributes());
			FabricDefaultAttributeRegistry.register(EntityRegistry.GEOLAYERENTITY,
					EntityUtils.createGenericEntityAttributes());
			new ItemRegistry();
			new TileRegistry();
			new BlockRegistry();
			new SoundRegistry();
		}
	}
}
