/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.controller.AnimationController.ModelFetcher;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@EventBusSubscriber
@Mod(GeckoLib.ModID)
public class GeckoLibMod {
	public static CreativeModeTab geckolibItemGroup;
	/**
	 * When set to true, prevents examples from being registered.
	 *
	 * @deprecated due to mod loading order, setting this in your mod may not have an effect.
	 * Use the {@link #DISABLE_EXAMPLES_PROPERTY_KEY system property} instead.
	 */
	@Deprecated(since = "3.0.40")
	public static boolean DISABLE_IN_DEV = false;
	public static final String DISABLE_EXAMPLES_PROPERTY_KEY = "geckolib.disable_examples";
	private static final boolean isDevelopmentEnvironment = !FMLEnvironment.production;

	public GeckoLibMod() {
		GeckoLib.initialize();
		if (shouldRegisterExamples()) {
			IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
			EntityRegistry.ENTITIES.register(bus);
			ItemRegistry.ITEMS.register(bus);
			TileRegistry.TILES.register(bus);
			BlockRegistry.BLOCKS.register(bus);
			SoundRegistry.SOUNDS.register(bus);
			geckolibItemGroup = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), "geckolib_examples") {
				@Override
				public ItemStack makeIcon() {
					return new ItemStack(ItemRegistry.JACK_IN_THE_BOX.get());
				}
			};
		}
	}

	@SubscribeEvent
	public static void onEntityRemoved(EntityLeaveWorldEvent event) {
		if (event.getEntity() == null) {
			return;
		}
		if (event.getEntity().getUUID() == null) {
			return;
		}
		if (event.getWorld().isClientSide)
			GeoArmorRenderer.LIVING_ENTITY_RENDERERS.values().forEach(instances -> {
				if (instances.containsKey(event.getEntity().getUUID())) {
					ModelFetcher<?> beGone = instances.get(event.getEntity().getUUID());
					AnimationController.removeModelFetcher(beGone);
					instances.remove(event.getEntity().getUUID());
				}
			});
	}

	/**
	 * Returns whether examples are to be registered. Examples are registered when:
	 * <ul>
	 *     <li>The mod is running in a development environment; <em>and</em></li>
	 *     <li>{@link #DISABLE_IN_DEV} is not set to true; <em>and</em></li>
	 *     <li>the system property defined by {@link #DISABLE_EXAMPLES_PROPERTY_KEY} is not set to "true".</li>
	 * </ul>
	 *
	 * @return whether the examples are to be registered
	 */
	static boolean shouldRegisterExamples() {
		return isDevelopmentEnvironment && !DISABLE_IN_DEV && !Boolean.getBoolean(DISABLE_EXAMPLES_PROPERTY_KEY);
	}
}
