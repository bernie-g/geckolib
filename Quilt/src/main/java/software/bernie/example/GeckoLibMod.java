/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3q.GeckoLib;

public class GeckoLibMod implements ModInitializer {
	public static boolean DISABLE_IN_DEV = false;
	boolean isDevelopmentEnvironment = QuiltLoader.isDevelopmentEnvironment();

	@Override
	public void onInitialize(ModContainer mod) {
		GeckoLib.initialize();
		if (isDevelopmentEnvironment && !GeckoLibMod.DISABLE_IN_DEV) {
			new EntityRegistry();
			FabricDefaultAttributeRegistry.register(EntityRegistry.GEO_EXAMPLE_ENTITY,
					EntityUtils.createGenericEntityAttributes());
			FabricDefaultAttributeRegistry.register(EntityRegistry.EXTENDED_RENDERER_EXAMPLE,
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
