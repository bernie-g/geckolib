/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.example.registry.*;
import software.bernie.geckolib.GeckoLib;

@EventBusSubscriber
@Mod(GeckoLib.MOD_ID)
public final class GeckoLibMod {
	public static final String DISABLE_EXAMPLES_PROPERTY_KEY = "geckolib.disable_examples";

	public GeckoLibMod() {
		GeckoLib.initialize();

		if (shouldRegisterExamples()) {
			IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

			EntityRegistry.ENTITIES.register(bus);
			ItemRegistry.ITEMS.register(bus);
			BlockEntityRegistry.TILES.register(bus);
			BlockRegistry.BLOCKS.register(bus);
			SoundRegistry.SOUNDS.register(bus);
			bus.addListener(this::addCreativeTabs);
		}
	}

	public void addCreativeTabs(final CreativeModeTabEvent.Register event) {
		event.registerCreativeModeTab(new ResourceLocation(GeckoLib.MOD_ID, "geckolib_examples"),
				e -> e.icon(() -> new ItemStack(ItemRegistry.JACK_IN_THE_BOX.get()))
						.title(Component.translatable("itemGroup." + GeckoLib.MOD_ID + ".geckolib_examples"))
							.displayItems((enabledFeatures, entries, operatorEnabled) -> {
				                entries.accept(ItemRegistry.JACK_IN_THE_BOX.get());
				                entries.accept(ItemRegistry.PISTOL.get());
				                entries.accept(ItemRegistry.GECKO_ARMOR_HELMET.get());
				                entries.accept(ItemRegistry.GECKO_ARMOR_CHESTPLATE.get());
				                entries.accept(ItemRegistry.GECKO_ARMOR_LEGGINGS.get());
				                entries.accept(ItemRegistry.GECKO_ARMOR_BOOTS.get());
				                entries.accept(ItemRegistry.WOLF_ARMOR_HELMET.get());
				                entries.accept(ItemRegistry.WOLF_ARMOR_CHESTPLATE.get());
				                entries.accept(ItemRegistry.WOLF_ARMOR_LEGGINGS.get());
				                entries.accept(ItemRegistry.WOLF_ARMOR_BOOTS.get());
				                entries.accept(ItemRegistry.GECKO_HABITAT.get());
				                entries.accept(ItemRegistry.FERTILIZER.get());
				                entries.accept(ItemRegistry.BAT_SPAWN_EGG.get());
				                entries.accept(ItemRegistry.BIKE_SPAWN_EGG.get());
				                entries.accept(ItemRegistry.RACE_CAR_SPAWN_EGG.get());
				                entries.accept(ItemRegistry.PARASITE_SPAWN_EGG.get());
				                entries.accept(ItemRegistry.MUTANT_ZOMBIE_SPAWN_EGG.get());
				                entries.accept(ItemRegistry.GREMLIN_SPAWN_EGG.get());
				                entries.accept(ItemRegistry.FAKE_GLASS_SPAWN_EGG.get());
				                entries.accept(ItemRegistry.COOL_KID_SPAWN_EGG.get());
				}));
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
