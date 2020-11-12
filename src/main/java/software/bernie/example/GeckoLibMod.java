/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod(modid = GeckoLib.ModID)
public class GeckoLibMod
{
	public static CreativeTabs geckolibItemGroup;

	public GeckoLibMod()
	{
		GeckoLib.initialize();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		EntityRegistry.ENTITIES.register(bus);
		ItemRegistry.ITEMS.register(bus);
		TileRegistry.TILES.register(bus);
		BlockRegistry.BLOCKS.register(bus);
		SoundRegistry.SOUNDS.register(bus);
		geckolibItemGroup = new CreativeTabs(0, "geckolib_examples")
		{
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(ItemRegistry.JACK_IN_THE_BOX.get());
			}
		};

	}
}
