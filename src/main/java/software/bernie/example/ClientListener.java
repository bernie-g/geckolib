/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.renderer.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderer.geo.GeoItemRenderer;

public class ClientListener implements ClientModInitializer {

	public static void registerClientPackets() {

		// Server --> Client
		ClientPlayNetworking.registerGlobalReceiver(GeckoLibNetwork.SYNCABLE,ClientListener::handleSyncPacket);
	}

	public static void handleSyncPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

		final String key = buf.readString(32767); // The max length here can be removed in 1.17+
		final int id = buf.readVarInt();
		final int state = buf.readVarInt();

		final ISyncable syncable = GeckoLibNetwork.getSyncable(key);
		if (syncable != null) {
			syncable.onAnimationSync(id, state);
		} else {
			GeckoLib.LOGGER.warn("Syncable on the server is missing on the client for " + key);
		}
	}

	@Override
	public void onInitializeClient() {
		registerClientPackets();
		if (FabricLoader.getInstance().isDevelopmentEnvironment() && !GeckoLibMod.DISABLE_IN_DEV) {
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.GEO_EXAMPLE_ENTITY,
					(entityRenderDispatcher, context) -> new ExampleGeoRenderer(entityRenderDispatcher));
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.BIKE_ENTITY,
					(entityRenderDispatcher, context) -> new BikeGeoRenderer(entityRenderDispatcher));
			GeoItemRenderer.registerItemRenderer(ItemRegistry.JACK_IN_THE_BOX, new JackInTheBoxRenderer());
			GeoArmorRenderer.registerArmorRenderer(PotatoArmorItem.class, new PotatoArmorRenderer());
			BlockEntityRendererRegistry.INSTANCE.register(TileRegistry.BOTARIUM_TILE, BotariumTileRenderer::new);
			BlockEntityRendererRegistry.INSTANCE.register(TileRegistry.FERTILIZER, FertilizerTileRenderer::new);

			EntityRendererRegistry.INSTANCE.register(EntityType.CREEPER,
					(entityRenderDispatcher, context) -> new ReplacedCreeperRenderer(entityRenderDispatcher));

			BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.BOTARIUM_BLOCK, RenderLayer.getCutout());
		}
	}
}
