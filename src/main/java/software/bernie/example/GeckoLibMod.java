/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.example.registry.*;
import software.bernie.geckolib3.GeckoLib;

public class GeckoLibMod implements ModInitializer
{
	public static boolean DISABLE_IN_DEV = false;
	
	public static ItemGroup geckolibItemGroup;

	@Override
	public void onInitialize()
	{
		GeckoLib.initialize();
		if (FabricLoader.INSTANCE.isDevelopmentEnvironment() && !GeckoLibMod.DISABLE_IN_DEV)
		{
			new EntityRegistry();
			FabricDefaultAttributeRegistry.register(EntityRegistry.GEO_EXAMPLE_ENTITY, EntityUtils.createGenericEntityAttributes());
			FabricDefaultAttributeRegistry.register(EntityRegistry.BIKE_ENTITY, EntityUtils.createGenericEntityAttributes());
			new ItemRegistry();
			new TileRegistry();
			new BlockRegistry();
			new SoundRegistry();
			geckolibItemGroup = FabricItemGroupBuilder.create(new Identifier(GeckoLib.ModID, "geckolib_examples"))
			.icon(() -> new ItemStack(ItemRegistry.JACK_IN_THE_BOX))
			.build();
		}
	}
}
