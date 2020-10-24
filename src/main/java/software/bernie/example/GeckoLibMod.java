/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.example.registry.*;
import software.bernie.geckolib.GeckoLib;

@Mod(GeckoLib.ModID)
public class GeckoLibMod
{
	public static ItemGroup geckolibItemGroup;

	public GeckoLibMod()
	{
		GeckoLib.initialize();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		EntityRegistry.ENTITIES.register(bus);
		ItemRegistry.ITEMS.register(bus);
		TileRegistry.TILES.register(bus);
		BlockRegistry.BLOCKS.register(bus);
		SoundRegistry.SOUNDS.register(bus);
		geckolibItemGroup = new ItemGroup(0, "geckolib_examples")
		{
			@Override
			public ItemStack createIcon()
			{
				return new ItemStack(ItemRegistry.JACK_IN_THE_BOX.get());
			}
		};
	}
}
