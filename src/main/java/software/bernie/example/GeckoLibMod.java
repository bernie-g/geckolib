/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod(modid = GeckoLib.ModID)
public class GeckoLibMod
{
	private static CreativeTabs geckolibItemGroup;

	public static CreativeTabs getGeckolibItemGroup()
	{
		if (geckolibItemGroup == null)
		{
			geckolibItemGroup = new CreativeTabs(0, "geckolib_examples")
			{
				@Override
				public ItemStack getTabIconItem()
				{
					return new ItemStack(ItemRegistry.JACK_IN_THE_BOX);
				}
			};
		}

		return geckolibItemGroup;
	}

	public GeckoLibMod()
	{
		GeckoLib.initialize();
	}

}
