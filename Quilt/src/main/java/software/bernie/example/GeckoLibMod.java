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
	/**
	 * When set to true, prevents examples from being registered.
	 *
	 * @deprecated due to mod loading order, setting this in your mod may not have an effect.
	 * Use the {@link #DISABLE_EXAMPLES_PROPERTY_KEY system property} instead.
	 */
	@Deprecated(since = "3.1.21")
	public static boolean DISABLE_IN_DEV = false;
	public static final String DISABLE_EXAMPLES_PROPERTY_KEY = "geckolib.disable_examples";
	private static final boolean isDevelopmentEnvironment = QuiltLoader.isDevelopmentEnvironment();

	@Override
	public void onInitialize(ModContainer mod) {
		GeckoLib.initialize();
		if (shouldRegisterExamples()) {
			new EntityRegistry();
			FabricDefaultAttributeRegistry.register(EntityRegistry.GEO_EXAMPLE_ENTITY,
					EntityUtils.createGenericEntityAttributes());
			FabricDefaultAttributeRegistry.register(EntityRegistry.EXTENDED_RENDERER_EXAMPLE,
					EntityUtils.createGenericEntityAttributes());
			FabricDefaultAttributeRegistry.register(EntityRegistry.BIKE_ENTITY,
					EntityUtils.createGenericEntityAttributes());
			FabricDefaultAttributeRegistry.register(EntityRegistry.CAR_ENTITY,
					EntityUtils.createGenericEntityAttributes());
			FabricDefaultAttributeRegistry.register(EntityRegistry.GEOLAYERENTITY,
					EntityUtils.createGenericEntityAttributes());
			FabricDefaultAttributeRegistry.register(EntityRegistry.TEXTURE_PER_BONE_EXAMPLE,
					EntityUtils.createGenericEntityAttributes());
			new ItemRegistry();
			new TileRegistry();
			new BlockRegistry();
			new SoundRegistry();
		}
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
