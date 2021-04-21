/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import software.bernie.example.network.GeckoLibModNetwork;
import software.bernie.example.registry.*;
import software.bernie.geckolib3.GeckoLib;

@Mod(GeckoLib.ModID)
public class GeckoLibMod {
	public static ItemGroup geckolibItemGroup;
	public static boolean DISABLE_IN_DEV = false;

	public GeckoLibMod() {
		GeckoLib.initialize();
		if (!FMLEnvironment.production && !DISABLE_IN_DEV) {
			IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
			EntityRegistry.ENTITIES.register(bus);
			ItemRegistry.ITEMS.register(bus);
			TileRegistry.TILES.register(bus);
			BlockRegistry.BLOCKS.register(bus);
			SoundRegistry.SOUNDS.register(bus);
			geckolibItemGroup = new ItemGroup(ItemGroup.getGroupCountSafe(), "geckolib_examples") {
				@Override
				public ItemStack makeIcon() {
					return new ItemStack(ItemRegistry.JACK_IN_THE_BOX.get());
				}
			};
			GeckoLibModNetwork.register();
		}
	}
}
