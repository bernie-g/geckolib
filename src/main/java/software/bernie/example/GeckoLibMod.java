/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.example.registry.*;
import software.bernie.geckolib3.GeckoLib;

@Mod(GeckoLib.ModID)
public class GeckoLibMod
{
	public static ItemGroup geckolibItemGroup;

	public GeckoLibMod()
	{
		GeckoLib.initialize();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::onCommonSetup);
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

	public static AttributeModifierMap createGenericEntityAttributes()
	{
		return AttributeModifierMap.func_233803_a_().
				func_233814_a_(Attributes.field_233818_a_).
				func_233814_a_(Attributes.field_233819_b_).
				func_233814_a_(Attributes.field_233820_c_).
				func_233814_a_(Attributes.field_233821_d_).
				func_233814_a_(Attributes.field_233822_e_).
				func_233814_a_(Attributes.field_233823_f_).
				func_233814_a_(Attributes.field_233824_g_).
				func_233814_a_(Attributes.field_233825_h_).
				func_233814_a_(Attributes.field_233826_i_).
				func_233814_a_(Attributes.field_233827_j_).
				func_233814_a_(Attributes.field_233828_k_).
				func_233814_a_(Attributes.field_233829_l_).
				func_233814_a_(Attributes.field_233830_m_).
				func_233814_a_(ForgeMod.SWIM_SPEED.get()).
				func_233814_a_(ForgeMod.REACH_DISTANCE.get()).
				func_233814_a_(ForgeMod.NAMETAG_DISTANCE.get()).
				func_233814_a_(ForgeMod.ENTITY_GRAVITY.get()).func_233813_a_();
	}

	public void onCommonSetup(FMLCommonSetupEvent event)
	{
		GlobalEntityTypeAttributes.put(EntityRegistry.GEO_EXAMPLE_ENTITY.get(), createGenericEntityAttributes());
		GlobalEntityTypeAttributes.put(EntityRegistry.BIKE_ENTITY.get(), createGenericEntityAttributes());
	}
}
